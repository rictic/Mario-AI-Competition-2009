package ch.idsia.utils;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, firstName_at_idsia_dot_ch
 * Date: May 5, 2009
 * Time: 9:34:33 PM
 * Package: ch.idsia.utils
 */
public class ParameterContainer
{
    protected HashMap<String, String> optionsHashMap = new HashMap<String, String>();
    private static List<String> allowedOptions = null;
    protected static HashMap<String, String> defaultOtionsHashMap = null;
    private String[] allowed = null;

    public ParameterContainer()
    {
        if (allowed == null)
            allowed = new String[]{
                    "-ag",
//            "-agentName",
                    "-an",
//            "-attemptsNumber",
//            "-e",
                    "-echo",
                    "-ewf",
//            "-exitWhenFinished",
//            "-gameViewer",
//            "-gameViewerContinuousUpdates",
//            "-gui",
                    "-gv",
                    "-gvc",
                    "-ld",
//            "-levelDifficulty",
//            "-levelLength",
//            "-levelRandSeed",
//            "-levelType",
                    "-ll",
                    "-ls",
                    "-lt",
                    "-m",
                    "-mm",
                    "-maxFPS",
//            "-matLabFile",
//            "-pauseWorld",
                    "-port",
//            "-powerRestoration",
                    "-pr",
                    "-pw",
                    "-server",
                    "-ssiw",
//            "-stopSimulationIfWin",
                    "-t",
                    "-tc",
                    "-tl",
//            "-toolsConfigurator",
                    "-vaot",
//            "-viewAlwaysOnTop",
//            "-viewLocationX",
//            "-viewLocationY",
                    "-vis",
//            "-visual",
                    "-vlx",
                    "-vly",
                    "-ze",
                    "-zm"
            };
        if (allowedOptions == null)
        {
            allowedOptions = new ArrayList<String>();
            Collections.addAll(allowedOptions, allowed);
        }

        InitDefaults();
    }

    public void addParameterValue(String param, String value)
    {
        if (allowedOptions.contains(param))
        {
            assert (optionsHashMap.get(param) == null);
            optionsHashMap.put(param, value);
        }
        else
            System.err.println("Parameter " + param + " is not valid. Typo?");
    }

    public void setParameterValue(String param, String value)
    {
        try
        {
            if (allowedOptions.contains(param))
            {
                optionsHashMap.put(param, value);
            }
            else
            {
                throw new IllegalArgumentException("Parameter " + param + " is not valid. Typo?");
            }
        }
        catch (IllegalArgumentException e)
        {

            System.err.println("Error: Undefined parameter '" + param + " " + value + "'");
            System.err.println(e.getMessage());
            System.err.println("Some defaults might be used instead");
        }
    }

    public String getParameterValue(String param)
    {
        if (allowedOptions.contains(param))
        {
            if (optionsHashMap.get(param) == null)
            {
                //System.err.println("InfoWarning: Default value '" + defaultOtionsHashMap.get(param) + "' for " + param +
                //        " used");
                optionsHashMap.put(param, defaultOtionsHashMap.get(param));
            }
            return optionsHashMap.get(param);
        }
        else
        {
            System.err.println("Parameter " + param + " is not valid. Typo?");
            return "";
        }
    }

    public int i(String s)
    {
        return Integer.parseInt(s);
    }

    public String s(Object i)
    {
        return String.valueOf(i);
    }

    public String s(Agent a)
    {   
        if (RegisterableAgent.getAgentByName(a.getName()) == null)
            RegisterableAgent.registerAgent(a);
        return a.getName();
    }

    public Agent a(String s)
    {
        return RegisterableAgent.getAgentByName(s);      
    }

    public boolean b(String s)
    {
        return "on".equals(s) || Boolean.valueOf(s);
    }

    public static void InitDefaults()
    {
        if (defaultOtionsHashMap != null)
            return;
        else
        {
            defaultOtionsHashMap = new HashMap<String, String>();
            new HumanKeyboardAgent();
            defaultOtionsHashMap.put("-ag","HumanKeyboardAgent"); //defaultOtionsHashMap.put("-agentName","NoAgent");
            defaultOtionsHashMap.put("-an","1"); //defaultOtionsHashMap.put("-attemptsNumber","5");
            defaultOtionsHashMap.put("-echo","off"); //defaultOtionsHashMap.put("-echo","off");
            defaultOtionsHashMap.put("-ewf","on"); //defaultOtionsHashMap.put("-exitWhenFinished","off");
            defaultOtionsHashMap.put("-gv","off"); //defaultOtionsHashMap.put("-gameViewer","off");
            defaultOtionsHashMap.put("-gvc","off"); //defaultOtionsHashMap.put("-gameViewerContinuousUpdates","off");
            defaultOtionsHashMap.put("-ld","0"); //defaultOtionsHashMap.put("-levelDifficulty","0");
            defaultOtionsHashMap.put("-ll","320"); //defaultOtionsHashMap.put("-levelLength","320");
            defaultOtionsHashMap.put("-ls","1"); //defaultOtionsHashMap.put("-levelRandSeed","1");
            defaultOtionsHashMap.put("-lt","0"); //defaultOtionsHashMap.put("-levelType","1");
            defaultOtionsHashMap.put("-maxFPS","off"); //defaultOtionsHashMap.put("-maxFPS","off");
            defaultOtionsHashMap.put("-m",""); //defaultOtionsHashMap.put("-matLabFile","DefaultMatlabFile");
            defaultOtionsHashMap.put("-mm","2");
            defaultOtionsHashMap.put("-pw","off"); //defaultOtionsHashMap.put("-pauseWorld","off");
            defaultOtionsHashMap.put("-port","4242"); //defaultOtionsHashMap.put("-port","4242");
            defaultOtionsHashMap.put("-pr","off"); //defaultOtionsHashMap.put("-powerRestoration","off");
            defaultOtionsHashMap.put("-ssiw","off"); //defaultOtionsHashMap.put("-stopSimulationIfWin","off");
            defaultOtionsHashMap.put("-server","off");
            defaultOtionsHashMap.put("-t","on"); //defaultOtionsHashMap.put("-timer","on");
            defaultOtionsHashMap.put("-tl","200"); //defaultOtionsHashMap.put("-timer","on");
            defaultOtionsHashMap.put("-tc","off"); //defaultOtionsHashMap.put("-toolsConfigurator","off");
            defaultOtionsHashMap.put("-vaot","off"); //defaultOtionsHashMap.put("-viewAlwaysOnTop","off");
            defaultOtionsHashMap.put("-vlx","0"); //defaultOtionsHashMap.put("-viewLocationX","0");
            defaultOtionsHashMap.put("-vly","0"); //defaultOtionsHashMap.put("-viewLocationY","0");
            defaultOtionsHashMap.put("-vis","on"); //defaultOtionsHashMap.put("-visual","on");
            defaultOtionsHashMap.put("-zm","1");
            defaultOtionsHashMap.put("-ze","0");
        }
    }

    public static String getDefaultParameterValue(String param)
    {
        if (allowedOptions.contains(param))
        {
            assert (defaultOtionsHashMap.get(param) != null);
            return defaultOtionsHashMap.get(param);
        }
        else
        {
            System.err.println("Reques for Default Parameter " + param + " Failed. Typo?");
            return "";
        }
    }
}