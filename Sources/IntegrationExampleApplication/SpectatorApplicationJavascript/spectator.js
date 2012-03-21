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

var DEFAULT_PWD = "hs78dfPPgz";

var spectatorState;

function showConnect () {
  $("#connect").show();
  $("#log").hide();
  $("#toolbar").hide();
  $("#ended").hide();
  $("#loading").hide();
  $("#nickInput").focus();
}


function showMessagesArea () {
  $("#connect").hide();
  $("#loading").hide();
  $("#ended").hide();
  $("#log").empty();
  $("#log").show();
  $("#spectatorName").empty();
  $("#spectatorName").append(spectatorState.login);
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
  // start the spectatorState (consumeLoop...)
  spectatorState.start();
  // publish a join message
  publishToGameLogicServer(spectatorState, "join.joinSpectator", spectatorState.login+",Tidy-City,Instance-1,*.*.*.*");
}


/**
 * Callback function triggered by gameMasterState.connectionExit().
 **/
function onExitConnection(){
  // update the display to show the end screen
  showEnd(); 
}


$(document).ready(function() {
    
    // when the user clicks the join button
    $("#connectButton").click(function () {
        var nick = $("#nickInput").attr("value");
        // instantiate spectatorState with specific heartbeat and maxRetry values (optional)
        spectatorState = new State(nick, DEFAULT_PWD , "Tidy-City", "Instance-1", heartbeat=0, maxRetry=100);
        spectatorState.observationKey = "*.*.*.*";
        // register its own actions
        spectatorState.listOfActions.myFirstActionKind = new MyFirstActionKind();
         //make the connect request to the server
        joinSpectatorGameInstance(  spectatorState.login, spectatorState.password, 
                                    spectatorState.gameName, spectatorState.instanceName,spectatorState.observationKey, 
                                    function(session){
                                        onCreateConnection(session);
                                    });
        //lock the UI while waiting for a response
        showLoad();
        return false;
    });
        
        
    // when the user clicks on the end connection button
    $("#endConnectionButton").click(function () {
        spectatorState.connectionExit();
        return false;
    });
    
    // first screen displayed to spectator
    showConnect();
});


// If we can, notify the proxy that we're going away to end the amqp connection
$(window).unload(function () {
    spectatorState.connectionExit();
});