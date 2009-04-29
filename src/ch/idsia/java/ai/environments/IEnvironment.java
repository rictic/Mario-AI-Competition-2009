package ch.idsia.java.ai.environments;

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
//    public static final int NumberOfKeysToPress = 20;  // TODO: Manage all key presses in one place: Agent or Scene or MarioComponent..

    // always the same dimensionality (e.g. 32 by 15)
    // always centered on the agent
    public EnvCell[][] getCompleteObservation();

    public byte[][] getEnemiesObservation(); // usually < 5 and therefore matrix would be Very sparce

    public byte[][] getLevelSceneObservation();

    public boolean isMarioOnGround();
    public boolean mayMarioJump();
//    public Point getMarioPosition();
}
