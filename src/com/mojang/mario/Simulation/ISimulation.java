package com.mojang.mario.Simulation;

import com.mojang.mario.Agents.IAgent;
import com.mojang.mario.MarioComponent;
import com.mojang.mario.Tools.EvaluationInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 7, 2009
 * Time: 2:13:59 PM
 * Package: com.mojang.mario.Simulation
 */
public interface ISimulation
{
    public void setSimulationOptions(SimulationOptions simulationOptions);

//    public void setAgent(IAgent agent);
//
//    public void setLevelType(int levelType);
//
//    public void setLevelDifficulty(int levelDifficulty);
//
//    public void setLevelLength(int levelLength);
//
//    public IAgent getAgent ();
//
//    public EvaluationInfo simulateOneLevel();
//
//    public MarioComponent getMarioComponent();

    public EvaluationInfo simulateOneLevel();
}
