package ch.idsia.tools;

import ch.idsia.mario.simulation.BasicSimulator;
import ch.idsia.mario.simulation.ISimulation;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.engine.GlobalOptions;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 6, 2009
 * Time: 8:12:18 PM
 * Package: com.mojang.mario.Tools
 */

public class Evaluator implements Runnable
{
    Thread thisThread = null;
    EvaluatorOptions evaluatorOptions;

    private List<EvaluationInfo> EvaluationSummary = new ArrayList<EvaluationInfo>();
    private ConsoleHistory consoleHistory;

    public void Evaluate()
    {
        ISimulation simulator = new BasicSimulator(evaluatorOptions.getBasicSimulatorOptions());
        // Simulate One Level

        EvaluationInfo evaluationInfo;
        boolean continueCondition;

        long startTime = System.currentTimeMillis();
        consoleHistory.addRecord("Evaluation started at " + GlobalOptions.getDateTime(null));
        do
        {
            consoleHistory.addRecord("Attempts left: " + evaluatorOptions.maxAttempts);
            evaluationInfo = simulator.simulateOneLevel();
            evaluationInfo.levelType = evaluatorOptions.getLevelType();
            evaluationInfo.levelDifficulty = evaluatorOptions.getLevelDifficulty();
            evaluationInfo.levelRandSeed = evaluatorOptions.getLevelRandSeed();
            EvaluationSummary.add(evaluationInfo);
//            System.out.println("run  finished with result : " + evaluationInfo);

            continueCondition = !GlobalOptions.StopSimulationIfWin || !(evaluationInfo.marioStatus == Mario.STATUS_WIN);
        }
        while ( --evaluatorOptions.maxAttempts > 0 && continueCondition );

        Collections.sort(EvaluationSummary, new EvBasicFitnessComparator());

        consoleHistory.addRecord("Entire Evaluation Finished with results:");
        for (EvaluationInfo ev : EvaluationSummary)
        {
//            System.out.println(ev);
            consoleHistory.addRecord(ev.toString());
        }
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - startTime;
        consoleHistory.addRecord("Evaluation Finished at " + GlobalOptions.getDateTime(null));
        consoleHistory.addRecord("Total Evaluation Duration (HH:mm:ss:ms) " + GlobalOptions.getDateTime(elapsed));
    }

    public void reset()
    {
        EvaluationSummary = new ArrayList<EvaluationInfo>();
    }

    public Evaluator(EvaluatorOptions evaluatorOptions)
    {                      
        Init(evaluatorOptions);
    }

    public void run()
    {
        Evaluate();
    }

    public void start()
    {
        thisThread.start();
    }

//    public void stop()
//    {
//        thisThread.stop();
//    }

    public void Init(EvaluatorOptions evaluatorOptions)
    {
        Mario.resetStatic();
        this.evaluatorOptions = evaluatorOptions;
        thisThread = new Thread(this);
    }

    public void setConsole(ConsoleHistory consoleHistory) {
        this.consoleHistory = consoleHistory;
    }
}

class EvBasicFitnessComparator implements Comparator
{
    public int compare(Object o, Object o1)
    {
        double ei1Fitness = ((EvaluationInfo)(o)).computeBasicFitness();
        double ei2Fitness = ((EvaluationInfo)(o1)).computeBasicFitness();
        if (ei1Fitness < ei2Fitness)
            return 1;
        else if (ei1Fitness > ei2Fitness)
            return -1;
        else
            return 0;
    }
}

class EvCoinsFitnessComparator implements Comparator
{
    public int compare(Object o, Object o1)
    {
        double ei1Fitness = ((EvaluationInfo)(o)).computeCoinsFitness();
        double ei2Fitness = ((EvaluationInfo)(o1)).computeCoinsFitness();
        if (ei1Fitness < ei2Fitness)
            return 1;
        else if (ei1Fitness > ei2Fitness)
            return -1;
        else
            return 0;
    }
}

class EvDistanceFitnessComparator implements Comparator
{
    public int compare(Object o, Object o1)
    {
        double ei1Fitness = ((EvaluationInfo)(o)).computeDistancePassed();
        double ei2Fitness = ((EvaluationInfo)(o1)).computeDistancePassed();
        if (ei1Fitness < ei2Fitness)
            return 1;
        else if (ei1Fitness > ei2Fitness)
            return -1;
        else
            return 0;
    }
}


//Create MarioComponent

// Create LevelScene

//Visualization

//StartLevel



//        ForwardAgent fwa = new ForwardAgent();
//        fwa.reset();
//        Evaluate(fwa, LevelGenerator.TYPE_OVERGROUND, 1, 0, 320, true, true);

//        marioComponent.startLevel(93004739, 80, LevelGenerator.TYPE_CASTLE);
////        marioComponent.startLevel(93004739, 5, LevelGenerator.TYPE_OVERGROUND);
////        marioComponent.start();
//        marioComponent.run1();
//        marioComponent.startLevel(93004739*2, 1, LevelGenerator.TYPE_OVERGROUND);
//        marioComponent.run1();


//        marioComponent.startLevel(1, 1, LevelGenerator.TYPE_OVERGROUND);
//        System.out.println("run TYPE_OVERGROUND finished with result : " + marioComponent.run1());

//        int resultStatus = 0;
//        do
//        {
//            marioComponent.startLevel(1, 1, LevelGenerator.TYPE_OVERGROUND);
//            resultStatus = marioComponent.run1();
//            System.out.println("run TYPE_OVERGROUND finished with result : " + resultStatus);
//        }
//        while (resultStatus != 1);

//        marioComponent.startLevel(1, 0, LevelGenerator.TYPE_UNDERGROUND);
//        System.out.println("run TYPE_UNDERGROUND finished with result : " + marioComponent.run1());

//        if (level[x][y] == TILE_LEVEL && data[x][y] != 0 && data[x][y] > -10)
//
//        {
//            Mario.levelString = (worldNumber + 1) + "-";
//            int difficulty = worldNumber+1;
//            int type = LevelGenerator.TYPE_OVERGROUND;
//            if (data[x][y] > 1 && new Random(seed + x * 313211 + y * 534321).nextInt(3) == 0)
//            {
//                type = LevelGenerator.TYPE_UNDERGROUND;
//            }
//            if (data[x][y] < 0)
//            {
//                if (data[x][y] == -2)
//                {
//                    Mario.levelString += "X";
//                    difficulty += 2;
//                }
//                else if (data[x][y] == -1)
//                {
//                    Mario.levelString += "?";
//                }
//                else
//                {
//                    Mario.levelString += "#";
//                    difficulty += 1;
//                }
//
//                type = LevelGenerator.TYPE_CASTLE;
//            }
//            else
//            {
//                Mario.levelString += data[x][y];
//            }
//        }
