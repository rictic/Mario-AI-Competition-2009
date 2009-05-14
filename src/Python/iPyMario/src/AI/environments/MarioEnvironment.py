__author__="Sergey Karakovskiy, sergey at idsia fullstop ch"
__date__ ="$May 13, 2009 1:29:41 AM$"

from AI.environments.TCPEnvironment import TCPEnvironment
from Utils.DataAdaptor import extractObservation

class MarioEnvironment(TCPEnvironment):
    """
    Deals with Mario specific data
    """

    levelScene = None
    mayMarioJump = False
    isMarioOnGround = False

    def getObservation(self):
        data = self.getSensors()
#        print "data: ", data
        return extractObservation(data)





