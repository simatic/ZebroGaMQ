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

#include "PresenceAction.h"
#include "GameLogicProtocol.h"
#include "Properties.h"
#include "ZebroGamqUtil.h"

PresenceAction::PresenceAction() {
	Action::setKind("presence");

	// TODO Methode existant dans la classe Java, mais qui n semblent pas necessaires en CPP
	// Action::add("heartbeat", heartbeat);
	// Action::add("askParticipantsList", askParticipantsList);
	Action::add("participantsList", participantsList);
}

std::string PresenceAction::toString() const {
	return getKind() + ZebroGamqUtil::getRabbitMQProperties()->getProperty("routingKeySeparator") + std::string("participantsList");
}


