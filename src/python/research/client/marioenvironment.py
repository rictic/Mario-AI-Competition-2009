__author__ = "Sergey Karakovskiy, sergey at idsia fullstop ch"
__date__ = "$May 13, 2009 1:29:41 AM$"

from tcpenvironment import TCPEnvironment
from utils.dataadaptor import extractObservation

class MarioEnvironment(TCPEnvironment):
    """ An Environment class, wrapping access to the MarioServer, 
    and allowing interactions to a level. """

    # Level settings
    levelDifficulty = 5
    levelType = 2
    creaturesEnabled = True
    initMarioMode = 2
    
    # Other settings
    visualization = True

    def getSensors(self):
        data = TCPEnvironment.getSensors(self)
#        print "data: ", data
        return extractObservation(data)

    def reset(self):
        argstring = "-ld %d -lt %d -vis %d -mm %d " % (self.levelDifficulty,
                                                            self.levelType,
                                                            self.visualization,
                                                            self.initMarioMode,
                                                            )
        if self.creaturesEnabled:
            argstring += "-pw off "
        else:
            argstring += "-pw on "
        argstring += "-zm 1 -vaot on -maxFPS off "
        print argstring
        self.client.sendData("reset "+argstring+"\r\n")
