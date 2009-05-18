__author__ = "Sergey Karakovskiy, sergey at idsia fullstop ch; Tom Schaul"
__date__ = "$May 13, 2009 1:25:30 AM$"

from client import Client
from pybrain.rl.environments.environment import Environment
from pybrain.utilities import setAllArgs

class TCPEnvironment(Environment):

    def __init__(self, host = 'localhost', port = 4242, agentName = "UnnamedClient", **otherargs):
        """General TCP Environment"""
        setAllArgs(self, otherargs)
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
            self.client.disconnect()
            self.connected = False            
        elif len(data) > 5:
        #        print data
            return data

    def performAction(self, action):
        """takes a numpy array of ints and sends as a string to server"""
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
