package ch.idsia.tools;

import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.simulation.BasicSimulator;
import ch.idsia.mario.simulation.ISimulation;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    EvaluationOptions evaluationOptions;

    private List<EvaluationInfo> evaluationSummary = new ArrayList<EvaluationInfo>();

    public List<EvaluationInfo> evaluate()
    {
        ISimulation simulator = new BasicSimulator(evaluationOptions.getSimulationOptionsCopy());
        // Simulate One Level

        EvaluationInfo evaluationInfo;

        long startTime = System.currentTimeMillis();
        String startMessage = "Evaluation started at " + GlobalOptions.getDateTime(null);
        LOGGER.println(startMessage, LOGGER.VERBOSE_MODE.ALL);

        boolean continueCondition;
        int i = 0;
        do
        {
            LOGGER.println("Attempts left: " + (evaluationOptions.getMaxAttempts() - ++i ), LOGGER.VERBOSE_MODE.ALL);
            evaluationInfo = simulator.simulateOneLevel();
            
            evaluationInfo.levelType = evaluationOptions.getLevelType();
            evaluationInfo.levelDifficulty = evaluationOptions.getLevelDifficulty();
            evaluationInfo.levelRandSeed = evaluationOptions.getLevelRandSeed();
            evaluationSummary.add(evaluationInfo);
            for (int j = 0; j < 5000;  j++)
            {
             LOGGER.println("Writing to log " + j , LOGGER.VERBOSE_MODE.INFO);
             LOGGER.println("run  finished with result : " + evaluationInfo, LOGGER.VERBOSE_MODE.ALL);
            }
            continueCondition = !GlobalOptions.StopSimulationIfWin || !(evaluationInfo.marioStatus == Mario.STATUS_WIN);
        }
        while ( evaluationOptions.getMaxAttempts() > i && continueCondition );

        String fileName = "";
        if (!this.evaluationOptions.getMatlabFileName().equals(""))
           fileName = exportToMatLabFile();
        Collections.sort(evaluationSummary, new evBasicFitnessComparator());

        LOGGER.println("Entire Evaluation Finished with results:", LOGGER.VERBOSE_MODE.ALL);
        for (EvaluationInfo ev : evaluationSummary)
        {
//             LOGGER.println(ev.toString(), LOGGER.VERBOSE_MODE.ALL);
        }
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - startTime;
         LOGGER.println(startMessage, LOGGER.VERBOSE_MODE.ALL);
         LOGGER.println("Evaluation Finished at " + GlobalOptions.getDateTime(null), LOGGER.VERBOSE_MODE.ALL);
         LOGGER.println("Total Evaluation Duration (HH:mm:ss:ms) " + GlobalOptions.getDateTime(elapsed), LOGGER.VERBOSE_MODE.ALL);
        if (!fileName.equals(""))
            LOGGER.println("Exported to " + fileName, LOGGER.VERBOSE_MODE.ALL);
//        if (evaluationOptions.isExitProgramWhenFinished())
//            System.exit(0);
        return evaluationSummary;
    }

    public void verbose(String message, LOGGER.VERBOSE_MODE verbose_mode)
    {
        LOGGER.println(message, verbose_mode);
    }

    public void getMeanEvaluationSummary()
    {
        //TODO: SK
    }

    public String exportToMatLabFile()
    {
        FileOutputStream fos = null;
        String fileName = this.evaluationOptions.getMatlabFileName() + ".m";
        try {

            fos = new FileOutputStream(fileName);              
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            int i = 0;
            bw.newLine();
            bw.write("%% " + this.evaluationOptions.getAgent().getName());
            bw.newLine();
            bw.write("% BasicFitness ");            
            bw.newLine();
            bw.write("Attempts = [1:" + evaluationSummary.size() + "];");
            bw.newLine();
            bw.write("% BasicFitness ");
            bw.newLine();
            bw.write("BasicFitness = [");
            for (EvaluationInfo ev : evaluationSummary)
                bw.write(String.valueOf(ev.computeBasicFitness()) + " ");
            bw.write("];");
            bw.newLine();
            bw.write("plot(Attempts,BasicFitness, '.')");
            bw.close();
            return fileName;
        }
        catch (FileNotFoundException e)  {  e.printStackTrace(); return "Null" ;       }
        catch (IOException e) {     e.printStackTrace();  return "Null";      }
    }

    public void exportToPyPlot(String fileName)
    {
        //TODO:SK
    }

    public void reset()
    {
        evaluationSummary = new ArrayList<EvaluationInfo>();
    }

    public Evaluator(EvaluationOptions evaluationOptions)
    {                      
        init(evaluationOptions);
    }

    public void run()
    {
        evaluate();
    }

    public void start()
    {
        thisThread.start();
    }

    public void init(EvaluationOptions evaluationOptions)
    {
        ToolsConfigurator.CreateMarioComponentFrame(evaluationOptions.getViewLocation(),
                                                    evaluationOptions.isViewAlwaysOnTop(),
                                                    evaluationOptions.isVisualization());
        Mario.resetStatic();
        this.evaluationOptions = evaluationOptions;
        thisThread = new Thread(this);
    }
}

class evBasicFitnessComparator implements Comparator
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

class evCoinsFitnessComparator implements Comparator
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

class evDistanceFitnessComparator implements Comparator
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