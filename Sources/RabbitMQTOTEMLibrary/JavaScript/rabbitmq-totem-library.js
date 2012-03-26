/**
 TCM: TOTEM Communication Middleware
 Copyright: Copyright (C) 2009-2012
 Contact: denis.conan@telecom-sudparis.eu, michel.simatic@telecom-sudparis.eu

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 3 of the License, or any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 USA

 Developer(s): Denis Conan, Gabriel Adgeg
 */

var DEFAULT_HEARTBEAT_TASK_PERIOD = 5000, //ms
    CONNECTION_TO_BROKER_CLOSED_MESSAGE = "[ERROR] AMQP_CONNECTION_FORCED_CLOSED", // message sent by proxy on forced closed of AMQP connection
    DEFAULT_MAX_RETRY = 60; //Number of seconds before stoping the reconnection routine

var hearbeatTask;

/**
 * Create a state, required to store in game informations and to publish messages.
 * 
 * @param   login       the ID of the user
 * @param   password    the password of the user
 * @param   gameName    the name of the game
 * @param   instanceName the name of the instance to create and/or to join
 * @param   heartbeat   the period (in ms) between each heartbeat messages (optional)
 * @param   maxRetry    the maximum number of retries before stopping the reconnection routine
 **/ 
function State(login, password, gameName, instanceName, heartbeat, maxRetry){
    this.login = login;
    this.password = password;
    this.gameName = gameName;
    this.instanceName = instanceName;
    if (typeof heartbeat == "number"){
        this.heartbeat = heartbeat;
    }else{
        this.heartbeat = DEFAULT_HEARTBEAT_TASK_PERIOD;
    }
    if (typeof maxRetry == "number"){
        this.maxRetry = maxRetry;
    }else{
        this.maxRetry = DEFAULT_MAX_RETRY;
    }
    this.listOfActions = {  join: new JoinAction(),
                            presence: new PresenceAction(),
                            lifecycle: new LifeCycleAction()};
    this.start = function (){
        this.exiting = false;
        // request messages from server
        // MUST NOT acknoledge first message since it won't be forward to the browser
        getMessage(this, false);
        startHeartbeatTask(this);
    };
    this.connectionExit = function(){
        if(this.exiting === false){
            $.get("/endconnection", {nick: this.login}, function (data) { onEndConnection(this, data); }, "json");
        }
    };
}


/** Publish a message to a recipient.
 * 
 * @param   recipient   the login of the required recipient
 * @param   state       the State of the sender
 * @param   action      the action that should be triggered by the recipient 
 *                      on the reception of the message (e.g. "myFirstActionKind.myFirstAction")
 * @param   message     the message (could be null)
 * @return  the jQuery function matching with the publication request.
 *          (could be used to add a callback).
 **/
function publish (recipient, state, action, message) {
    return $.get("/publish",
                    {nick: state.login, recipient: recipient, action: action, message: message},
                    function (data) {},
                    "json");
}


/** Publish a message to the GameLogic server.
 * 
 * @param   recipient   the login of the required recipient
 * @param   state       the State of the sender
 * @param   action      the action that should be triggered by the GameLogic server 
 *                      on the reception of the message (e.g. "myFirstActionKind.myFirstAction")
 * @param   message     the message (could be null)
 * @return  the jQuery function matching with the publication request.
 *          (could be used to add a callback).
 **/
function publishToGameLogicServer (state, action, message) {
    return $.get("/publishtogamelogicserver",
                    {nick: state.login, action: action, message: message},
                    function (data) {},
                    "json");
}


 /** Publish a message to all the recipients, including the GameLogic server.
 * 
 * @param   recipient   the login of the required recipient
 * @param   state       the State of the sender
 * @param   action      the action that should be triggered by all the recipients 
 *                      on the reception of the message (e.g. "myFirstActionKind.myFirstAction")
 * @param   message     the message (could be null)
 * @return  the jQuery function matching with the publication request.
 *          (could be used to add a callback).
 **/
function publishToAll (state, action, message) {
    return $.get("/publishtoall",
                    {nick: state.login, action: action, message: message},
                    function (data) {},
                    "json");
}


/* Ask for a new message.
 * If ackLastMessageReceived is set to true, last message received
 * from the proxy is ack and erased.
 */
function getMessage (state, ackLastMessageReceived) {
    $.ajax({ cache: false,
         type: "GET",
         url: "/getmessage",
         dataType: "json",
         data: { nick: state.login,
                 ackLast: ackLastMessageReceived},
         error: function () {
             if(state.numberOfRetries < state.maxRetry){
                println("[WARNING]", "long poll error, number of connection retries: "+state.numberOfRetries+"...");
                state.numberOfRetries++;
                reconnect (state);
             }else{
                println("[WARNING]", "long poll error, too many retries, ending connection...");
                onEndConnection(state);
             }
             
         },
         success: function (data) {
             state.numberOfRetries = 0;
             longPoll(state, data);
         }
     });
     
    // Reconnection routine, every second 
    function reconnect(state) {
        setTimeout(function() {
            getMessage (state, true);
        }, 1000);    
    }
     
     // Handle the delivery of a message, and request for a new one.
    function longPoll (state, data) {
        if (!state.exiting && data && data.message) {
            if (data.message == CONNECTION_TO_BROKER_CLOSED_MESSAGE){
                onExitConnection();
            }else{
                var split = data.routingKey.split(".");
                var publisher   = split[0];
                var consumer    = split[1];
                var actionKind  = split[2];
                var actionName  = split[3];
                var executed = executeAction(state, publisher, consumer, actionKind, actionName, data.message);
                if(!executed){
                    println("Publisher: "+publisher,"Message: "+data.message+" /action: "+actionKind+"."+actionName);
                }
                //make another request that include ack of the message received
                getMessage(state, true);
            }
        }
    }
    
    
    function executeAction (state, publisher, consumer, actionKind, actionName, message) {
        if(state.listOfActions[actionKind] && typeof state.listOfActions[actionKind][actionName] === 'function'){
            state.listOfActions[actionKind][actionName](state, publisher, consumer, message);
            return true;
        } else{
            return false;   
        }
    }
}


// handle the server's response to our end connection request
function onEndConnection (state, session) {
  if (session && session.error) {
    alert(session.error);
    return;
  }
  // stop the consume loop
  state.exiting = true;
  // stop the sending of heatbeat messages
  if(hearbeatTask){
      clearInterval(hearbeatTask);
  }
  onExitConnection();
}


function startHeartbeatTask (state){
    if(state.heartbeat !== 0){
        // send periodically a heartbeat message.
        hearbeatTask = setInterval(function() {
            publishToGameLogicServer(state, "presence.heartbeat", new Date().toGMTString());
        }, state.heartbeat);    
    }  
}



/**************************************************************************************/
/**************************************************************************************/
/**************** Lobby functions to interact with the Game server ********************/
/**************************************************************************************/
/**************************************************************************************/

 /** Retrieve the list of existing game instances.
 * 
 * @param   gameName    the name of the game for which instances should be listed.
 * @param   callback    the function to execute if the request succeeds,
 *                      the available game instances are stored into the data parameter.
 * 
 * @return  the jQuery function matching with the listInstances request.
 **/
function listGameInstances (gameName, callback) {
    return $.ajax({ cache: false,
               type: "GET",
               url: "/xmlrpc-list-instances",
               dataType: "json",
               data: { gameName: gameName},
               error: function (xhr, ajaxOptions, thrownError){
                  // convert the response to JSON 
                  jsonValue = jQuery.parseJSON( xhr.responseText );
                  alert(jsonValue.error);
               },
               success: function (data) {
                   callback(data);
               }
               });       
}


/** Create and join a game instance.
 *
 * @param   login       the login of the user.
 * @param   password    the password of the user.
 * @param   gameName    the name of the game for which instance should be created.
 * @param   instanceName the name of the instance to create.
 * @param   callback    the function to execute if the request succeeds.
 * 
 * @return  the jQuery function matching with the createInstance request.
 **/
function createAndJoinGameInstance (login, password, gameName, instanceName, callback) {
    return $.ajax({ cache: false,
               type: "GET",
               url: "/xmlrpc-create-and-join-instance",
               dataType: "json",
               data: {  login: login ,
                        password: password,
                        gameName: gameName ,
                        instanceName: instanceName},
               error: function (xhr, ajaxOptions, thrownError){
                  // convert the response to JSON 
                  jsonValue = jQuery.parseJSON( xhr.responseText );
                  alert(jsonValue.error);
               },
               success: function (session) {
                   callback(session);
               }
           });    
}



/** Join an available game instance.
 *
 * @param   login           the login of the spectator.
 * @param   password        the password of the spectator.
 * @param   gameName        the name of the game for which instance should be joined.
 * @param   instanceName    the name of the instance to join.
 * @param   observationKey  the observation key (can be set to null).
 * @param   callback        the function to execute if the request succeeds.
 * 
 * @return  the jQuery function matching with the joinSpectator request.
 **/
function joinGameInstance (login, password, gameName, instanceName, observationKey, callback) {
    var params = {  login: login ,
                    password: password ,
                    gameName: gameName ,
                    instanceName: instanceName};
    if(observationKey !== null){
        params.observationKey = observationKey;
    } 
    return $.ajax({ cache: false,
               type: "GET",
               url: "/xmlrpc-join-instance",
               dataType: "json",
               data: params,
               error: function (xhr, ajaxOptions, thrownError){
                  // convert the response to JSON 
                  jsonValue = jQuery.parseJSON( xhr.responseText );
                  alert(jsonValue.error);
               },
               success: function (session) {
                   callback(session);
               }
               });
}


 /** Terminate a game instance.
 * 
 * @param   gameName    the name of the game for which instance should be terminated.
 * @param   instanceName the name of the instance to terminate.
 * @param   callback    the function to execute if the request succeeds,
 *                      the available game instances are stored into the data parameter.
 * 
 * @return  the jQuery function matching with the terminateInstance request.
 **/
function terminateGameInstance (gameName, instanceName, callback) {
    return $.ajax({ cache: false,
               type: "GET",
               url: "/xmlrpc-terminate-instance",
               dataType: "json",
               data: {  gameName: gameName ,
                        instanceName: instanceName},
               error: function (xhr, ajaxOptions, thrownError){
                  // convert the response to JSON 
                  jsonValue = jQuery.parseJSON( xhr.responseText );
                  alert(jsonValue.error);
               },
               success: function (data) {
                   callback(data);
               }
               });
}