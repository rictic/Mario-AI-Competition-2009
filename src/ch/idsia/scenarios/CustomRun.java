package ch.idsia.scenarios;

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

        // SeRVer
//        if (options.isServerMode() )
//        {
//            String message = "-ld 5"; // receive from server;
//            CmdLineOptions opts = new CmdLineOptions(message.split(" "));
//            options.setAgent(new ForwardJumpingAgent());
//            Evaluator evaluator = new Evaluator(options);
//            evaluator.evaluate();
//
//            while (server.isRunning())
//            {
//                options = parse(server.receive());
//                evaluator.init(opts);
//                evaluator.evaluate();
//
//
//            }
//        }

        //
//        options.setAgent(new ForwardJumpingAgent());
        Evaluator evaluator = new Evaluator(options);
        evaluator.evaluate();                
    }
}
