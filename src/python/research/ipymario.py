__author__="Sergey Karakovskiy, sergey at idsia dot ch"
__date__ ="$Apr 30, 2009 1:46:32 AM$"

import sys
from client.marioenvironment import MarioEnvironment
from pybrain.rl.experiments.episodic import EpisodicExperiment
from tasks.mariotask import MarioTask
from utils.cmdlineoptions import CmdLineOptions
from agents.forwardagent import ForwardAgent

def main():
    task = MarioTask(initMarioMode = 1)
    agent = ForwardAgent()
    exp = EpisodicExperiment(task, agent)
    print 'Task Ready'
    exp.doEpisodes(1)
    print 'mm 1:', task.reward
    
    task.env.initMarioMode = 2
    exp.doEpisodes(1)
    print 'mm 2:', task.reward
    
    task.env.initMarioMode = 0
    exp.doEpisodes(1)
    print 'mm 0:', task.reward
    
    print "finished"

if __name__ == "__main__":
    main()

