import numpy
__author__="Sergey Karakovskiy, sergey at idsia fullstop ch"
__date__ ="$May 1, 2009 2:46:34 AM$"

from AI.agents.Agent import Agent

if __name__ != "__main__":
    print "Importing %s " % __name__;

class ForwardAgent(Agent):
    """ In fact the Python twin of the
        corresponding Java ForwardAgent.
    """
    action = None
    actionStr = None
    KEY_JUMP = 3
    KEY_SPEED = 4
    levelScene = None
    mayMarioJump = None
    isMarioOnGround = None

    trueJumpCounter = 0;
    trueSpeedCounter = 0;

    def __init__(self):
        """Constructor"""
        self.trueJumpCounter = 0
        self.trueSpeedCounter = 0
        self.action = numpy.zeros(5, int)
        self.action[1] = 1
        self.actionStr = ""
        pass

    def _dangerOfGap(self):
        for x in range(9,13):
            f = True
            for y in range(12,22):
                if  (self.levelScene[y,x] != 0):
                    f = False
            if (f and self.levelScene[12,11] != 0):
                return True
        return False


    def _a2(self):
        """ Interesting, sometimes very useful behaviour which might prevent falling down into a gap!
        Just substitue getAction by this method and see how it behaves.

        Get observation, (possibly analyse it), sent an action back
        @param obs: observation from the environment
        @type obs: by default, this is assumed to be a numpy array of doubles
        """
        if (self.mayMarioJump):
                    print "m: %d, %s, %s, 12: %d, 13: %d, j: %d" \
            % (self.levelScene[11,11], self.mayMarioJump, self.isMarioOnGround, \
            self.levelScene[11,12], self.levelScene[11,12], self.trueJumpCounter)
        else:
            if self.levelScene == None:
                print "Bad news....."
            print "m: %d, 12: %d, 13: %d, j: %d" \
                % (self.levelScene[11,11], \
                self.levelScene[11,12], self.levelScene[11,12], self.trueJumpCounter)

        a = numpy.zeros(5, int)
        a[1] = 1

        danger = self._dangerOfGap()
        if (self.levelScene[11,12] != 0 or \
            self.levelScene[11,13] != 0 or danger):
            if (self.mayMarioJump or \
                ( not self.isMarioOnGround and a[self.KEY_JUMP] == 1)):
                a[self.KEY_JUMP] = 1
            self.trueJumpCounter += 1
        else:
            a[self.KEY_JUMP] = 0;
            self.trueJumpCounter = 0

        if (self.trueJumpCounter > 16):
            self.trueJumpCounter = 0
            self.action[self.KEY_JUMP] = 0;

        a[self.KEY_SPEED] = danger

        actionStr = ""

        for i in range(5):
            if a[i] == 1:
                actionStr += '1'
            elif a[i] == 0:
                actionStr += '0'
            else:
                print "something very dangerous happen...."

        actionStr += "\r\n"
        print "action: " , actionStr
        return actionStr

    def produceAction(self):
        """Get observation, (possibly analyse it), sent an action back
        @param obs: observation from the environment
        @type obs: by default, this is assumed to be a numpy array of doubles
        """
        print "M: mayJump: %s, onGround: %s, level[11,12]: %d, level[11,13]: %d, jc: %d" \
            % (self.mayMarioJump, self.isMarioOnGround, self.levelScene[11,12], \
            self.levelScene[11,13], self.trueJumpCounter)

        danger = self._dangerOfGap()
        if (self.levelScene[11,12] != 0 or \
            self.levelScene[11,13] != 0 or danger):
            if (self.mayMarioJump or \
                ( not self.isMarioOnGround and self.action[self.KEY_JUMP] == 1)):
                self.action[self.KEY_JUMP] = 1
            self.trueJumpCounter += 1
        else:
            self.action[self.KEY_JUMP] = 0;
            self.trueJumpCounter = 0

        if (self.trueJumpCounter > 16):
            self.trueJumpCounter = 0
            self.action[self.KEY_JUMP] = 0;

        self.action[self.KEY_SPEED] = danger
        return self.action
#        self.actionStr = ""
#
#        for i in range(5):
#            if self.action[i] == 1:
#                self.actionStr += '1'
#            elif self.action[i] == 0:
#                self.actionStr += '0'
#            else:
#                print "something very dangerous happen...."
#
#        self.actionStr += "\r\n"
#        print "action: " , self.actionStr
#        return self.actionStr

    def integrateObservation(self, mayMarioJump, isMarioOnGround, levelScene):
        """This method stores the observation inside the agent"""
        self.mayMarioJump, self.isMarioOnGround, self.levelScene = \
            mayMarioJump, isMarioOnGround, levelScene
                                    
        self.printLevelScene()

    def printLevelScene(self):
        ret = ""
        for x in range(22):
            tmpData = ""
            for y in range(22):
                tmpData += self.mapElToStr(self.levelScene[x][y]);
            ret += "\n%s" % tmpData;
        print ret

    def mapElToStr(self, el):
        """maps element of levelScene to str representation"""
        s = "";
        if  (el == 0):
            s = "##"
        s += "#MM#" if (el == 95) else str(el)
        while (len(s) < 4):
            s += "#";
        return s + " "

    def printObs(self):
        """for debug"""
        print repr(self.observation)