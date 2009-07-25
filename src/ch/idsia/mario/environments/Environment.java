package ch.idsia.mario.environments;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Mar 28, 2009
 * Time: 8:51:57 PM
 * Package: .Environments
 */

public interface Environment
{
//    public static final int NumberOfActionSlots = 16;
    public static final int numberOfButtons = 5;
    public static final int numberOfObservationElements = 486 + 1;
    public static final int HalfObsWidth = 11;
    public static final int HalfObsHeight = 11;

    // always the same dimensionality: 22x22
    // always centered on the agent
    public byte[][] getCompleteObservation();

    public byte[][] getEnemiesObservation(); // TODO: think of this:
    // TODO: usually < 5 and therefore matrix would be Very sparce. Probably should change to list?

    public byte[][] getLevelSceneObservation();

    public float[] getMarioFloatPos();

    public float[] getEnemiesFloatPos();

    public String getBitmapEnemiesObservation();

    public String getBitmapLevelObservation();

    public boolean isMarioOnGround();
    public boolean mayMarioJump();
//    public Point getMarioPosition();
}
