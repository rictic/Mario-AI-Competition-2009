__author__ = "Tom Schaul"

from pybrain.rl.experiments.episodic import EpisodicExperiment
from tasks.mariotask import MarioTask
from agents.networkagent import MLPMarioAgent, SimpleMLPMarioAgent


def combinedScore(agent, task = None):
    """ Let the agent act on a number of levels of increasing difficulty. 
    Return the combined score."""
    if task == None:
        task = MarioTask(agent.name)
    exp = EpisodicExperiment(task, agent)
    res = 0
    for difficulty in range(12):
        for seed in range(15):
            task.env.levelSeed = seed
            task.env.levelDifficulty = difficulty  
            exp.doEpisodes(1)
            print 'Difficulty: %d, Seed: %d, Fitness: %.2f' % (difficulty, seed, task.reward)
            res += task.reward
    return res
    
def main():
    agent1 = SimpleMLPMarioAgent(2)
    print agent1.name
    f = combinedScore(agent1)
    print "\nTotal:", f
    
    agent2 = MLPMarioAgent(4)
    print agent2.name
    f = combinedScore(agent2)
    print "\nTotal:", f
    
    
if __name__ == "__main__":
    main()

