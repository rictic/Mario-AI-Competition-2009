package ch.idsia.ai;

import ch.idsia.ai.ea.ES;
import ch.idsia.ai.tasks.Task;
import ch.idsia.ai.tasks.ProgressTask;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 4, 2009
 * Time: 4:33:25 PM
 */
public class Evolve {

    final static int generations = 100;
    final static int populationSize = 100;

    public static void main(String[] args) {
        Evolvable initial = new SimpleMLPAgent ();
        Task task = new ProgressTask();
        ES es = new ES (task, initial, populationSize);
        for (int gen = 0; gen < generations; gen++) {
            es.nextGeneration();
            System.out.println("Generation " + gen + " best " + es.getBestFitnesses()[0]);
        }
    }

}
