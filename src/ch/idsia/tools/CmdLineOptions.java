package ch.idsia.tools;

import ch.idsia.mario.engine.GlobalOptions;

import java.util.*;
import java.awt.Point;


/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 25, 2009
 * Time: 9:05:20 AM
 * Package: com.mojang.mario.Tools
 */
public class CmdLineOptions extends EvaluationOptions
{
    // TODO: SK Move default options to xml, properties, beans, whatever..
//    private SmartBool gui = new SmartBool();
//    private SmartBool toolsConfigurator = new SmartBool();
//    private SmartBool gameViewer = new SmartBool();
//    private SmartBool gameViewerContinuousUpdates = new SmartBool();
//    private SmartBool timer = new SmartBool();
//    private SmartInt attemptsNumber = new SmartInt();
//    private SmartBool echo = new SmartBool();
//    private SmartBool maxFPS = new SmartBool();
//    private SmartType<String> agentName = new SmartType<String>();
//    private SmartInt serverAgentPort = new SmartInt();
//    private SmartBool serverAgentEnabled = new SmartBool(false);
//    private SmartType<Point> viewLocation = new SmartType<Point>(new Point(0,0));
//    private SmartInt viewLocationX = new SmartInt(0);
//    private SmartInt viewLocationY = new SmartInt(0);
//
//    private SmartBool viewAlwaysOnTop = new SmartBool(false);

    public CmdLineOptions(String[] args)
    {
        super();
        // -agent wox name, like evolvable in simplerace
        // -ll digit  range [5:15], increase if succeeds.
        //TODO Load From File.

//        argsHashMap.put("-ag", agentName.setValue(GlobalOptions.defaults.getAgentName()));
//        argsHashMap.put("-agentName", agentName);
//        argsHashMap.put("-port", serverAgentPort.setValue(GlobalOptions.defaults.getServerAgentPort()));
//        argsHashMap.put("-visual", visualization.setValue(GlobalOptions.VisualizationOn));
//        argsHashMap.put("-vis", visualization);
//        argsHashMap.put("-viewAlwaysOnTop", viewAlwaysOnTop);
//        argsHashMap.put("-vaot", viewAlwaysOnTop);
//        argsHashMap.put("-gui", gui.setValue(GlobalOptions.defaults.isGui()));
//        argsHashMap.put("-levelDifficulty", levelDifficulty.setValue(GlobalOptions.defaults.getLevelDifficulty()));
//        argsHashMap.put("-ld", levelDifficulty);
//        argsHashMap.put("-levelLength", levelLength.setValue(GlobalOptions.defaults.getLevelLength()));
//        argsHashMap.put("-ll", levelLength);
//        argsHashMap.put("-levelType", levelType.setValue(GlobalOptions.defaults.getLevelType()));
//        argsHashMap.put("-lt", levelType);
//        argsHashMap.put("-levelRandSeed", levelRandSeed.setValue(GlobalOptions.defaults.getLevelRandSeed()));
//        argsHashMap.put("-ls", levelRandSeed);
//        argsHashMap.put("-toolsConfigurator", toolsConfigurator.setValue(GlobalOptions.defaults.isToolsConfigurator()) );
//        argsHashMap.put("-tc", toolsConfigurator);
//        argsHashMap.put("-gameViewer", gameViewer.setValue(GlobalOptions.defaults.isGameViewer()));
//        argsHashMap.put("-gv", gameViewer);
//        argsHashMap.put("-gameViewerContinuousUpdates", gameViewerContinuousUpdates.setValue(GlobalOptions.defaults.isGameViewerContinuousUpdates()));
//        argsHashMap.put("-gvc", gameViewerContinuousUpdates);
//        argsHashMap.put("-timer", timer.setValue(GlobalOptions.defaults.isTimer()));
//        argsHashMap.put("-t", timer);
////        argsHashMap.put("-verbose", GlobalOptions.defaults.getVerbose());
//        argsHashMap.put("-attemptsNumber", attemptsNumber.setValue(GlobalOptions.defaults.getAttemptsNumber()));
//        argsHashMap.put("-an", attemptsNumber);
//        argsHashMap.put("-echo", echo.setValue(GlobalOptions.defaults.isEcho()));
//        argsHashMap.put("-e", echo);
//        argsHashMap.put("-maxFPS", maxFPS.setValue(GlobalOptions.defaults.isMaxFPS()));
//        argsHashMap.put("-pw", pauseWorld.setValue(GlobalOptions.defaults.isPauseWorld()));
//        argsHashMap.put("-pauseWorld", pauseWorld);
//        argsHashMap.put("-powerRestoration", powerRestoration.setValue(GlobalOptions.defaults.isPowerRestoration()));
//        argsHashMap.put("-pr", powerRestoration);
//        argsHashMap.put("-stopSimulationIfWin", stopSimulationIfWin.setValue(GlobalOptions.defaults.isStopSimulationIfWin()));
//        argsHashMap.put("-ssiw", stopSimulationIfWin);
//        argsHashMap.put("-exitWhenFinished", exitProgramWhenFinished.setValue(GlobalOptions.defaults.isExitProgramWhenFinished()));
//        argsHashMap.put("-ewf", exitProgramWhenFinished);
//        argsHashMap.put("-viewLocationX", viewLocationX);
//        argsHashMap.put("-viewLocationY", viewLocationY);
//        argsHashMap.put("-vlx", viewLocationX);
//        argsHashMap.put("-vly", viewLocationY);
//        argsHashMap.put("-m", matlabFileName);

        this.ParseArgs(args);

        if (isEcho())
        {
            System.out.println("\nOptions have been set to:");
            for (Map.Entry<String,String> el : optionsHashMap.entrySet())
                System.out.println(el.getKey() + ": " + el.getValue());
        }
        GlobalOptions.VisualizationOn = isVisualization();
        GlobalOptions.GameVeiwerContinuousUpdatesOn = isGameViewerContinuousUpdates();
        GlobalOptions.FPS = (isMaxFPS()) ? GlobalOptions.InfiniteFPS : 24;
        GlobalOptions.pauseWorld = isPauseWorld();
        GlobalOptions.PowerRestoration = isPowerRestoration();
        GlobalOptions.StopSimulationIfWin = isStopSimulationIfWin();
    }

    public void ParseArgs(String[] args) {
        for (int i = 0; i < args.length - 1; i += 2)
            try
            {
                setParameterValue(args[i], args[i + 1]);
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                // Basically we can push the red button to explaud the computer, since this case cannot' never happen.
                System.err.println("Error: Wrong number of input parameters");
//                System.err.println("It is good day to kill yourself with the yellow wall");
            }
    }

//    public static void main(String[] args) {
//        CmdLineOptions cl = new CmdLineOptions(new String[]{"-ll", "42", "sadf", "AA", "-gv", "on", "sd", "lsd", "-echo", "-ll"});
//        System.out.println(cl.getLevelLength());
//        System.out.println(cl.isGameViewer());
//    }

    public Boolean isToolsConfigurator() {
        return b(getParameterValue("-tc"));      }
//
//        String value = (optionsHashMap.get("-tc") != null) ? optionsHashMap.get("-tc")  : optionsHashMap.get("-toolsConfigurator");
//        return (value == null) ? defaultOtionsHashMap.get("-toolsConfigurator").equals("on") : value.equals("on");
//    }

    public Boolean isGameViewer() {
        return b(getParameterValue("-gv"));      }
//        String value = (optionsHashMap.get("-gv") != null) ? optionsHashMap.get("-gv")  : optionsHashMap.get("-gameViewer");
//        return (value != null) && value.equals("on") || defaultOtionsHashMap.get("-gameViewer").equals("on");
//    }

    public Boolean isGameViewerContinuousUpdates() {
        return b(getParameterValue("-gvc"));      }

    public Boolean isTimer() {
        return b(getParameterValue("-t"));      }

    public Integer getAttemptsNumber() {
        return i(getParameterValue("-an"));      }

    public Boolean isEcho() {
        return b(getParameterValue("-echo"));      }

    public Boolean isMaxFPS() {
        return b(getParameterValue("-maxFPS"));      }

    public String getAgentName() {
        return getParameterValue("-ag");      }

    public Integer getServerAgentPort() {
        String value = optionsHashMap.get("-port");
        if (value == null)
        {
            if (getAgentName().startsWith("ServerAgent"))
            {
                if ( getAgentName().split(":").length > 1)
                {
                    return Integer.parseInt(getAgentName().split(":")[1]);
                }
            }
        }
        return Integer.parseInt(defaultOtionsHashMap.get("-port"));
    }

    public boolean isServerAgentEnabled() {
        return getAgentName().startsWith("ServerAgent");
    }

    public Point getViewLocation()
    {
        int x = i(getParameterValue("-vlx"));
        int y = i(getParameterValue("-vly"));
        return new Point(x, y);
    }

    public Boolean isViewAlwaysOnTop() {
        return b(getParameterValue("-vaot"));      }
}
