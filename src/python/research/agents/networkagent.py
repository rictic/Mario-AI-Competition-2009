from pybrain.structure.modules.sigmoidlayer import SigmoidLayer
from pybrain.utilities import fListToString
__author__ = "Tom Schaul"

from agents.marioagent import MarioAgent
from scipy import zeros, array, ravel, rand
from pybrain.tools.shortcuts import buildNetwork


class ModuleMarioAgent(MarioAgent):
    """ A MarioAgent that produces actions by evaluating a module on the level-scene input. """
    
    def __init__(self, module):
        assert module.indim == 22**2
        assert module.outdim == 5
        self.module = module
        self.lastobs = zeros(22**2)
        
    def getAction(self):
        out = self.module.activate(self.lastobs)
        res = zeros(5, int)
        for i in range(5):
            if rand() < out[i]:
                res[i] = 1
        print res, fListToString(out, 2)
        return res
        
    def integrateObservation(self, obs):
        if len(obs) == 3:
            self.lastobs = ravel(array(obs[2]))
        
        
class MLPMarioAgent(ModuleMarioAgent):
    """ Containing a Multi-layer Perceptron """
    
    def __init__(self, hidden = 10):
        net = buildNetwork(22**2, hidden, 5, outclass = SigmoidLayer)
        ModuleMarioAgent.__init__(self, net)
        