package ch.idsia.scenarios;

import ch.idsia.ai.agents.ai.ForwardJumpingAgent;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.Evaluator;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, firstName_at_idsia_dot_ch
 * Date: May 7, 2009
 * Time: 4:38:23 PM
 * Package: ch.idsia
 */

public class CustomRun
{
    public static void main(String[] args) {
        CmdLineOptions options = new CmdLineOptions(args);
        options.setAgent(new ForwardJumpingAgent());
        Evaluator evaluator = new Evaluator(options);
        evaluator.evaluate();                
    }
}
