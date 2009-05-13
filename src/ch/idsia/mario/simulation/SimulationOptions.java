package ch.idsia.mario.simulation;

import ch.idsia.ai.agents.Agent;
import ch.idsia.utils.ParameterContainer;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 12, 2009
 * Time: 9:55:56 PM
 * Package: com.mojang.mario.Simulation
 */


public class SimulationOptions extends ParameterContainer
{
//    protected Agent agent;
//    protected MarioComponent marioComponent = null;
    
//    protected SmartInt levelType = new SmartInt();         //int
//    protected SmartInt levelDifficulty = new SmartInt();   //int
//    protected SmartInt levelLength = new SmartInt();       //int
//    protected SmartInt levelRandSeed = new SmartInt();     //int
//    protected SmartBool visualization = new SmartBool();     //boolean
//    protected SmartBool pauseWorld = new SmartBool();      // boolean
//    protected SmartBool powerRestoration = new SmartBool(); //boolean
//    protected SmartBool stopSimulationIfWin = new SmartBool(); //boolean

    //TODO: SK handle this common with all the rest options
//    public int maxAttempts;
    public static int currentAttempt = -42;


    protected SimulationOptions()
    {
        super();
        currentAttempt = 1;
    }

    public SimulationOptions getSimulationOptionsCopy()
            // is this just a copy method then? /julian
    {
        SimulationOptions ret = new SimulationOptions();
        ret.setAgent(getAgent());
        ret.setLevelDifficulty(getLevelDifficulty());
        ret.setLevelLength(getLevelLength());
        ret.setLevelRandSeed(getLevelRandSeed());
        ret.setLevelType(getLevelType());
//        ret.setMarioComponent(marioComponent);
        ret.setVisualization(isVisualization());
        ret.setPauseWorld(isPauseWorld());
        ret.setPowerRestoration(isPowerRestoration());
        ret.setMaxAttempts(getMaxAttempts());
        return ret;
    }

    public Agent getAgent() {
        return a(getParameterValue("-ag"));      }

    public void setAgent(Agent agent) {
        setParameterValue("-ag", s(agent));
    }

    // TODO? LEVEL_TYPE enum?
    public int getLevelType() {
        return i(getParameterValue("-lt"));      }

    public void setLevelType(int levelType) {
        setParameterValue("-lt", s(levelType));    }

    public int getLevelDifficulty() {
        return i(getParameterValue("-ld"));                           }

    public void setLevelDifficulty(int levelDifficulty) {
        setParameterValue("-ld", s(levelDifficulty));    }

    public int getLevelLength() {
        return i(getParameterValue("-ll"));      }

    public void setLevelLength(int levelLength) {
        setParameterValue("-ll", s(levelLength));    }

    public int getLevelRandSeed() {
        return i(getParameterValue("-ls"));     }

    public void setLevelRandSeed(int levelRandSeed) {
        setParameterValue("-ls", s(levelRandSeed));    }

    public boolean isVisualization() {
        return b(getParameterValue("-vis"));     }

    public void setVisualization(boolean visualization) {
        setParameterValue("-vis", s(visualization));    }

    public void setPauseWorld(boolean pauseWorld) {
        setParameterValue("-pw", s(pauseWorld));    }

    public Boolean isPauseWorld() {
        return b(getParameterValue("-pw"));     }

    public Boolean isPowerRestoration() {
        return b(getParameterValue("-pr"));     }

    public void setPowerRestoration(boolean powerRestoration) {
        setParameterValue("-pr", s(powerRestoration));    }

    public Boolean isStopSimulationIfWin() {
        return b(getParameterValue("-ssiw"));     }

    public void setStopSimulationIfWin(boolean stopSimulationIfWin) {
        setParameterValue("-ssiw", s(stopSimulationIfWin));    }

    public int getMaxAttempts() {
        return i(getParameterValue("-an"));     }

    public void setMaxAttempts(int maxAttempts) {
        setParameterValue("-an", s(maxAttempts));    }


}
