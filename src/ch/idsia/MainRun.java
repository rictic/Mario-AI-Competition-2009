package ch.idsia;

import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.GameViewer;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.tools.Network.ServerAgent;
import ch.idsia.ai.agents.ai.ForwardAgent;
import ch.idsia.ai.agents.ai.RandomAgent;
import ch.idsia.ai.agents.ai.ForwardJumpingAgent;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.ai.SimpleMLPAgent;
import ch.idsia.mario.engine.GlobalOptions;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, firstName_at_idsia_dot_ch
 * Date: May 7, 2009
 * Time: 4:35:08 PM
 * Package: ch.idsia
 */

public class MainRun 
{
    public static void main(String[] args) {
        CmdLineOptions cmdLineOptions = new CmdLineOptions(args);
        EvaluationOptions evaluationOptions = cmdLineOptions;

        // Create an Agent here
        new ForwardAgent();
        new HumanKeyboardAgent();
        new RandomAgent();
        new ForwardJumpingAgent();
        new SimpleMLPAgent();
        new ServerAgent(cmdLineOptions.getServerAgentPort(), cmdLineOptions.isServerAgentEnabled());

    }
}
