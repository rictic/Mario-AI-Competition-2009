package ch.idsia.ai;

import ch.idsia.ai.ea.ES;
import ch.idsia.ai.tasks.Task;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.mario.engine.level.LevelGenerator;
import ch.idsia.mario.engine.MarioComponent;
import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.tools.EvaluatorOptions;
import ch.idsia.tools.GameViewer;
import ch.idsia.tools.ToolsConfigurator;

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
        //GlobalOptions.VisualizationOn = false;
        EvaluatorOptions options = new EvaluatorOptions ();
        options.setLevelLength(320);
        options.setLevelDifficulty(0);
        options.setLevelRandSeed(1);
        options.setVisualization(false);
        options.setLevelType(LevelGenerator.TYPE_OVERGROUND);
        options.setPauseWorld(false);
        options.setPowerRestoration(false);
        options.setStopSimulationIfWin(false);
        MarioComponent marioComponent = new MarioComponent(320, 240, null);
        GameViewer gameViewer = new GameViewer (null, null);
        marioComponent.setGameViewer(gameViewer);
        ToolsConfigurator.CreateMarioComponentFrame();
        marioComponent.init();
        options.setMarioComponent(marioComponent);
        Evolvable initial = new SimpleMLPAgent ();
        Task task = new ProgressTask();
        task.setOptions(options);
        ES es = new ES (task, initial, populationSize);
        for (int gen = 0; gen < generations; gen++) {
           es.nextGeneration();
            System.out.println("Generation " + gen + " best " + es.getBestFitnesses()[0]);
        }
    }

}
