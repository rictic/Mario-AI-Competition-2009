package ch.idsia.ai.agents.ai;

import ch.idsia.ai.environments.IEnvironment;
import ch.idsia.engine.sprites.Mario;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.ai.agents.IAgent;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 8, 2009
 * Time: 4:03:46 AM
 * Package: com.mojang.mario.Agents
 */
public class ForwardAgent extends RegisterableAgent implements IAgent
{
    Point prevPosition = new Point();
    int trueJumpCounter = 0;
    int trueSpeedCounter = 0;

    public ForwardAgent()
    {
        super("ForwardAgent");
        reset();
    }

    public void reset()
    {
        Action = new boolean[IEnvironment.NumberOfActionSlots];
        Action[Mario.KEY_RIGHT] = true;
        Action[Mario.KEY_SPEED] = true;
        prevPosition = new Point();
        trueJumpCounter = 0;
        trueSpeedCounter = 0;
    }

    private boolean DangerOfGap(byte[][] levelScene)
    {
        for (int x = 9; x < 13; ++x)
        {
            boolean f = true;
            for(int y = 12; y < 22; ++y)
            {
                if  (levelScene[x][y] != 0)
                    f = false;
            }
            if (f && levelScene[11][12] != 0)
                return true;
        }
        return false;
    }

    public boolean[] GetAction(IEnvironment observation)
    {
        //TODO: Discuss increasing diffuculty for handling the gaps.
        // this Agent requires observation.

        assert(observation != null);
        byte[][] levelScene = observation.getLevelSceneObservation();
//        System.out.println("");
//        for(int y = 0; y < 22; ++y)
//        {
//            for (int x = 0; x < 22; ++x)
//            {
//                System.out.print(levelScene[x][y] + " ");
//            }
//            System.out.println("");
//        }

//        System.out.println("");
//        boolean flag = (!prevPosition.equals(observation.getMarioPosition()));
//        prevPosition = observation.getMarioPosition();
//        int y = observation.getMarioPosition().y;
//        int x = observation.getMarioPosition().x;
//        if (x < 0 || x > observation.getLevelSceneObservation().length
//         || y < 0 || y > observation.getLevelSceneObservation()[0].length-1)
//            return EmptyAction;
//        System.out.println("obs x+1 [" + x + ", " + y + "] = " + observation.getLevelSceneObservation()[x+1][y]);
//        System.out.println("prev " + prevPosition);
//        System.out.println("flag: " + flag);
//        if (observation.getLevelSceneObservation()[x+1][y] != 0 ||
//               observation.getLevelSceneObservation()[x+1][observation.getLevelSceneObservation()[0].length-1] == 0 /*||
//               observation.getLevelSceneObservation()[x][observation.getLevelSceneObservation()[0].length-1] == 0 */)
        if (levelScene[13][11] != 0 || levelScene[12][11] != 0 ||  DangerOfGap(levelScene))
        {
            if (observation.mayMarioJump() || ( !observation.isMarioOnGround() && Action[Mario.KEY_JUMP]))
            {
                Action[Mario.KEY_JUMP] = true;
            }
            ++trueJumpCounter;
//            System.out.println("trueJumpCounter:" + trueJumpCounter);
        }
        else
        {
            Action[Mario.KEY_JUMP] = false;
            trueJumpCounter = 0;
        }

        if (trueJumpCounter > 16)
        {
            trueJumpCounter = 0;
            Action[Mario.KEY_JUMP] = false;
        }


//        if (++trueSpeedCounter > 10)
//        {
//            Action[Mario.KEY_SPEED] = false;
//            trueSpeedCounter = 0;
//        }
//        else
//            Action[Mario.KEY_SPEED] = false;

        Action[Mario.KEY_SPEED] = DangerOfGap(levelScene);
        return Action;
    }
}
