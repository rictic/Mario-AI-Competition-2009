__author__="Sergey Karakovskiy, sergey at idsia fullstop ch"
__date__ ="$May 7, 2009 12:47:18 PM$"

from AI.environments.MarioEnvironment import MarioEnvironment
from AI.tasks.EpisodicTask import EpisodicTask

if __name__ != "__main__":
    print "Loading %s ..." % __name__;

class MarioTask(EpisodicTask):
#    def __init__(self):
#        """Encapsulates Mario specific options and transfers them to EpisodicTask"""
#        EpisodicTask(MarioEnvironment())
#        EpisodicTask.reset(self)
#        pass
#
    def isFinished(self):
        pass

    def getReward(self):
        """ compute and return the current reward (i.e. corresponding to the last action performed) """
        return 1 
