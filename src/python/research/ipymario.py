__author__="Sergey Karakovskiy, sergey at idsia dot ch"
__date__ ="$Apr 30, 2009 1:46:32 AM$"

import sys
from client.marioenvironment import MarioEnvironment
from pybrain.rl.experiments.episodic import EpisodicExperiment
from tasks.mariotask import MarioTask
from utils.cmdlineoptions import CmdLineOptions

def main():
    clo = CmdLineOptions(sys.argv)
    task = MarioTask(MarioEnvironment(clo.getHost(), clo.getPort(), clo.getAgent().name))
    exp = EpisodicExperiment(task, clo.getAgent())
    exp.doEpisodes(3)
    print "finished"

if __name__ == "__main__":
    main()

