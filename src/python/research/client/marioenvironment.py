__author__ = "Sergey Karakovskiy, sergey at idsia fullstop ch"
__date__ = "$May 13, 2009 1:29:41 AM$"

from tcpenvironment import TCPEnvironment
from utils.dataadaptor import extractObservation

class MarioEnvironment(TCPEnvironment):
    """
    Deals with Mario specific data
    """

    levelScene = None
    mayMarioJump = False
    isMarioOnGround = False

    def getSensors(self):
        data = TCPEnvironment.getSensors(self)
#        print "data: ", data
        return extractObservation(data)

    def reset(self):
        self.client.sendData("reset -ld 5 -lt 2 -pw off -zm 1 -mm 2 -vaot on -vis on -maxFPS off\r\n")
