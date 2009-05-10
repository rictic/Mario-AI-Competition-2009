package ch.idsia.mario.environments;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Mar 28, 2009
 * Time: 8:51:57 PM
 * Package: com.mojang.mario.Environments
 */

public interface IEnvironment
{
    public static final int NumberOfActionSlots = 16;
    public static final int NumberOfActions = 5;
    public static final int NumberOfObservationElements = 486;

    // always the same dimensionality: 22x22
    // always centered on the agent
    public EnvCell[][] getCompleteObservation();

    public byte[][] getEnemiesObservation(); // TODO: think of this:
    // TODO: usually < 5 and therefore matrix would be Very sparce. Probably should change to list?

    public byte[][] getLevelSceneObservation();

    public boolean isMarioOnGround();
    public boolean mayMarioJump();
//    public Point getMarioPosition();
}
