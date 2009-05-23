package ch.idsia.ai.agents.ai;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.Evolvable;
import ch.idsia.ai.MLP;
import ch.idsia.mario.environments.Environment;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 13, 2009
 * Time: 11:11:33 AM
 */
public class LargerMLPAgent implements Agent, Evolvable {

    private String name = "LargerMLPAgent";
    private MLP mlp;
    final int numberOfOutputs = Environment.numberOfButtons;
    final int numberOfInputs = 26;

    public LargerMLPAgent () {
        mlp = new MLP (numberOfInputs, 6, numberOfOutputs);
    }

    private LargerMLPAgent (MLP mlp) {
        this.mlp = mlp;
    }

    public Evolvable getNewInstance() {
        return new LargerMLPAgent(mlp.getNewInstance());
    }

    public Evolvable copy() {
        return new LargerMLPAgent (mlp.copy ());
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
        inputs = new double[numberOfInputs];
        inputs[1] = 1;
        int which = 1;
        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                inputs[which++] = probe(i, j, scene);
            }
        }
        double[] outputs = mlp.propagate (inputs);
        boolean[] action = new boolean[numberOfOutputs];
        for (int i = 0; i < action.length; i++) {
            action[i] = outputs[i] > 0;
        }
        return action;
    }

    public AGENT_TYPE getType() {
        return AGENT_TYPE.AI;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private double probe (int x, int y, byte[][] scene) {
        int realX = x + 11;
        int realY = y + 11;
        return (scene[realX][realY] != 0) ? 1 : 0;
    }

}
