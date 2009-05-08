package ch.idsia.ai.tasks;

import ch.idsia.ai.agents.IAgent;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.mario.engine.MarioComponent;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 8, 2009
 * Time: 11:26:43 AM
 * Package: com.mojang.mario.Tasks
 */
public class ProgressTask implements Task {

    public double[] evaluate() {
        MarioComponent marioComponent = new MarioComponent(320, 240);
        marioComponent.init();
//        options.setMarioComponent(marioComponent);
//        Evaluator evaluator = new Evaluator (options);
//        List<EvaluationInfo> results = evaluator.evaluate ();
//        double distanceTravelled = 0;
//        for (EvaluationInfo result : results) {
//            distanceTravelled += result.computeDistancePassed();
//        }
//        distanceTravelled = distanceTravelled / results.size();
//        return new double[]{distanceTravelled};
        return evaluate();
    }

    public double[] evaluate(IAgent controller) {
        return new double[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setOptions(EvaluationOptions options) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public EvaluationOptions getOptions() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setDifficuly(int difficulty) {
//        options.setLevelDifficulty(difficulty);
    }

    public void setSeed(int seed) {
//        options.setLevelRandSeed(seed);
    }
}
