#! /usr/bin/python
__author__="Sergey Karakovskiy, sergey at idsia dot ch"
__date__ ="$Apr 29, 2009 10:11:15 PM$"

import socket
import sys
import random
import numpy

def parse(data):
    ret = numpy.empty(shape = (22,22), dtype = numpy.int)
    k = 0
    for i in range(22):
        for j in range(22):
            ret[i,j] = int(data[k + 2])
            k += 1
    return ret

class SimpleTCPAgent:
    """
    Simple TCP Agent. Connect to a server, receives observation, sends action
    """
    def __init__(self, HOST, PORT):
        """Constructor. Receives host and port and tries to open
        a connection"""
        self.host = HOST
        self.port = PORT
        try:
          self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        except socket.error, msg:
          sys.stderr.write("[ERROR] %s\n" % msg[1])
          sys.exit(1)

        try:
          self.sock.connect((self.host, self.port))

        except socket.error, msg:
          sys.stderr.write("[ERROR] %s\n" % msg[1])
          sys.exit(2)
        GreatingMessage = "Hello, Mario! I am SimpleTCPAgent! \r\n"
        self.sock.settimeout(3)

        self.sock.send(GreatingMessage)

        self.size = 4096
        data = self.sock.recv(self.size)
        print data

    def getAction(self):
        """Get observation, (possibly analyse it), sent an action back
        @param obs: observation from the environment
        @type obs: by default, this is assumed to be a numpy array of doubles
        """
        self.obs = data = self.sock.recv(self.size).split(' ')
        if len(data) != 486:
            print "Network critical error. len(data) = ", len(data), ". \nConnection dropped"


        fwd = "01000\r\n"
        fwdjump = "01010\r\n"
        fwdunjump = "01010\r\n"
        actions = [fwd, fwdjump,fwdunjump]

        return actions[random.randint(0, 2)]


    def sendAction(self, action):
        """Sends the action through the opened in constructor TCP"""
        self.sock.send(action)

    def __del__(self):
        """Destructor. Closes the socket"""
        self.sock.close()

if __name__ == "__main__":
    print "SimpleTCPAgent executed as independent module."
else:
    print "SimpleTCPAgent invoked from module "

HOST = 'localhost'
PORT = 4242;

sys.exit(0)