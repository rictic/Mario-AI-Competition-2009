package ch.idsia.ai.ea;

import ch.idsia.ai.ea.CrossoverEvolvable;
import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.tasks.Task;

public class GA
{
	private final CrossoverEvolvable[] population;
	private final double[] fitness;
	private int generation = 0;
	private CrossoverEvolvable bestEver;
	private double bestFitnessEver;
	
	private static final int tournamentSize = 5;
	private static final double probCrossover = 0.50;
	
	private final Task task;
	
    public GA(Task task, CrossoverEvolvable initial, int populationSize)
    {
    	this.population = new CrossoverEvolvable[populationSize];
    	for (int i = 0; i < populationSize; i++)
    	{
    		this.population[i] = initial.getNewInstance();
    	}
    	this.fitness = new double[populationSize];
    	this.task = task;
    	
    	// Evaluate the first generation
    	for (int i = 0; i < population.length; i++)
    	{
    		evaluate(i);
    	}
    	
    	sortPopulationByFitness();
    	bestEver = population[0];
    	bestFitnessEver = fitness[0];
    }
    
    private int randomMemberIndex()
    {
    	return (int)(Math.random() * population.length);
    }

    public void nextGeneration()
    {
    	printStats();
    	
    	// Perform tournament selection until half the population is replaced
    	int numReplaced = 0, randomMemberA, randomMemberB;
    	while (numReplaced < population.length / 2)
    	{
    		if (Math.random() < GA.probCrossover)
    		{
    			// Perform crossover on two tournament selected members
    			randomMemberA = nextTournamentResult();
    			randomMemberB = nextTournamentResult();
    			CrossoverEvolvable[] result = population[randomMemberA].crossover(population[randomMemberB]);
    			
    			// Replace the two random members with their offspring
    			population[randomMemberA] = result[0];
    			population[randomMemberB] = result[1];
    			
    			// Evaluate the new offspring
    			evaluate(randomMemberA);
    			evaluate(randomMemberB);
    			
    			numReplaced += 2;
    		}
    		else
    		{
    			randomMemberA = nextTournamentResult();
    			population[randomMemberA].mutate();
    			evaluate(randomMemberA);
    			numReplaced += 1;
    		}
    	}
    	
    	sortPopulationByFitness();
    	if (fitness[0] > bestFitnessEver)
    	{
    		bestEver = population[0];
    		bestFitnessEver = fitness[0];
    	}
    	
    	this.generation++;
    }

    private void evaluate(int which)
    {
    	fitness[which] = task.evaluate((Agent)population[which])[0];
    }
    
    private void printStats()
    {
    	double avgFitness = 0.0;
    	double maxFitness = 0.0;
    	double minFitness = 1.0;
    	
    	for (int i = 0; i < fitness.length; i++)
    	{
    		avgFitness += fitness[i];
    		
    		if (fitness[i] > maxFitness)
    		{
    			maxFitness = fitness[i];
    		}
    		
    		if (fitness[i] < minFitness)
    		{
    			minFitness = fitness[i];
    		}
    	}
    	
    	avgFitness /= (double)fitness.length;
    	
    	System.out.println("Stats for Generation " + generation + ":");
    	System.out.println(" - Average Fitness: " + avgFitness);
    	System.out.println(" - Max Fitness: " + maxFitness);
    	System.out.println(" - Min Fitness: " + minFitness);
    }
    
    private int nextTournamentResult()
    {
    	// Evaluate a random sample of GA.tournamentSize and select member with best fitness 
    	int randomMember, bestMember = -1;
    	
    	for (int i = 0; i < GA.tournamentSize; i++)
    	{
    		randomMember = randomMemberIndex();
    		
    		// If there is no best member
    		if (bestMember == -1)
    		{
    			// The random member is the current best
    			bestMember = randomMember;
    		}
    		else
    		{
    			// Otherwise if the random member has higher fitness than the best member
    			if (fitness[randomMember] > fitness[bestMember])
    			{
    				// The random member becomes the best member
    				bestMember = randomMember;
    			}
    		}
    	}
    	
    	return bestMember;
    }

    private void sortPopulationByFitness() {
        for (int i = 0; i < population.length; i++) {
            for (int j = i + 1; j < population.length; j++) {
                if (fitness[i] < fitness[j]) {
                    swap(i, j);
                }
            }
        }
    }

    private void swap(int i, int j) {
        double cache = fitness[i];
        fitness[i] = fitness[j];
        fitness[j] = cache;
        CrossoverEvolvable gcache = population[i];
        population[i] = population[j];
        population[j] = gcache;
    }

    public CrossoverEvolvable[] getBests() {
        return new CrossoverEvolvable[]{population[0]};
    }
    
    public CrossoverEvolvable bestEver()
    {
    	return bestEver;
    }
    
    public double bestFitnessEver()
    {
    	return bestFitnessEver();
    }

    public double[] getBestFitnesses() {
        return new double[]{fitness[0]};  //To change body of implemented methods use File | Settings | File Templates.
    }
}