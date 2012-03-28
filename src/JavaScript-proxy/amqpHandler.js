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

var amqp = require('amqp'),
    sys = require("sys"),
    readFile = require("fs").readFile,
    qs = require("querystring"),
    trim = require('./utils').trim,
    proxy = require("./proxy");

var amqpHandler = exports;


EMPTY_MESSAGE = " "; // cause it is impossible to publish a null message
CONNECTION_TO_BROKER_CLOSED_MESSAGE = "[ERROR] AMQP_CONNECTION_FORCED_CLOSED";


var BROKER_HOST,
    BROKER_PORT,
    GAME_LOGIC_EXCHANGE_NAME,
    connections = {},
    queues = {},
    exchanges = {},
    messages = {},
    deliveryInfos = {};


loadRabbitMQProperties();

function loadRabbitMQProperties (){
    readFile("resources/rabbitmq.properties", function (err, data) {
        if (err) throw err;
        var res = qs.parse(data.toString(), sep='\n', eq=' ');
        BROKER_HOST = res.gameLogicServerBrokerHost ? trim(res.gameLogicServerBrokerHost) : "localhost";
        BROKER_PORT = res.gameLogicServerBrokerPort ? trim(res.gameLogicServerBrokerPort) : 5672;
        GAME_LOGIC_EXCHANGE_NAME = res.gameLogicServerExchangeName ? trim(res.gameLogicServerExchangeName) : "GameInstance";
        GAME_LOGIC_USER_NAME = res.gameLogicServerUserName ? trim(res.gameLogicServerUserName) : "gamelogicserver";
    });
}

amqpHandler.createConnection = function(userName, loginParameters){
    // temp
    var connection;
    if(loginParameters === null){
        connection = amqp.createConnection({host: BROKER_HOST, port: BROKER_PORT });
    }else{
        connection = amqp.createConnection({host: BROKER_HOST ,
                                            port: BROKER_PORT ,
                                            login: userName ,
                                            password: loginParameters.password ,
                                            vhost: loginParameters.vhost});
        console.info("[DEBUG] Creating AMQP Connection for user "+userName+" on vhost "+loginParameters.vhost+"'.");
    }
    // wait for connection to become established.
    connection.on('ready', function(){
        console.info("[INFO] AMQP Connection is established for user "+userName+".");
        addConnection(userName, connection);
        // declare the GameInstance exchange
        exchangeDeclare (userName);
        // declare and subscribe to a queue
        queueSubscribe (userName);
    });
    
    // event received when a connection is ended, by example due to vhost access denied
    connection.on('end', function(){
        // inform the proxy that the AMQP connection is ended for userName
        proxy.amqpConnectionCreation.emit('amqpConnectionEnded',userName);
    });
    
    
    // event received when a connection is ended, by example due to vhost access denied
    connection.on('error', function(exception){
        console.info("[WARN] AMQP Connection exception for user "+userName+": "+exception);
        amqpHandler.endConnection(userName);
        // send an error message to the proxy
        messages[userName] = CONNECTION_TO_BROKER_CLOSED_MESSAGE;
        // inform the proxy that a message has been received for userName
        proxy.forwardMessage.emit('messagesReceived',userName);
    });
      
};


/*
 * Add a connection to the list of AMQP connections.
 * If user already had an AMQP connection stored in the list,
 * this former connection is closed, and both queue and exchange matching 
 * with this user are removed form the queues' list and the exchanges' list.
 * 
 * @param userName      the name of user which created the AMQP Connection.
 * @param connection    the AMQP connection.
 */
function addConnection (userName, connection){
    // retrieve former connection if user was already connected
    var formerConnection = connections[userName];
    // store new connection
    connections[userName] = connection;
    // former connection is closed
    if(formerConnection){
        formerConnection.end();
        queues[userName] = null;
        exchanges[userName] = null;
        console.info("[INFO] Former AMQP connection is removed for user "+userName+".");
    }
}


// Subscribe to a queue, named with the parameter userName.
// Acknoledgment for each message received.
function queueSubscribe (userName){
    var connection = connections[userName];
    if (connection){
        // instantiate the queue
        var queue = connection.queue(userName, {   exclusive: false,
                                                    autoDelete: false});                                            
        // queue stored for further closing and acknoledgment of messages
        queues[userName] = queue;
        // inform the proxy that the AMQP connection is ready for userName
        proxy.amqpConnectionCreation.emit('amqpConnectionReady',userName);
        // consume the queue
        queue.subscribe({ ack: true },function(message, headers, deliveryInfo){
            messages[userName] = message.data.toString();
            deliveryInfos[userName] = deliveryInfo;
            // inform the proxy that a message has been received for userName
            proxy.forwardMessage.emit('messagesReceived',userName);
            //sys.puts("[DEBUG] Message "+message.data.toString()+" received for user "+userName);
        });
    }else {
        console.warn("[WARNING] Cannot subscribe to a queue: no AMQP Connection is matching with user "+userName);
    }
}


// Declare a topic exchange for the publication of messages. 
function exchangeDeclare (userName){
    var connection = connections[userName];
    if (connection){
        // declare exchange
        var exchange = connection.exchange(GAME_LOGIC_EXCHANGE_NAME,{ type:'topic'});
        console.log("[INFO] Exchange "+GAME_LOGIC_EXCHANGE_NAME+" declared by "+userName+".");
        exchange.on('open', function(){
            exchanges[userName] = exchange;
        });
    }else {
        console.warn("[WARNING] Cannot declare an exchange: no AMQP Connection is matching with user "+userName);
    }
}


amqpHandler.endConnection = function(userName){
    var connection = connections[userName];
    if(connection){
        queues[userName] = null;
        exchanges[userName] = null;
        connection.end();
        connections[userName] = null;
        console.info("[INFO] AMQP Connection has ended for user "+userName+".");
    }else{
        console.warn("[WARNING] Cannot end AMQP connection: no connection is matching with user "+userName);
    }
};


amqpHandler.isConnectionOpen = function(userName){
    var connection = connections[userName];
    return (connection !== null);
};


amqpHandler.publishMessage = function(userName,routingKey,message){
    var connection = connections[userName];
    if (connection){
        var e = exchanges[userName];
        if(e){
            if(message){
                e.publish(routingKey, message);
                console.log("[DEBUG] Message '"+message.toString()+"' published by user '"+userName+"' on exchange '"+e.name+"' with routing key '"+routingKey+"'.");
            }else{
                e.publish(routingKey, EMPTY_MESSAGE);
                console.log("[DEBUG] Empty message published by user '"+userName+"' on exchange '"+e.name+"' with routing key '"+routingKey+"'.");
            }
        }else {
            console.warn("[WARNING] No exchange is matching with user "+userName);
        }
    }else {
        console.warn("[WARNING] Cannot publish a message: no AMQP Connection is matching with user "+userName);
    }
};


amqpHandler.getMessage = function(userName){
    return messages[userName];
};


amqpHandler.getHeaders = function(userName){
    return headers[userName];
};


amqpHandler.getDeliveryInfo = function(userName){
    return deliveryInfos[userName];
};


amqpHandler.ack = function(userName){
    var queue = queues[userName];
    if(queue){
        queue.shift();
        //sys.puts("[DEBUG] Message "+messages[userName]+" ack by user "+userName);
    }else{
        console.warn("[WARNING] No queue is matching with user "+userName);
    }
};
