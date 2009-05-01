__author__="Sergey Karakovskiy, sergey at idsia dot ch"
__date__ ="$Apr 30, 2009 1:46:32 AM$"
from AI.Environment import Environment
import sys
from AI import ForwardRandomAgent
from AI import ForwardAgent
from Network import TCPClientAgent

if __name__ == "__main__":
    HOST = 'localhost'
    PORT = 4242
#    fra = ForwardRandomAgent()
#    agent = TCPClientAgent(fra, HOST, PORT)
    fa = ForwardAgent(Environment())
    agent = TCPClientAgent(fa, HOST, PORT)
    while True:
        agent.getObservation()
        if not agent.isConnected():
            print "Agent was disconnected. Exiting..."
            break
#            sys.exit(1)
        agent.produceAction()

    sys.exit(0)
else:
    print "Better run this file:"