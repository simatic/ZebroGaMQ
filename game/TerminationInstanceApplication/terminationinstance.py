import xmlrpclib
import sys
from net.totem.configuration.xmlrpc.xmlrpcconfig import XMLRPCConfiguration

if __name__ == '__main__':
    print " [Termination] Beginning for ",sys.argv[1]
    proxy = xmlrpclib.ServerProxy("http://"
                                  + XMLRPCConfiguration().getXMLRPCProperty("gameServerXMLRPCHost")
                                  + ":"
                                  + XMLRPCConfiguration().getXMLRPCProperty("gameServerXMLRPCPort")
                                  + "/")
    try:
        proxy.terminateGameInstance("aMazing",sys.argv[1])
    except:
        pass
    print ' [Termination] Exiting'
    sys.exit(1)
