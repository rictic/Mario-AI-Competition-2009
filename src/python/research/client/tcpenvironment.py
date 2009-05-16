__author__="Sergey Karakovskiy, sergey at idsia fullstop ch"
__date__ ="$May 13, 2009 1:25:30 AM$"

from network.client import Client
#from ai.environments.environment import Environment
from pybrain.rl.environments.environment import Environment

class TCPEnvironment(Environment):
    """
    Documentation
    """

    client = None
    host = None
    port = None

    def __init__(self, host, port, agentName):
        """General TCP Environment"""
        self.host = host
        self.port = port
        self.client = Client(host, port, agentName)
        self.connected = True

    def isAvailable(self):
        """returns the availability status of the environment"""
        return self.connected

    def getSensors(self):
        """ receives and observation"""
        #        print "Looking forward to receive data"
        data = self.client.recvData()
        #        print "Data received: ", data
        if data == "-ciao":
            self.connected = False
            self.disconnet()
        elif len(data) > 5:
        #        print data
            return data
        pass

    def performAction(self, action):
        """takes a numpy array of ints and sends as a string to server"""
    #        print "TCPAgent.produceAction: produsing Action..."
        actionStr = ""
        for i in range(5):
            if action[i] == 1:
                actionStr += '1'
            elif action[i] == 0:
                actionStr += '0'
            else:
                raise "something very dangerous happen...."
        actionStr += "\r\n"
        self.client.sendData(actionStr)
        #        print "Action %s produced" % a
        pass

    def reset(self):
        self.client.sendData("reset -ld 5 -lt 0 -pw on -zm 1 -mm 2 -vaot on\r\n")
        