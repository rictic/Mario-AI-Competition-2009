package ch.idsia.tools;

import ch.idsia.mario.simulation.BasicSimulator;
import ch.idsia.mario.simulation.ISimulation;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.engine.GlobalOptions;

import java.util.*;
import java.io.*;

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
        String startMessage = "Evaluation started at " + GlobalOptions.getDateTime(null);
        consoleHistory.addRecord(startMessage);

        do
        {
            consoleHistory.addRecord("Attempts left: " + evaluatorOptions.maxAttempts);
            //TODO: SK place in common place for options.
            evaluationInfo = simulator.simulateOneLevel();
            evaluationInfo.levelType = evaluatorOptions.getLevelType();
            evaluationInfo.levelDifficulty = evaluatorOptions.getLevelDifficulty();
            evaluationInfo.levelRandSeed = evaluatorOptions.getLevelRandSeed();
            EvaluationSummary.add(evaluationInfo);
//            System.out.println("run  finished with result : " + evaluationInfo);
            continueCondition = !GlobalOptions.StopSimulationIfWin || !(evaluationInfo.marioStatus == Mario.STATUS_WIN);
        }
        while ( --evaluatorOptions.maxAttempts > 0 && continueCondition );

        String fileName = exportToMatLabFile();
        Collections.sort(EvaluationSummary, new EvBasicFitnessComparator());

        consoleHistory.addRecord("Entire Evaluation Finished with results:");
        for (EvaluationInfo ev : EvaluationSummary)
        {
            consoleHistory.addRecord(ev.toString());
        }
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - startTime;
        consoleHistory.addRecord(startMessage);
        consoleHistory.addRecord("Evaluation Finished at " + GlobalOptions.getDateTime(null));
        consoleHistory.addRecord("Total Evaluation Duration (HH:mm:ss:ms) " + GlobalOptions.getDateTime(elapsed));
        consoleHistory.addRecord("Exported to " + fileName);
        if (evaluatorOptions.isExitProgramWhenFinished())
            System.exit(0);
    }

    public void getMeanEvaluationSummary()
    {
        //TODO: SK
    }

    public String exportToMatLabFile()
    {
        FileOutputStream fos = null;
        String fileName = this.evaluatorOptions.getMatlabFileName() + ".m";
        try {

            fos = new FileOutputStream(fileName);              
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            int i = 0;
            bw.newLine();
            bw.write("%% " + this.evaluatorOptions.getAgent().getName());
            bw.newLine();
            bw.write("% BasicFitness ");            
            bw.newLine();
            bw.write("Attempts = [1:" + EvaluationSummary.size() + "];");
            bw.newLine();
            bw.write("% BasicFitness ");
            bw.newLine();
            bw.write("BasicFitness = [");
            for (EvaluationInfo ev : EvaluationSummary)
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