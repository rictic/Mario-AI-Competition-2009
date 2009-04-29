package ch.idsia.mario.tasks;

import ch.idsia.mario.agents.IAgent;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 8, 2009
 * Time: 11:26:43 AM
 * Package: com.mojang.mario.Tasks
 */
public class ProgressTask implements Task {

//    Evaluator evaluator = new Evaluator ();

    public double[] evaluate(IAgent controller) {
//        evaluator.evaluate (controller);
//        return new double[]{evaluator.distanceTravelled ()};
        return new double [6];
    }

    public void setDifficuly(int difficulty) {
//        evaluator.setDifficulty ();
    }

    public void setSeed(int seed) {
        
    }
}
