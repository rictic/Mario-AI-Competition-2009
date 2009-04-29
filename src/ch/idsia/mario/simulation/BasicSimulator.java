package ch.idsia.mario.simulation;

import ch.idsia.java.mario.engine.MarioComponent;
import ch.idsia.java.mario.engine.GlobalOptions;
import ch.idsia.ai.agents.IAgent;
import ch.idsia.tools.EvaluationInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 7, 2009
 * Time: 2:27:48 PM
 * Package: com.mojang.mario.Simulation
 */

public class BasicSimulator implements ISimulation
{
    SimulationOptions simulationOptions = null;
    private MarioComponent marioComponent;

    public BasicSimulator(SimulationOptions simulationOptions)
    {
        GlobalOptions.VisualizationOn = simulationOptions.isVisualization();
        this.marioComponent = simulationOptions.getMarioComponent();
        this.setSimulationOptions(simulationOptions);
    }

    private MarioComponent prepareMarioComponent()
    {
        IAgent agent = simulationOptions.getAgent();
        marioComponent.setAgent(agent);
        return marioComponent;
    }

    public void setSimulationOptions(SimulationOptions simulationOptions)
    {
        this.simulationOptions = simulationOptions;
    }

    public EvaluationInfo simulateOneLevel()
    {
        prepareMarioComponent();
        marioComponent.startLevel(simulationOptions.getLevelRandSeed(), simulationOptions.getLevelDifficulty()
                                 , simulationOptions.getLevelType(), simulationOptions.getLevelLength());
        marioComponent.setPaused(simulationOptions.isPauseWorld());
        return marioComponent.run1();
    }
}
