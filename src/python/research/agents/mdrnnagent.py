__author__ = "Tom Schaul"

from agents.networkagent import ModuleMarioAgent
from pybrain.structure.networks.multidimensional import MultiDimensionalRNN
from pybrain.structure.networks.feedforward import FeedForwardNetwork
from pybrain.structure.modules.linearlayer import LinearLayer
from pybrain.structure.connections.full import FullConnection

class MdrnnAgent(ModuleMarioAgent):
    """ A MarioAgent, deciding on its actions using a special Multi-dimensional RNN. """
    
    
    def __init__(self, **args):
        self.setArgs(**args)
        net1 = MultiDimensionalRNN((22,22), hsize = 1)
        net = FeedForwardNetwork() 
        net.addInputModule(net1)
        o = LinearLayer(5)
        net.addOutputModule(o)
        mid = 22**2/2
        net.addConnection(FullConnection(net1, o, inSliceFrom = mid, inSliceTo = mid+1))
        net.sortModules()
        ModuleMarioAgent.__init__(self, net)
    
