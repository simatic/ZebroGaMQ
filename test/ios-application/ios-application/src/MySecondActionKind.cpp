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

#include "MySecondActionKind.h"
#include "MyGameLogicProtocol.h"
#include <zebrogamq-ios/Properties.h>
#include <zebrogamq-ios/ZebroGamqUtil.h>

MySecondActionKind::MySecondActionKind() {
    Action::setKind("MySecondActionKind");

	Action::add("myFourthAction", myFourthAction);
	Action::add("myFifthAction", myFifthAction);
}

std::string MySecondActionKind::toString() const {
	return getKind() + ZebroGamqUtil::getRabbitMQProperties()->getProperty("routingKeySeparator") + std::string("ackLoadServer");
}
