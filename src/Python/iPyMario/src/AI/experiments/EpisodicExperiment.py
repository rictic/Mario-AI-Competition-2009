__author__="Sergey Karakovskiy, sergey at idsia fullstop ch"
__date__ ="$May 12, 2009 11:18:19 PM$"

from experiment import Experiment


#class EpisodicExperiment(Experiment):
#    """ The extension of Experiment to handle episodic tasks. """
#
#    def doEpisodes(self, number = 1):
#        """ returns the rewards of each step as a list """
#        all_rewards = []
#        for dummy in range(number):
#            rewards = []
#            self.stepid = 0
#            # the agent is informed of the start of the episode
#            self.agent.newEpisode()
#            self.task.reset()
#            while not self.task.isFinished():
#                r = self._oneInteraction()
#                rewards.append(r)
#            all_rewards.append(rewards)
#        return all_rewards


class EpisodicExperiment(Experiment):
    """
    Documentation
    """

    agent = None
    task = None

    def __init__(self, agent, task):
        """Documentation"""
        self.agent = agent
        self.task = task

    def doEpisodes(self, amount):
        for i in range(amount):
            while not self.task.isFinished():
                j, g, l = self.task.getObservation()
                self.agent.integrateObservation(j, g, l)
                a = self.agent.produceAction()
                self.task.performAction(a)
                r = self.task.getReward()
                self.agent.grantReward(r)
#        if not agent.isConnected():
            print "Task is no longer available. Finishing episode %d..." % i