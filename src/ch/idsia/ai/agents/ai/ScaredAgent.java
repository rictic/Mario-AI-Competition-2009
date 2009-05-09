package ch.idsia.ai.agents.ai;

import ch.idsia.ai.agents.IAgent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.IEnvironment;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, firstname_at_idsia_dot_ch
 * Date: May 9, 2009
 * Time: 9:46:59 AM
 * Package: ch.idsia.ai.agents
 */
public class ScaredAgent extends RegisterableAgent implements IAgent {
    public ScaredAgent() {
        super("ScaredAgent");
    }

    int trueJumpCounter = 0;
    int trueSpeedCounter = 0;

    private boolean DangerOfGap(byte[][] levelScene)
    {
        for (int x = 9; x < 13; ++x)
        {
            boolean f = true;
            for(int y = 12; y < 22; ++y)
            {
                if  (levelScene[y][x] != 0)
                    f = false;
            }
            if (f && levelScene[12][11] != 0)
                return true;
        }
        return false;
    }

    public void reset() {
        Action[Mario.KEY_RIGHT] = true;
        Action[Mario.KEY_SPEED] = false;
    }

    public boolean[] getAction(IEnvironment observation) {
        byte[][] levelScene = observation.getLevelSceneObservation();
        if (/*levelScene[11][13] != 0 ||*/ levelScene[11][12] != 0 ||
           /* levelScene[12][13] == 0 ||*/ levelScene[12][12] == 0 )
        {
            if (observation.mayMarioJump() || ( !observation.isMarioOnGround() && Action[Mario.KEY_JUMP]))
            {
                Action[Mario.KEY_JUMP] = true;
            }
            ++trueJumpCounter;
        }
        else
        {
            Action[Mario.KEY_JUMP] = false;
            trueJumpCounter = 0;
        }

        if (trueJumpCounter > 46)
        {
            trueJumpCounter = 0;
            Action[Mario.KEY_JUMP] = false;
        }

        return Action;
    }
}
