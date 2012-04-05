"""
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

 Developer(s): Denis Conan, Gabriel Adgeg
"""

import xmlrpclib
import sys
from zebrogamq.configuration.xmlrpc.xmlrpcconfig import XMLRPCConfiguration

confDir = "../resources/"

if __name__ == '__main__':
    # if confDir is used in params
    if len(sys.argv) == 2:
        confDir = sys.argv[1]
    print " [Termination] Beginning"
    proxy = xmlrpclib.ServerProxy("http://"
                                  + XMLRPCConfiguration(confDir).getXMLRPCProperty("gameServerXMLRPCHost")
                                  + ":"
                                  + XMLRPCConfiguration(confDir).getXMLRPCProperty("gameServerXMLRPCPort")
                                  + "/")
    try:
        proxy.terminate()
        #proxy.terminateGameInstance("Tidy-City","Instance-1")
    except:
        pass
    print ' [Termination] Exiting'
    sys.exit(1)
