package ch.idsia.tools;

import ch.idsia.mario.simulation.BasicSimulator;
import ch.idsia.mario.simulation.ISimulation;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.engine.GlobalOptions;

import java.util.*;
import java.util.List;
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
    EvaluationOptions evaluationOptions;

    private List<EvaluationInfo> evaluationSummary = new ArrayList<EvaluationInfo>();
    private ConsoleHistory consoleHistory;

    public List<EvaluationInfo> evaluate()
    {
        if (consoleHistory == null)
            setConsole(new ConsoleHistory(null));
        ISimulation simulator = new BasicSimulator(evaluationOptions.getSimulationOptionsCopy());
        // Simulate One Level

        EvaluationInfo evaluationInfo;

        long startTime = System.currentTimeMillis();
        String startMessage = "Evaluation started at " + GlobalOptions.getDateTime(null);
        consoleHistory.addRecord(startMessage);

        boolean continueCondition;
        int i = 0;
        do
        {
            consoleHistory.addRecord("Attempts left: " + (evaluationOptions.getMaxAttempts() - ++i ));
            //TODO: SK place in common place for options.
            evaluationInfo = simulator.simulateOneLevel();
            evaluationInfo.levelType = evaluationOptions.getLevelType();
            evaluationInfo.levelDifficulty = evaluationOptions.getLevelDifficulty();
            evaluationInfo.levelRandSeed = evaluationOptions.getLevelRandSeed();
            evaluationSummary.add(evaluationInfo);
//            System.out.println("run  finished with result : " + evaluationInfo);
            continueCondition = !GlobalOptions.StopSimulationIfWin || !(evaluationInfo.marioStatus == Mario.STATUS_WIN);
        }
        while ( evaluationOptions.getMaxAttempts() > i && continueCondition );

        String fileName = "";
        if (!this.evaluationOptions.getMatlabFileName().equals(""))
           fileName = exportToMatLabFile();
        Collections.sort(evaluationSummary, new evBasicFitnessComparator());

        // consoleHistory.addRecord("Entire Evaluation Finished with results:");
        for (EvaluationInfo ev : evaluationSummary)
        {
            // consoleHistory.addRecord(ev.toString());
        }
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - startTime;
        // consoleHistory.addRecord(startMessage);
        // consoleHistory.addRecord("Evaluation Finished at " + GlobalOptions.getDateTime(null));
        // consoleHistory.addRecord("Total Evaluation Duration (HH:mm:ss:ms) " + GlobalOptions.getDateTime(elapsed));
//        if (!fileName.equals(""))
//            consoleHistory.addRecord("Exported to " + fileName);
//        if (evaluationOptions.isExitProgramWhenFinished())
//            System.exit(0);
        return evaluationSummary;
    }

    public void verbose(String message)
    {
        if (consoleHistory == null)
            setConsole(new ConsoleHistory(null));
        consoleHistory.addRecord(message);
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

    public void setConsole(ConsoleHistory consoleHistory) {
        this.consoleHistory = consoleHistory;
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