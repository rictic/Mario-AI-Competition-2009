package ch.idsia.scenarios;

import ch.idsia.ai.agents.ai.*;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.tools.Evaluator;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, firstName_at_idsia_dot_ch
 * Date: May 7, 2009
 * Time: 4:35:08 PM
 * Package: ch.idsia
 */

/*
For "quick and dirty" plays you can adjust the default parameters in ParameterContainer class, but we strongly encourage
you to use the API proposed. Because if in the first case you are mostlikely the only person who had become resposible for
the stability of the entire system, in the second case you can rely on our direct support as soon as possible. And(!)
If you encounter any trouble with using API proposed, please, e-mail us {sergey, julian} @ idsia . ch immediately. Because if
anybody encounters any trouble that implies some other person to encounter the same trouble and we cannot effort that.
Thank you for your kind assistance and productive collaboration!
Sergey Karakovskiy and Julian Togelius.
 */

public class MainRun 
{
    public static void main(String[] args) {
        CmdLineOptions cmdLineOptions = new CmdLineOptions(args);
        EvaluationOptions evaluationOptions = cmdLineOptions;  // if none options mentioned, all defalults are used.
        createNativeAgents(cmdLineOptions);
        Evaluator evaluator = new Evaluator(evaluationOptions);
        List<EvaluationInfo> evaluationSummary = evaluator.evaluate();
//        LOGGER.save("log.txt");

        if (cmdLineOptions.isExitProgramWhenFinished())
            System.exit(0);
    }

    private static boolean calledBefore = false;
    public static void createNativeAgents(CmdLineOptions cmdLineOptions)
    {
        if (!calledBefore)
        {
            // Create an Agent here or mention the set of agents you want to be available for the framework.
            // All created agents by now are used here.
            // They can be accessed by just setting the commandline property -ag to the name of desired agent.
            calledBefore = true;
            new ForwardAgent();
            new HumanKeyboardAgent();
            new RandomAgent();
            new ForwardJumpingAgent();
            new SimpleMLPAgent();
//            new ServerAgent(cmdLineOptions.getServerAgentPort(), cmdLineOptions.isServerAgentEnabled());
            new ScaredAgent();
            new ScaredSpeedyAgent();
        }
    }

}
