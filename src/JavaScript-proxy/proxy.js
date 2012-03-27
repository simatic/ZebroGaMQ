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

/*  Inspired from server.js of the node chat demo
    available here: https://github.com/ry/node_chat */

JAVASCRIPT_SRC_DIRECTORY = "../JavaScript-client/";
FIRST_REQUEST_TIMEOUT = 500; // in ms
FIRST_REQUEST_NUMBER_OF_TRIES = 10;
BROADCAST_KEY = "all";

var fu = require("./external/fu"),
    readFile = require("fs").readFile,
    xmlrpc = require("xmlrpc"),
    amqpHandler = require("./amqpHandler"),
    sys = require("sys"),
    url = require("url"),
    qs = require("querystring"),
    trim = require('./utils').trim,
    createRoutingKey = require('./utils').createRoutingKey,
    events = require('events');

var NODE_PROXY_HOST, 
    NODE_PROXY_PORT,
    GAMESERVER_HOST,
    GAMESERVER_PORT,
    GAME_LOGIC_USER_NAME,
    getMessageResponses = [],
    createAmqpConnectionResponses = [],
    firstGetMessageRequest = [],
    forwardMessage = new events.EventEmitter(),
    amqpConnectionCreation = new events.EventEmitter();

exports.forwardMessage = forwardMessage;
exports.amqpConnectionCreation = amqpConnectionCreation;

startProxy();

/**
 * Load the properties file.
 * Once it is done, start the proxy. 
 **/
function startProxy (){
     readFile("resources/config.properties", function (err, data) {
        if (err) throw err;
        // retrieve properties
        var res = qs.parse(data.toString(), sep='\n', eq=' ');
        NODE_PROXY_HOST = res.nodeProxyHost ? trim(res.nodeProxyHost) : "localhost";
        NODE_PROXY_PORT = res.nodeProxyPort ? trim(res.nodeProxyPort) : 8001;
        GAMESERVER_HOST = res.gameServerHost ? trim(res.gameServerHost) : "localhost";
        GAMESERVER_PORT = res.gameServerPort ? trim(res.gameServerPort) : 8888;
        readFile("resources/rabbitmq.properties", function (err, data) {
            if (err) throw err;
            var res = qs.parse(data.toString(), sep='\n', eq=' ');
            GAME_LOGIC_USER_NAME = res.gameLogicUserName ? trim(res.gameLogicUserName) : "gamelogicserver";
        });
        
        // start the proxy
        fu.listen(Number(NODE_PROXY_PORT), NODE_PROXY_HOST);
        sys.puts("URL of JavaScript application: http://" + (NODE_PROXY_HOST || "127.0.0.1") + ":" + NODE_PROXY_PORT.toString() + "/JavaScript-application");
        
        // load files required by clients applications
        loadGenericFiles();
        loadGamesSpecificFiles();
   });
}

function loadGenericFiles(){
    fu.get("/rabbitmq-totem-library.js", fu.staticHandler(JAVASCRIPT_SRC_DIRECTORY+"rabbitmq-totem-library.js"));
    fu.get("/rabbitmq-totem-actions.js", fu.staticHandler(JAVASCRIPT_SRC_DIRECTORY+"rabbitmq-totem-actions.js"));
    fu.get("/jquery-1.6.2.min.js", fu.staticHandler(JAVASCRIPT_SRC_DIRECTORY+"external/jquery-1.6.2.min.js"));
}

function loadGamesSpecificFiles(){
    readFile("resources/games-specific-files", function (err, data) {
        if (err) throw err;
        var res = qs.parse(data.toString(), sep='\n', eq=' ');
        for(var prop in res){
            if(res.hasOwnProperty(prop)){
                fu.get(prop, fu.staticHandler(trim(res[prop])));
            }
        }
    });    
}


// Forward message to user on the reception of message
// in its queue.
forwardMessage.on('messagesReceived',function(userName){
     var message = amqpHandler.getMessage(userName);
     var deliveryInfo = amqpHandler.getDeliveryInfo(userName);
     console.log("[DEBUG] on messagesReceived: message = "+message+" with routingKey = "+deliveryInfo.routingKey);
     if(firstGetMessageRequest[userName]){
         handleFirstGetMessageRequest(userName, message, deliveryInfo.routingKey);
     }else{
         handleGetMessageRequest(userName, message, deliveryInfo.routingKey);
     }
});

// Send an HTTP response to the user if the AMQP connection was
// created properly.
amqpConnectionCreation.on('amqpConnectionReady',function(userName){
     if(createAmqpConnectionResponses[userName]){
         createAmqpConnectionResponses[userName].simpleJSON(200, {nick: userName});
         createAmqpConnectionResponses[userName] = null;
     }
});

// Send an HTTP response to the user if the AMQP connection was
// not created.
amqpConnectionCreation.on('amqpConnectionEnded',function(userName){
    if(createAmqpConnectionResponses[userName]){
         createAmqpConnectionResponses[userName].simpleJSON(400, {error: "AMQP connection is ended for "+userName+": check that user has suitable permissions."});
         createAmqpConnectionResponses[userName] = null;
     }
});


// This method is needed to handle the case where
// a message has been received on the subscription of a queue
// while no getMessage request has been sent yet.
function handleFirstGetMessageRequest (userName, message, routingKey) {
    var count = 0;
    setInterval(function(){
         if(getMessageResponses[userName]){
            getMessageResponses[userName].simpleJSON(200, {message: message, routingKey: routingKey});
            getMessageResponses[userName] = null;
            firstGetMessageRequest[userName] = false;
            // leave the loop
            clearInterval(this);
         }else{
            count++;
            if(count > FIRST_REQUEST_NUMBER_OF_TRIES){
                // connection is ended
                amqpHandler.endConnection(userName);
                sys.puts("[WARNING] User "+userName+" doesn't send any getMessage request: "+
                         "its connection is ended.");
                // leave the loop
                clearInterval(this);           
            }
         }
    },FIRST_REQUEST_TIMEOUT);
}


function handleGetMessageRequest (userName, message, routingKey) {
    if(getMessageResponses[userName]){
         getMessageResponses[userName].simpleJSON(200, {message: message, routingKey: routingKey});
         getMessageResponses[userName] = null;
     }
}


fu.get("/xmlrpc-create-and-join-instance", function (req, res) {
  var login = qs.parse(url.parse(req.url).query).login;
  var password = qs.parse(url.parse(req.url).query).password;
  var gameName = qs.parse(url.parse(req.url).query).gameName;
  var instanceName = qs.parse(url.parse(req.url).query).instanceName;
  
  // if params are correct
  if (login && password && gameName && instanceName) {
      var client = xmlrpc.createClient({ host: GAMESERVER_HOST, port: GAMESERVER_PORT});
      // Sends a method call to the XML-RPC server
      client.methodCall("createAndJoinGameInstance", [login, password, gameName, instanceName], function (error, value) {
          // Results of the method response
          console.log("Response for "+login+"'s game instance creation request: " + value);
          if(value === true){
              // store the response for proper handling on result of AMQP createConnection
              createAmqpConnectionResponses[login] = res;
              // create amqp connection
              amqpHandler.createConnection(login, {password: password, vhost: "/"+gameName+"/"+instanceName});
              firstGetMessageRequest[login] = true;
          }else{
              res.simpleJSON(400, {error: "XML-RPC login failed: permission denied."});
          }
      });
  }else{
    res.simpleJSON(400, {error: "Cannot execute XML-RPC login: mandatory parameters are not specified."});
  }
});


fu.get("/xmlrpc-join-instance", function (req, res) {
  var login = qs.parse(url.parse(req.url).query).login;
  var password = qs.parse(url.parse(req.url).query).password;
  var gameName = qs.parse(url.parse(req.url).query).gameName;
  var instanceName = qs.parse(url.parse(req.url).query).instanceName;
  var observationKey = qs.parse(url.parse(req.url).query).observationKey;
  
  // if params are correct
  if (login && password && gameName && instanceName) {
      var client = xmlrpc.createClient({ host: GAMESERVER_HOST, port: GAMESERVER_PORT});
      var params;
      // test if application has used the observationKey param
      if(observationKey){
          params = [login, password, gameName, instanceName, observationKey];
      }else{
          params = [login, password, gameName, instanceName];
      }   
      // Sends a method call to the XML-RPC server
      client.methodCall("joinGameInstance", params, function (error, value) {
          // Results of the method response
          console.log("Response for "+login+"'s game instance creation request: " + value);
          if(value === true){
              // store the response for proper handling on result of AMQP createConnection
              createAmqpConnectionResponses[login] = res;
              // create amqp connection
              amqpHandler.createConnection(login, {password: password, vhost: "/"+gameName+"/"+instanceName});
              firstGetMessageRequest[login] = true;
          }else{
              res.simpleJSON(400, {error: "XML-RPC login failed: permission denied."});
          }
      });
  }else{
    res.simpleJSON(400, {error: "Cannot execute XML-RPC login: mandatory parameters are not specified."});
  }
});


fu.get("/xmlrpc-terminate-instance", function (req, res) {
  var gameName = qs.parse(url.parse(req.url).query).gameName;
  var instanceName = qs.parse(url.parse(req.url).query).instanceName;
  
  // if params are correct
  if (gameName && instanceName) {
      var client = xmlrpc.createClient({ host: GAMESERVER_HOST, port: GAMESERVER_PORT});
      // Sends a method call to the XML-RPC server
      client.methodCall("terminateGameInstance", [gameName, instanceName], function (error, value) {
          if(value === true){
              console.log(gameName+" "+instanceName+" termination request OK.");
              res.simpleJSON(200,{});
          }else{
              console.log("Cannot terminate game instance "+gameName+" "+instanceName);
              res.simpleJSON(400, {error: "XML-RPC instance termination failed."});
          }
      });
  }else{
    res.simpleJSON(400, {error: "Cannot terminate game instance: mandatory parameters are not specified."});
  }
});


fu.get("/xmlrpc-list-instances", function (req, res) {
  var gameName = qs.parse(url.parse(req.url).query).gameName;
  
  // if param is correct
  if (gameName) {
      var client = xmlrpc.createClient({ host: GAMESERVER_HOST, port: GAMESERVER_PORT});
      // Sends a method call to the XML-RPC server
      client.methodCall("listGameInstances", [gameName], function (error, value) {
          if(error){
              console.log("Cannot list game instances for "+gameName);
              res.simpleJSON(400, {error: "XML-RPC list game instances failed."});
          }else{
              console.log("Game instances for "+gameName+": "+value);
              res.simpleJSON(200,value);
          }
      });
  }else{
    res.simpleJSON(400, {error: "Cannot list game instances: mandatory parameter is not specified."});
  }
});


fu.get("/createconnection", function (req, res) {
  var nick = qs.parse(url.parse(req.url).query).nick;
  // if id is correct
  if (nick) {
    // store the response for proper handling on result of AMQP createConnection
    createAmqpConnectionResponses[nick] = res;  
    amqpHandler.createConnection(nick);
    firstGetMessageRequest[nick] = true;
  }else{
    res.simpleJSON(400, {error: "Cannot create connection: user name is not specified."});
  }
});


fu.get("/endconnection", function (req, res) {
  var nick = qs.parse(url.parse(req.url).query).nick;
  // used to acknoledge a terminate message
  var ackLast = qs.parse(url.parse(req.url).query).ackLast;
  // if id is correct
  if (nick) {
    // json encode boolean as string  
    if (ackLast == "true"){
        amqpHandler.ack(nick);
    }
    amqpHandler.endConnection(nick);
    getMessageResponses[nick] = null;
    res.simpleJSON(200,{});
  }else{
    res.simpleJSON(400, {error: "Cannot end connection: user name is not specified."});
  }
});


fu.get("/publish", function (req, res) {
  var nick = qs.parse(url.parse(req.url).query).nick;
  var recipient = qs.parse(url.parse(req.url).query).recipient;
  var action = qs.parse(url.parse(req.url).query).action;
  var message = qs.parse(url.parse(req.url).query).message;
  // if id is correct
  if (nick && recipient && action) {
    var routingKey = createRoutingKey(nick, recipient, action);
    amqpHandler.publishMessage(nick, routingKey, message);
    res.simpleJSON(200,{});
  }else{
    res.simpleJSON(400, {error: "Cannot publish message: user name, routing key or message is not specified."});
  }
});


fu.get("/publishtogamelogicserver", function (req, res) {
  var nick = qs.parse(url.parse(req.url).query).nick;
  var action = qs.parse(url.parse(req.url).query).action;
  var message = qs.parse(url.parse(req.url).query).message;
  // if id is correct
  if (nick && action) {
    var routingKey = createRoutingKey(nick, GAME_LOGIC_USER_NAME, action);
    amqpHandler.publishMessage(nick, routingKey, message);
    res.simpleJSON(200,{});
  }else{
    res.simpleJSON(400, {error: "Cannot publish message: user name, routing key or message is not specified."});
  }
});


fu.get("/publishtoall", function (req, res) {
  var nick = qs.parse(url.parse(req.url).query).nick;
  var action = qs.parse(url.parse(req.url).query).action;
  var message = qs.parse(url.parse(req.url).query).message;
  // if id is correct
  if (nick && action) {
    var routingKey = createRoutingKey(nick, BROADCAST_KEY, action);
    amqpHandler.publishMessage(nick, routingKey, message);
    res.simpleJSON(200,{});
  }else{
    res.simpleJSON(400, {error: "Cannot publish message: user name, routing key or message is not specified."});
  }
});


fu.get("/getmessage", function (req, res) {
  var nick = qs.parse(url.parse(req.url).query).nick;
  var ackLastMessageReceived = qs.parse(url.parse(req.url).query).ackLast;
  // if id is correct
  if (nick) {
      // at this point connection may have been closed
      // due to a too long first request. Indeed,
      // a check is needed
      if(amqpHandler.isConnectionOpen(nick)){
          // json encode boolean as string
          if(ackLastMessageReceived != "false"){
              amqpHandler.ack(nick);
          }
          // store the response
          getMessageResponses[nick] = res;
      }else{
          res.simpleJSON(400, {error: "Cannot get message: connection has been closed."});
      }
  }else{
      res.simpleJSON(400, {error: "Cannot get message: user name is not specified."});
  }
});