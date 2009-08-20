package ch.idsia.scenarios;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.ai.agents.ai.TimingAgent;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.Evaluator;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.utils.StatisticalSummary;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: Aug 13, 2009
 * Time: 6:32:50 PM
 */
public class CompetitionScore {

    final static int numberOfTrials = 10;

    public static void main(String[] args) {
        Agent controller = RegisterableAgent.load ("com.reddit.programming.mario.BestFirstAgent");
        final int startingSeed = Integer.parseInt ("0");
        score (controller, startingSeed);
        System.exit (0);
    }

    public static void score (Agent agent, int startingSeed) {
        TimingAgent controller = new TimingAgent (agent);
        RegisterableAgent.registerAgent (controller);
        EvaluationOptions options = new CmdLineOptions(new String[0]);

        options.setMaxAttempts(1);
        options.setVisualization(false);
        options.setMaxFPS(true);
        System.out.println("Scoring controller " + controller + " with starting seed " + startingSeed);

        double competitionScore = 0;

        competitionScore += testConfig (controller, options, startingSeed, 0, false);
        competitionScore += testConfig (controller, options, startingSeed, 3, false);
        competitionScore += testConfig (controller, options, startingSeed, 5, false);
        competitionScore += testConfig (controller, options, startingSeed, 10, false);
        System.out.println("Competition score: " + competitionScore);
    }

    public static double testConfig (TimingAgent controller, EvaluationOptions options, int seed, int level, boolean paused) {
        options.setLevelDifficulty(level);
        options.setPauseWorld(paused);
        StatisticalSummary ss = test (controller, options, seed);
        double averageTimeTaken = controller.averageTimeTaken();
        System.out.printf("Difficulty %d score %.4f (avg time %.4f)\n",
                level, ss.mean(), averageTimeTaken);
        if (averageTimeTaken > 40) {
            System.out.println("Maximum allowed average time is 40 ms per time step.\n" +
                    "Controller disqualified");
            System.exit (0);
        }
        return ss.mean();
    }

    public static StatisticalSummary test (Agent controller, EvaluationOptions options, int seed) {
        StatisticalSummary ss = new StatisticalSummary ();
        for (int i = 0; i < numberOfTrials; i++) {
            options.setLevelRandSeed(seed + i);
            controller.reset();
            options.setAgent(controller);
            Evaluator evaluator = new Evaluator (options);
            EvaluationInfo result = evaluator.evaluate().get(0);
            ss.add (result.computeDistancePassed());
        }
        return ss;
    }

}
