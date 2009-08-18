package ch.idsia.mario.environments;

import ch.idsia.mario.engine.LevelScene;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Mar 28, 2009
 * Time: 8:51:57 PM
 * Package: .Environments
 */

public interface Environment
{
    public static final int numberOfButtons = 5;
    public static final int numberOfObservationElements = 486 + 1;
    public static final int HalfObsWidth = 11;
    public static final int HalfObsHeight = 11;

    // always the same dimensionality: 22x22
    // always centered on the agent


    // upcoming feature for Milano conf, unkomment this, if you would like to try it!
    // Chaning ZLevel during the game on-the-fly;
    // if your agent recieves too ambiguous observation, it might request for more precise for the next step
    public byte[][] getCompleteObservation(/*int ZLevelMap, int ZLevelEnemies*/);

    public byte[][] getEnemiesObservation(/*int ZLevelEnemies*/);

    public byte[][] getLevelSceneObservation(/*int ZLevelMap*/);

    public float[] getMarioFloatPos();

    public int getMarioMode();

    public float[] getEnemiesFloatPos();
    
    public String getBitmapEnemiesObservation();

    public String getBitmapLevelObservation();

    public boolean isMarioOnGround();
    public boolean mayMarioJump();
    public boolean isMarioCarrying();
}
