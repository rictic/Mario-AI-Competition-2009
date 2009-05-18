from pybrain.structure.modules.sigmoidlayer import SigmoidLayer
from pybrain.utilities import fListToString
__author__ = "Tom Schaul"

from agents.marioagent import MarioAgent
from scipy import zeros, array, ravel, rand
from pybrain.tools.shortcuts import buildNetwork


class ModuleMarioAgent(MarioAgent):
    """ A MarioAgent that produces actions by evaluating a module on the level-scene input. 
    The module takes as input an 1x484 numpy array, and produces a 1x5 numpy array as output,
    with each entry corresponding to the probablility of taking the corresponding action
    (left, right, down, jump, speed/shoot). """
    
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
        return res
        
    def integrateObservation(self, obs):
        if len(obs) == 3:
            self.lastobs = ravel(obs[2])
            
                        
class SimpleModuleAgent(ModuleMarioAgent):
    """ Like parent class, but simplifying inputs to a 7x7 array, and outputs to 
    only 3 actions (right, jump, speed/shoot). """
    
    def __init__(self, module):
        assert module.indim == 7**2
        assert module.outdim == 3
        self.module = module
        self.lastobs = zeros(7**2)

    def getAction(self):
        out = self.module.activate(self.lastobs)
        res = zeros(5, int)
        for i, v in zip([1,3,4], out):
            if rand() < v:
                res[i] = 1
        return res
        
    def integrateObservation(self, obs):
        if len(obs) == 3:
            middle = obs[2][8:15, 8:15]
            self.lastobs = ravel(middle)
        
        
class MLPMarioAgent(ModuleMarioAgent):
    """ Containing a Multi-layer Perceptron """
    
    def __init__(self, hidden):
        net = buildNetwork(22**2, hidden, 5, outclass = SigmoidLayer)
        ModuleMarioAgent.__init__(self, net)
        
        
class SimpleMLPMarioAgent(SimpleModuleAgent):
    """ Containing a Multi-layer Perceptron """
    
    def __init__(self, hidden):
        net = buildNetwork(7**2, hidden, 3, outclass = SigmoidLayer)
        SimpleModuleAgent.__init__(self, net)
        