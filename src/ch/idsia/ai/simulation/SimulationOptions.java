package ch.idsia.java.ai.simulation;

import ch.idsia.java.ai.agents.IAgent;
import ch.idsia.engine.MarioComponent;
import ch.idsia.utils.SmartBool;
import ch.idsia.utils.SmartInt;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 12, 2009
 * Time: 9:55:56 PM
 * Package: com.mojang.mario.Simulation
 */


public class SimulationOptions 
{
    protected IAgent agent;
    protected MarioComponent marioComponent = null;
    protected SmartInt levelType = new SmartInt();         //int
    protected SmartInt levelDifficulty = new SmartInt();   //int
    protected SmartInt levelLength = new SmartInt();       //int
    protected SmartInt levelRandSeed = new SmartInt();     //int
    protected SmartBool visualization = new SmartBool();     //boolean
    protected SmartBool pauseWorld = new SmartBool();      // boolean
    protected SmartBool powerRestoration = new SmartBool(); //boolean


    protected SmartBool stopSimulationIfWin = new SmartBool(); //boolean

    public SimulationOptions getBasicSimulatorOptions()
    {
        SimulationOptions ret = new SimulationOptions();
        ret.setAgent(agent);
        ret.setLevelDifficulty(levelDifficulty.getValue());
        ret.setLevelLength(levelLength.getValue());
        ret.setLevelRandSeed(levelRandSeed.getValue());
        ret.setLevelType(levelType.getValue());
        ret.setMarioComponent(marioComponent);
        ret.setVisualization(visualization.getValue());
        ret.setPauseWorld(pauseWorld.getValue());
        ret.setPowerRestoration(powerRestoration.getValue());
        return ret;
    }

    public IAgent getAgent() {
        return agent;
    }

    public void setAgent(IAgent agent) {
        this.agent = agent;
    }

    public MarioComponent getMarioComponent() {
        return marioComponent;
    }

    public void setMarioComponent(MarioComponent marioComponent) {
        this.marioComponent = marioComponent;
    }

    public int getLevelType() {
        return levelType.getValue();
    }

    public void setLevelType(int levelType) {
        this.levelType.setValue(levelType);
    }

    public int getLevelDifficulty() {
        return levelDifficulty.getValue();
    }

    public void setLevelDifficulty(int levelDifficulty) {
        this.levelDifficulty.setValue(levelDifficulty);
    }

    public int getLevelLength() {
        return levelLength.getValue();
    }

    public void setLevelLength(int levelLength) {
        this.levelLength.setValue(levelLength);
    }

    public int getLevelRandSeed() {
        return levelRandSeed.getValue();
    }

    public void setLevelRandSeed(int levelRandSeed) {
        this.levelRandSeed.setValue(levelRandSeed);
    }

    public boolean isVisualization() {
        return visualization.getValue();
    }

    public void setVisualization(boolean visualization) {
        this.visualization.setValue(visualization);
    }

    public void setPauseWorld(boolean pauseWorld) {
        this.pauseWorld.setValue(pauseWorld);
    }

    public Boolean isPauseWorld() {
        return pauseWorld.getValue();
    }


    public Boolean isPowerRestoration() {
        return powerRestoration.getValue();
    }

    public void setPowerRestoration(boolean powerRestoration) {
        this.powerRestoration.setValue(powerRestoration);
    }

    public Boolean isStopSimulationIfWin() {
        return stopSimulationIfWin.getValue();
    }

    public void setStopSimulationIfWin(boolean stopSimulationIfWin) {
        this.stopSimulationIfWin.setValue(stopSimulationIfWin);
    }

}
