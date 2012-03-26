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

/*  Inspired from client.js of the node chat demo
    available here: https://github.com/ry/node_chat */

var gameLogicState;

var PARTICIPANTS_LIST_TASK_PERIOD = 15000, //ms
    DEFAULT_PWD = "hs78dfPPgz",
    participantsListTask;
    
function showConnect () {
  $("#connect").show();
  $("#log").hide();
  $("#toolbar").hide();
  $("#ended").hide();
  $("#loading").hide();
}

function showMessagesArea () {
  $("#connect").hide();
  $("#loading").hide();
  $("#ended").hide();
  $("#log").empty();
  $("#log").show();
  $("#userName").empty();
  $("#userName").append(gameLogicState.login);
  $("#exchangeName").empty();
  $("#queueName").empty();
  $("#toolbar").show();
}


function showLoad () {
  $("#connect").hide();
  $("#log").hide();
  $("#toolbar").hide();
  $("#ended").hide();
  $("#loading").show();
}


function showEnd () {
  $("#connect").hide();
  $("#log").hide();
  $("#toolbar").hide();
  $("#ended").show();
  $("#loading").hide();
}


function println (action, text) {
  if (text === null)
    return;

  //every message is actually a table with 2 cols:
  //  the queue name,
  //  and the content
  var messageElement = $(document.createElement("table"));
  
  // to handle message style
  messageElement.addClass("message");

  var content;
  content = '<tr>' +
            '  <td class="queue">' + action + '</td>' +
            '  <td class="msg-text">' + text + '</td>' +
            '</tr>';    
  
  messageElement.html(content);

  //the log is the stream that we view
  $("#log").append(messageElement);

  //always view the most recent message when it is added
  window.scrollBy(0, 100000000000000000);
}


// handle the server's response to our create connection request
function onCreateConnection (session) {
  if (session.error) {
    alert(session.error);
    return;
  }
  // update the UI to the text area where messages are displayed
  showMessagesArea();
  // start the gameLogicState (consumeLoop, heartbeatTask...)
  gameLogicState.start();
  // start the participant list task
  startParticipantsListTask();
  // publish a join message
  var content =  gameLogicState.login+",Tidy-City,Instance-1";
  publishToGameLogicServer(gameLogicState, "join.join", content);
}


/**
 * Callback function triggered by gameLogicState.connectionExit().
 **/
function onExitConnection(){
  // stop the sending of messages to ask participants list
  if(participantsListTask){
      clearInterval(participantsListTask);
  }
  // update the display to show the end screen
  showEnd(); 
}

function startParticipantsListTask (){
    // send periodically a message to ask participants list.
    participantsListTask = setInterval(function() {
        publishToGameLogicServer(gameLogicState, "presence.askParticipantsList");
    }, PARTICIPANTS_LIST_TASK_PERIOD);
}


$(document).ready(function() {
    // first screen
    showConnect();
                                           
    // when the user clicks the create and join button
    $("#createButton").click(function () {
        // instantiate gameLogicState
        gameLogicState = new State("PLAYER_A", DEFAULT_PWD, "Tidy-City", "Instance-1");
        // register its own actions
        gameLogicState.listOfActions.myFirstActionKind = new MyFirstActionKind();
        // creation and joining of game instance 
        createAndJoinGameInstance( gameLogicState.login, gameLogicState.password, 
                                    gameLogicState.gameName, gameLogicState.instanceName, 
                                    function(session){
                                        onCreateConnection(session);
                                    });
        //lock the UI while waiting for a response
        showLoad();
        return false;
    });
    
    // when the user clicks the join button
    $("#joinButton").click(function () {
       // instantiate spectatorState with specific heartbeat and maxRetry values (optional)
        gameLogicState = new State("PLAYER_B", DEFAULT_PWD , "Tidy-City", "Instance-1", heartbeat=10000, maxRetry=100);
        gameLogicState.observationKey = "*.*.*.*";
        // register its own actions
        gameLogicState.listOfActions.myFirstActionKind = new MyFirstActionKind();
        // joining of game instance
        joinGameInstance(   gameLogicState.login, gameLogicState.password, 
                            gameLogicState.gameName, gameLogicState.instanceName,gameLogicState.observationKey, 
                            function(session){
                                onCreateConnection(session);
                            });
        //lock the UI while waiting for a response
        showLoad();
        return false;
    });
    
    // when the user clicks on the end connection button
    $("#endConnectionButton").click(function () {
        gameLogicState.connectionExit();
        return false;
    });
    
    // when the user clicks on the list instances button
    $("#listInstancesButton").click(function () {
        // XML-RPC termination instance request
        listGameInstances(gameLogicState.gameName, function(data){
            println("Game Instances",data);
        });
        return false;
    });
    
    // when the user clicks on the terminate instance button
    $("#terminateInstanceButton").click(function () {
        if(gameLogicState.exiting === false){
            // XML-RPC termination instance request
            terminateGameInstance(gameLogicState.gameName, gameLogicState.instanceName);
        }
        return false;
    });
});


// If we can, notify the proxy that we're going away to end the amqp connection
$(window).unload(function () {
    gameLogicState.connectionExit();
});