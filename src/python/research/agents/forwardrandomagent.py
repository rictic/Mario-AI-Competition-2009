__author__ = "Sergey Karakovskiy, sergey at idsia fullstop ch; Tom Schaul"
__date__ = "$Apr 30, 2009 6:46:04 PM$"

import random
from agents.forwardagent import ForwardAgent


class ForwardRandomAgent(ForwardAgent):
    """
    Very simple example of an agent, who does not respect the observations,
    but just generates random forward moves and jumps
    """

    def getAction(self):
        fwd =       "01000\r\n"
        fwdjump =   "01010\r\n"
        actions = [fwd, fwdjump]
        return actions[random.randint(0, len(actions) - 1)]

