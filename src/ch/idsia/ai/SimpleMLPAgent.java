package ch.idsia.java.ai;

import ch.idsia.java.ai.agents.RegisterableAgent;
import ch.idsia.java.ai.environments.IEnvironment;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: Apr 28, 2009
 * Time: 2:09:42 PM
 */
public class SimpleMLPAgent extends RegisterableAgent implements Evolvable {

    //private String name = "SimpleMLPAgent";
    final MLP mlp;
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
        return (Evolvable) new SimpleMLPAgent(mlp.getNewInstance());
    }

    public Evolvable copy() {
        return (Evolvable) new SimpleMLPAgent (mlp.copy ()); 
    }

    public void reset() {
        mlp.reset ();
        //return this;
    }

    public void mutate() {
        mlp.mutate ();
    }

    public boolean[] GetAction(IEnvironment observation) {
        double[] inputs = new double[numberOfInputs];
        byte[][] scene = observation.getLevelSceneObservation();
        byte[][] enemies = observation.getEnemiesObservation();
        double[] outputs = mlp.propagate (inputs);
        boolean[] action = new boolean[numberOfOutputs];
        for (int i = 0; i < action.length; i++) {
            action[i] = outputs[i] > 0;    
        }
        return action;
    }

    public AGENT_TYPE getType() {
        return AGENT_TYPE.HUMAN;
    }

  /*  public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;    
    }             */
}
