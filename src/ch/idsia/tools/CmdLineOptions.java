package ch.idsia.tools;

import ch.idsia.engine.GlobalOptions;
import ch.idsia.utils.ISmart;
import ch.idsia.utils.SmartBool;
import ch.idsia.utils.SmartInt;
import ch.idsia.utils.SmartType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 25, 2009
 * Time: 9:05:20 AM
 * Package: com.mojang.mario.Tools
 */
public class CmdLineOptions extends EvaluatorOptions
{
    private SmartBool gui = new SmartBool();
    private SmartBool toolsConfigurator = new SmartBool();
    private SmartBool gameViewer = new SmartBool();
    private SmartBool gameViewerContinuousUpdates = new SmartBool();
    private SmartBool timer = new SmartBool();
    private SmartInt attemptsNumber = new SmartInt();
    private SmartBool echo = new SmartBool();
    private SmartBool maxFPS = new SmartBool();
    private SmartType<String> agentName = new SmartType<String>();

    HashMap<String, ISmart> ArgsHashMap = new HashMap<String, ISmart>();

    public CmdLineOptions(String[] args)
    {
        // CmdLineOptions
        // -gui on/off
        // -agent wox name, like evolvable in simplerace
        // -ll digit  range [5:15], increase if succeeds.
        // -ld digit
        // -lt digit
        // -ls digit
        // -tc on/off tools
        // -gv on/off game viewer
        // -et on/off
        // -vb nothing/all/keys
        // -na digit number of attempts
        // -vis on/off

        ArgsHashMap.put("-ag", agentName.setValue(GlobalOptions.defaults.getAgentName()));
        ArgsHashMap.put("-agentName", agentName);
        ArgsHashMap.put("-visual", visualization.setValue(GlobalOptions.VisualizationOn));
        ArgsHashMap.put("-vis", visualization);
        ArgsHashMap.put("-gui", gui.setValue(GlobalOptions.defaults.isGui()));
        ArgsHashMap.put("-levelDifficulty", levelDifficulty.setValue(GlobalOptions.defaults.getLevelDifficulty()));
        ArgsHashMap.put("-ld", levelDifficulty);
        ArgsHashMap.put("-levelLength", levelLength.setValue(GlobalOptions.defaults.getLevelLength()));
        ArgsHashMap.put("-ll", levelLength);
        ArgsHashMap.put("-levelType", levelType.setValue(GlobalOptions.defaults.getLevelType()));
        ArgsHashMap.put("-lt", levelType);
        ArgsHashMap.put("-levelRandSeed", levelRandSeed.setValue(GlobalOptions.defaults.getLevelRandSeed()));
        ArgsHashMap.put("-ls", levelRandSeed);
        ArgsHashMap.put("-toolsConfigurator", toolsConfigurator.setValue(GlobalOptions.defaults.isToolsConfigurator()) );
        ArgsHashMap.put("-tc", toolsConfigurator);
        ArgsHashMap.put("-gameViewer", gameViewer.setValue(GlobalOptions.defaults.isGameViewer()));
        ArgsHashMap.put("-gv", gameViewer);
        ArgsHashMap.put("-gameViewerContinuousUpdates", gameViewerContinuousUpdates.setValue(GlobalOptions.defaults.isGameViewerContinuousUpdates()));
        ArgsHashMap.put("-gvc", gameViewerContinuousUpdates);
        ArgsHashMap.put("-timer", timer.setValue(GlobalOptions.defaults.isTimer()));
        ArgsHashMap.put("-t", timer);
//        ArgsHashMap.put("-verbose", GlobalOptions.defaults.getLevelRandSeed());
        ArgsHashMap.put("-attemptsNumber", attemptsNumber.setValue(GlobalOptions.defaults.getAttemptsNumber()));
        ArgsHashMap.put("-an", attemptsNumber);
        ArgsHashMap.put("-echo", echo.setValue(GlobalOptions.defaults.isEcho()));
        ArgsHashMap.put("-e", echo);
        ArgsHashMap.put("-maxFPS", maxFPS.setValue(GlobalOptions.defaults.isMaxFPS()));
        ArgsHashMap.put("-pw", pauseWorld.setValue(GlobalOptions.defaults.isPauseWorld()));
        ArgsHashMap.put("-pauseWorld", pauseWorld);
        ArgsHashMap.put("-powerRestoration", powerRestoration.setValue(GlobalOptions.defaults.isPowerRestoration()));
        ArgsHashMap.put("-pr", powerRestoration);
        ArgsHashMap.put("-stopSimulationIfWin", stopSimulationIfWin.setValue(GlobalOptions.defaults.isStopSimulationIfWin()));
        ArgsHashMap.put("-ssiw", stopSimulationIfWin);

        
        this.ParseArgs(args);

        if (isEcho())
        {
            System.out.println("Options set to:");
            for (Map.Entry<String,ISmart> el : ArgsHashMap.entrySet())
                System.out.println(el.getKey() + ": " + el.getValue().getValue());
        }
        GlobalOptions.VisualizationOn = isVisualization();
        GlobalOptions.GameVeiwerContinuousUpdatesOn = isGameViewerContinuousUpdates();
        GlobalOptions.FPS = (isMaxFPS()) ? GlobalOptions.InfiniteFPS : 24;
        GlobalOptions.pauseWorld = isPauseWorld();
        GlobalOptions.PowerRestoration = isPowerRestoration();
        GlobalOptions.StopSimulationIfWin = isStopSimulationIfWin();
    }

    public void ParseArgs(String[] args) {
        int i;
        for (i = 0; i < args.length - 1; i += 2)
            try  {((ISmart) ArgsHashMap.get(args[i])).setValueFromStr(args[i + 1]); }
            catch (NullPointerException e)
            {
                System.err.println("Error: Undefined command line parameter '" + args[i] + " " + args[i+1] + "'");
                System.err.println("Some defaults might be used instead");
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                // Basically we can push the red button to explaud the computer, since this case cannot' never happen.
                System.err.println("Error: Wrong number of input parameters");
//                System.err.println("It is good day to kill yourself with the yellow wall");
            }
    }

    public static void main(String[] args) {
        CmdLineOptions cl = new CmdLineOptions(new String[]{"-ll", "42", "sadf", "AA", "-gv", "on", "sd", "lsd", "-echo", "-ll"});
        System.out.println(cl.getLevelLength());
        System.out.println(cl.isGameViewer());
    }

    public Boolean isToolsConfigurator() {
        return toolsConfigurator.getValue();
    }

    public Boolean isGameViewer() {
        return gameViewer.getValue();
    }

    public Boolean isGameViewerContinuousUpdates() {
        return gameViewerContinuousUpdates.getValue();
    }

    public void setGameViewerContinuousUpdates(SmartBool gameViewerContinuousUpdates) {
        this.gameViewerContinuousUpdates = gameViewerContinuousUpdates;
    }

    public Boolean isTimer() {
        return timer.getValue();
    }

    public Integer getAttemptsNumber() {
        return attemptsNumber.getValue();
    }

    public Boolean isEcho() {
        return echo.getValue();
    }

    public Boolean isMaxFPS() {
        return maxFPS.getValue();
    }

    public String getAgentName() {
        return agentName.getValue();
    }

}
