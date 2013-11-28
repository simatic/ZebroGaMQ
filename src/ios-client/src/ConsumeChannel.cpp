/**
 ZebroGaMQ: Communication Middleware for Mobile Gaming
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

 Developer(s): Michel SIMATIC, Van Hung LE
 */

#include "ConsumeChannel.h"
#include "GameLogicState.h"
#include "Properties.h"
#include "AMQPcpp.h"
#include "LifeCycleAction.h"
#include "JoinAction.h"
#include "PresenceAction.h"
#include "ZebroGamqUtil.h"
#include <thread>
#include <fstream>
#include <iostream>

const std::string ConsumeChannel::RAW_ACTION_KIND = "rawActionKind";
const std::string ConsumeChannel::RAW_ACTION_NAME = "rawAction";

using namespace std;

ConsumeChannel::ConsumeChannel(const ChannelsManager* channelsManager,
		   const GameLogicState* state,
		   const std::vector<Action *> loai,
		   bool enableRawActions) : RabbitMQGameInstanceChannel(channelsManager, state) {
	/*
	Dans ConsumeChannel.java, méthode ConsumeChannel
	les lignes
			listsOfListOfActions = new Vector<List<Map<String, ? extends GameLogicActionInterface>>>();
			listsOfListOfActions.add(ListOfActions.ListOfActionsMaps);
			listsOfListOfActions.add(loai);
	deviennent
	        listsOfListOfActions devient un vector<Action *> // Donc le new Vector est inutile !
	        listsOfListOfActions.push_back(new LifeCycleAction());
	        listsOfListOfActions.push_back(new JoinAction());
	        listsOfListOfActions.push_back(new PresenceAction());
	        for (int i(loai->size() - 1) ; i >= 0 ; i--) {
	        	listsOfListOfActions.push_back(loai[i]);
	        }
	*/
	listsOfListOfActions.push_back(new LifeCycleAction());
	listsOfListOfActions.push_back(new JoinAction());
	listsOfListOfActions.push_back(new PresenceAction());
	for (int i(loai.size() - 1) ; i >= 0 ; i--) {
		listsOfListOfActions.push_back(loai[i]);
	}

	this->rawActionsEnabled = enableRawActions;
	startConsumeLoopThread(state);
}



void ConsumeChannel::startConsumeLoopThread(const GameLogicState* state) {
	std::thread t(&ConsumeChannel::consumeLoop, this, state);
	t.detach();
}

void ConsumeChannel::consumeLoop(const GameLogicState* state) {
	ofstream filestr;
	bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
	if ( isTempFileUsed ) {
		filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
	}
	if (state == nullptr) {
		if ( isTempFileUsed ) {
			filestr << "EXCEPTION: consumeLoop with null state" << endl;
		} else {
			cout << "EXCEPTION: consumeLoop with null state" << endl;
		}
	}
	if (this->amqp == nullptr) {
		if ( isTempFileUsed ) {
			filestr << "EXCEPTION: consumeLoop with null channel" << endl;
		} else {
			cout << "EXCEPTION: consumeLoop with null channel" << endl;
		}
	}
	if (state->login.size() == 0) {
		if ( isTempFileUsed ) {
			filestr << "EXCEPTION: consumeLoop with null queue name" << endl;
		} else {
			cout << "EXCEPTION: consumeLoop with null queue name" << endl;
		}
	}

	AMQPQueue* consumer = getQueingConsumer(state);
	while (!state->hasConnectionExited()) {
		try {
			consumer->Get(AMQP_NOACK);
			AMQPMessage * m= consumer->getMessage();
			if (m->getMessageCount() > -1) {
				if ( isTempFileUsed ) {
					filestr << "\n******************* Message livre ***************************\n" << endl;
				} else {
					cout << "\n******************* Message livre ***************************\n" << endl;
				}
				handleDelivery(m, state);

			} else {
				// We sleep a little before polling again the queue
				usleep(50000);
			}
		} catch (AMQPException &e) {
			consumer = newQueueingConsumer(state);
			if(consumer != nullptr){
				continue;
			}
			cout << e.getMessage() << std::endl;
		}
	}
	if ( isTempFileUsed ) {
		filestr.close();
	}
}

AMQPQueue* ConsumeChannel::getQueingConsumer(const GameLogicState *state) {
	AMQPQueue * queue = amqp->createQueue(state->login);
	queue->Declare(state->login, AMQP_NOACK);
	return queue;
}

void ConsumeChannel::handleDelivery(AMQPMessage *&message, const GameLogicState* state) {
	uint32_t j = 0;
	char * data = message->getMessage(&j);
	std::string body;
	if (data) {
		body = std::string(data);
		body = body.substr(0, j);
	}

	std::string headerString(message->getRoutingKey());
	if ( (body.size() != 0) && (headerString.size() != 0) ) {
		ofstream filestr;
		bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
		if ( isTempFileUsed ) {
			filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
			filestr << "Body = " << body << endl;
			filestr << "Head = " << headerString << endl;
			filestr << " [" << state->role << " " << state->login << "] received on "
				 	<< state->virtualHost << ": " << body << ", with routing key = "
				 	<< headerString << endl;
		} else {
			cout << "Body = " << body << endl;
			cout << "Head = " << headerString << endl;
			cout << " [" << state->role << " " << state->login << "] received on "
				 << state->virtualHost << ": " << body << ", with routing key = "
				 << headerString << endl;
		}
		std::vector<std::string> header;

		char *str = new char[headerString.size()];
		sprintf(str, "%s", headerString.c_str());
		char * pch;
		string delim = "\\" + ZebroGamqUtil::getRabbitMQProperties()->getProperty("routingKeySeparator");
		pch = strtok (str, delim.c_str());
		while (pch != NULL) {
			if (pch != NULL) {
				header.push_back(string(pch));
			}
			pch = strtok (NULL, delim.c_str());
		}

		if (header.size() == 4) {
			std::string actionKind = header[2];
			std::string actionName = header[3];
			try {
				executeAction(actionKind, actionName, state, header, body);
			} catch (AMQPException &e) {
				if ( isTempFileUsed ) {
					filestr << " [" << state->role << " " << state->login
						 	<< "] Unknown action: " << e.getMessage() << endl;
				} else {
					cout << " [" << state->role << " " << state->login
						 << "] Unknown action: " << e.getMessage() << endl;
				}
			}
		} else {
			if ( isTempFileUsed ) {
				filestr << " [" << state->role << " " << state->login << "] Malformed message "
					 	<< "(header of length " << header.size() << "instead of 4)" << endl;
				filestr << " [" << state->role << " " << state->login << "] Ignore message" << endl;
			} else {
				cout << " [" << state->role << " " << state->login << "] Malformed message "
					 << "(header of length " << header.size() << "instead of 4)" << endl;
				cout << " [" << state->role << " " << state->login << "] Ignore message" << endl;
			}
		}
		if ( isTempFileUsed ) {
			filestr.close();
		}
	}
}

bool ConsumeChannel::executeAction(const std::string nameKind,
		const std::string nameAction, const GameLogicState *state,
		const std::vector<std::string> header, const std::string body)
{
	bool result = false;
	bool found = false;
	/*
	Dans ConsumeChannel, méthode executeAction
	Les lignes
	for (List<Map<String, ? extends GameLogicActionInterface>> aml : listsOfListOfActions) {
		for (Map<String, ? extends GameLogicActionInterface> am : aml) {
			GameLogicActionInterface action = am.get(nameKind
					+ Util.getRabbitMQProperties().getProperty(
							"routingKeySeparator") + nameAction);
			if (action != null) {
				found = true;
				result = action.execute(state, header, body);
			}
		}
	}
	deviennent
	for (int k=(listsOfListOfActions.size() - 1) ; (k >= 0) && (!found) ; k--) {
		Action* anAction(listsOfListOfActions[k]);
		if (anAction->getKind() == nameKind) {
			for (int i(anAction->getList().size() - 1) ; (i >= 0) && (!found) ; i--) {
				if (anAction->getList()[i].s == nameAction) {
					found = true;
					(anAction->getList()[i].f)(state, header, body);
				}
			}
		}
	}*/
	for (int k=(listsOfListOfActions.size() - 1) ; (k >= 0) && (!found) ; k--) {
		Action* anAction(listsOfListOfActions[k]);
		if (anAction->getKind() == nameKind) {
			for (int i(anAction->getList().size() - 1) ; (i >= 0) && (!found) ; i--) {
				if (anAction->getList()[i].s == nameAction) {
					found = true;
					(anAction->getList()[i].f)(*state, header, body);
				}
			}
		}
	}

	if (!found) {
		if(rawActionsEnabled){
			result = executeRawAction(state, header, body);
		}else{
			ofstream filestr;
			bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
			if ( isTempFileUsed ) {
				filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
				filestr << "Unknown game instance action '"
				 		<< nameKind << ZebroGamqUtil::getRabbitMQProperties()->getProperty("routingKeySeparator")
				 		<< nameAction << "'" << endl;
				filestr.close();
			} else {
				cout << "Unknown game instance action '"
					 << nameKind << ZebroGamqUtil::getRabbitMQProperties()->getProperty("routingKeySeparator")
				 	 << nameAction << "'" << endl;
			}
		}
	}
	return result;
}

bool ConsumeChannel::executeRawAction(const GameLogicState *state,
		const std::vector<std::string> header, const std::string body)
{
	bool result = false;
	bool found = false;
	/*
	Dans ConsumeChannel, méthode executeAction
	Les lignes
	for (List<Map<String, ? extends GameLogicActionInterface>> aml : listsOfListOfActions) {
		for (Map<String, ? extends GameLogicActionInterface> am : aml) {
			GameLogicActionInterface action = am.get(nameKind
					+ Util.getRabbitMQProperties().getProperty(
							"routingKeySeparator") + nameAction);
			if (action != null) {
				found = true;
				result = action.execute(state, header, body);
			}
		}
	}
	deviennent
	for (int k=(listsOfListOfActions.size() - 1) ; (k >= 0) && (!found) ; k--) {
		Action* anAction(listsOfListOfActions[k]);
		if (anAction->getKind() == nameKind) {
			for (int i(anAction->getList().size() - 1) ; (i >= 0) && (!found) ; i--) {
				if (anAction->getList()[i].s == nameAction) {
					found = true;
					(anAction->getList()[i].f)(state, header, body);
				}
			}
		}
	}
	*/
	for (int k=(listsOfListOfActions.size() - 1) ; (k >= 0) && (!found) ; k--) {
		Action* anAction(listsOfListOfActions[k]);
		if (anAction->getKind() == RAW_ACTION_KIND) {
			for (int i(anAction->getList().size() - 1) ; (i >= 0) && (!found) ; i--) {
				if (anAction->getList()[i].s == RAW_ACTION_NAME) {
					found = true;
					(anAction->getList()[i].f)(*state, header, body);
				}
			}
		}
	}

	if (!found) {
		ofstream filestr;
		bool isTempFileUsed = (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile").compare("out") != 0);
		if ( isTempFileUsed ) {
			filestr.open (ZebroGamqUtil::getConfigProperties()->getProperty("temporaryLogFile"), ios::in | ios::out | ios::trunc);
			filestr << "EXCEPTION : (WARNING) : Raw action kind is not registered in the list of Game Logic Actions. " <<
				"You must either create and register it, or set enableRawActions to false in the" <<
				" constructor of your ChannelsManager." << endl;
			filestr.close();
		} else {
			cout << "EXCEPTION : (WARNING) : Raw action kind is not registered in the list of Game Logic Actions. " <<
					"You must either create and register it, or set enableRawActions to false in the" <<
					" constructor of your ChannelsManager." << endl;
		}
	}
	return result;
}


/*
 * If state has not exited, this method:
 * - print the exception message
 * - close channel and connection
 * - try to reconnect
 * - return a new QueuingConsumer attached to the new channel.
 *
 * TODO QueueingConsumer, a particular JAVA RabbitMQ class, is used to get message. In amqpcpp, message is get through queue => so, let's change it by AMQPQueue
 */
AMQPQueue *ConsumeChannel::newQueueingConsumer(const GameLogicState *state) {
	AMQPQueue *consumer = nullptr;
	if( !state->hasConnectionExited() ) {
		// close channel and connection
		amqp->closeChannel();
		bool reconnected = initCommunicationWithBroker(state);
		if(reconnected){
			// re instantiate consumer
			consumer = getQueingConsumer(state);
		}
	}
	return consumer;
}

void ConsumeChannel::close() {}

ConsumeChannel::~ConsumeChannel() {
	// TODO Auto-generated destructor stub
}

