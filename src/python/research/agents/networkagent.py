__author__ = "Tom Schaul"

from agents.marioagent import MarioAgent
from scipy import zeros
from pybrain.tools.shortcuts import buildNetwork


class ModuleMarioAgent(MarioAgent):
    """ A MarioAgent that produces actions by evaluating a module on the level-scene input. """
    
    def __init__(self, module):
        assert module.indim == 22**2
        assert module.outdim == 6
        self.module = module
        self.lastobs = zeros(22**2)
        
    def getAction(self):
        return self.module.activate(self.lastobs)
        
    def integrateObservation(self, obs):
        self.lastobs = obs
        
        
def MLPMarioAgent(ModuleMarioAgent):
    """ Containing a Multi-layer Perceptron """
    
    def __init__(self, hidden = 10):
        net = buildNetwork(22**2, hidden, 6)
        ModuleMarioAgent.__init__(self, net)