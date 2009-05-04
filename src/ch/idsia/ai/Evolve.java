package ch.idsia.ai;

import ch.idsia.ai.ea.ES;
import ch.idsia.ai.tasks.Task;
import ch.idsia.ai.tasks.ProgressTask;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 4, 2009
 * Time: 4:33:25 PM
 */
public class Evolve {

    public static void main(String[] args) {
        Evolvable initial = new SimpleMLPAgent ();
        Task task = new ProgressTask();
        ES es = new ES (task, initial, 100);
    }

}
