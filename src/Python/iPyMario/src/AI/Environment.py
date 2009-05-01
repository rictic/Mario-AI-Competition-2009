from Utils.DataAdaptor import parseObservation
__author__="Sergey Karakovskiy, sergey at idsia fullstop ch"
__date__ ="$May 1, 2009 3:05:27 AM$"

class Environment:
    """
    Environment class stores the observation
    """
    levelScene = None
    mayMarioJump = False
    isMarioOnGround = False

    def __init__(self):
        """initializes with string observation of the following format:
        'mayMarioJump() isMarioOnGround() [ levelScene obs separated by spaces ]
        """
    def getObservation(self, obsStr):
        """This method calls to parseObservation in order to process the data"""

        ret = self.mayMarioJump, self.isMarioOnGround, self.levelScene \
                        = parseObservation(obsStr)
        return ret
