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

var gameMasterState;

var PARTICIPANTS_LIST_TASK_PERIOD = 15000, //ms
    participantsListTask;

function showMessagesArea () {
  $("#loading").hide();
  $("#ended").hide();
  $("#log").empty();
  $("#log").show();
  $("#masterName").empty();
  $("#masterName").append(gameMasterState.login);
  $("#exchangeName").empty();
  $("#queueName").empty();
  $("#toolbar").show();
}


function showLoad () {
  $("#log").hide();
  $("#toolbar").hide();
  $("#ended").hide();
  $("#loading").show();
}


function showEnd () {
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
  // start the gameMasterState (consumeLoop, heartbeatTask...)
  gameMasterState.start();
  // start the participant list task
  startParticipantsListTask();
  // publish a join message
  publishToGameLogicServer(gameMasterState, "join.joinMaster", "michel,Tidy-City,Instance-1");
}


/**
 * Callback function triggered by gameMasterState.connectionExit().
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
        publishToGameLogicServer(gameMasterState, "presence.askParticipantsList");
    }, PARTICIPANTS_LIST_TASK_PERIOD);
}


$(document).ready(function() {
    
    // instantiate gameMasterState
    gameMasterState = new State("michel", "simatic", "Tidy-City", "Instance-1");
    // register its own actions
    gameMasterState.listOfActions.myFirstActionKind = new MyFirstActionKind();
    // XML-RPC loggin for Master
    createGameInstance( gameMasterState.login, gameMasterState.password, 
                        gameMasterState.gameName, gameMasterState.instanceName, 
                        function(session){
                            onCreateConnection(session);
                        });
    
    // when the user clicks on the end connection button
    $("#endConnectionButton").click(function () {
        gameMasterState.connectionExit();
        return false;
    });
    
    // when the user clicks on the list instances button
    $("#listInstancesButton").click(function () {
        // XML-RPC termination instance request
        listGameInstances(gameMasterState.gameName, function(data){
            println("Game Instances",data);
        });
        return false;
    });
    
    // when the user clicks on the terminate instance button
    $("#terminateInstanceButton").click(function () {
        if(gameMasterState.exiting === false){
            // XML-RPC termination instance request
            terminateGameInstance(gameMasterState.gameName, gameMasterState.instanceName);
        }
        return false;
    });
    
    //lock the UI while waiting for a response
    showLoad();
});


// If we can, notify the proxy that we're going away to end the amqp connection
$(window).unload(function () {
    gameMasterState.connectionExit();
});