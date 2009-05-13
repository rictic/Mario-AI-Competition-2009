package ch.idsia.ai.agents.ai;

import ch.idsia.ai.Evolvable;
import ch.idsia.ai.MLP;
import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.mario.environments.Environment;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: Apr 28, 2009
 * Time: 2:09:42 PM
 */
public class  SimpleMLPAgent extends RegisterableAgent implements Agent, Evolvable {

    //private String name = "SimpleMLPAgent";
    private MLP mlp;
    final int numberOfOutputs = 6;
    final int numberOfInputs = 10;

    public SimpleMLPAgent () {
        super ("SimpleMLPAgent");
        mlp = new MLP (numberOfInputs, 6, numberOfOutputs);
    }

    public SimpleMLPAgent (MLP mlp) {
        super ("SimpleMLPAgent");
        this.mlp = mlp;
    }

    public Evolvable getNewInstance() {
        return new SimpleMLPAgent(mlp.getNewInstance());
    }

    public Evolvable copy() {
        return new SimpleMLPAgent (mlp.copy ());
    }

    public void reset() {
        mlp.reset ();
    }

    public void mutate() {
        mlp.mutate ();
    }

    public boolean[] getAction(Environment observation) {
        double[] inputs;// = new double[numberOfInputs];
        byte[][] scene = observation.getLevelSceneObservation();
        //int[][] enemies = observation.getEnemiesObservation();
        inputs = new double[]{probe(-1, -1, scene), probe(0, -1, scene), probe(1, -1, scene),
                              probe(-1, 0, scene), probe(0, 0, scene), probe(1, 0, scene),
                                probe(-1, 1, scene), probe(0, 1, scene), probe(1, 1, scene),
                                1};
        double[] outputs = mlp.propagate (inputs);
        boolean[] action = new boolean[numberOfOutputs];
        for (int i = 0; i < action.length; i++) {
            action[i] = outputs[i] > 0;
        }
        return action;
    }

    private double probe (int x, int y, byte[][] scene) {
        int realX = x + 11;
        int realY = y + 11;
        return (scene[realX][realY] != 0) ? 1 : 0;
    }
}
