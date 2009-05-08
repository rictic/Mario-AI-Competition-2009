import getopt
__author__="Sergey Karakovskiy, sergey at idsia dot ch"
__date__ ="$Apr 30, 2009 1:46:32 AM$"
from AI.Environment import Environment
import sys
from AI import ForwardRandomAgent
from AI import ForwardAgent
from Network import TCPClientAgent


def main():
    try:
        opts, args = getopt.getopt(sys.argv[1:], "pa", ["port=", "agent="])
    except getopt.GetoptError, err:
        print str(err)
        usage()
        sys.exit(2)

    HOST = 'localhost'
    PORT = 4242
    agentName = "ForwardAgent"
    actingAgent = None

    for o, a in opts:
        if o == "--port":
            PORT = int(a)
        elif o == "--agent":
            agentName = a
        else:
            assert False, "unhandled option"
    if agentName == "ForwardAgent":
        actingAgent = ForwardAgent(Environment())
    elif agentName == "ForwardRandomAgent":
        actingAgent = ForwardRandomAgent()
    else:
        assert  False, "unknown Agent"

    task = MarioTask(difficulty = 4)
    exp = EpisodicExperiment(actingAgent, task)
    exp.doEpisodes(10)

    class MarioTask(EpisodicTask):

    MarioEnvironment()

    agent = TCPClientAgent(actingAgent, HOST, PORT)
    while True:
        agent.getObservation()
        if not agent.isConnected():
            print "Agent was disconnected. Exiting..."
            break
#            sys.exit(1)
        agent.produceAction()

def usage ():
    print "python iPyMario.py [--port port][--agent AgentName]"
    

if __name__ == "__main__":
    main()
else:
    print "This is module to be run rather than imported."

