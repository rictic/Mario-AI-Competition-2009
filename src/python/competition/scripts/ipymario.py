__author__="Sergey Karakovskiy, sergey at idsia dot ch"
__date__ ="$Apr 30, 2009 1:46:32 AM$"

import sys
from ai.environments.marioenvironment import MarioEnvironment
from ai.experiments.episodicexperiment import EpisodicExperiment
from ai.tasks.mariotask import MarioTask
from utils.cmdlineoptions import CmdLineOptions

#from pybrain.... episodic import EpisodicExperiment


#TODO: reset sends: vis, diff=, lt=, ll=, rs=, mariomode, time limit, pw,
# with creatures, without creatures HIGH.
# send creatures.

def main():
    clo = CmdLineOptions(sys.argv)
    task = MarioTask(MarioEnvironment(clo.getHost(), clo.getPort(), clo.getAgent().name))
    exp = EpisodicExperiment(clo.getAgent(), task)
    exp.doEpisodes(3)

if __name__ == "__main__":
    main()
else:
    print "This is module to be run rather than imported."
