package ch.idsia.java.ai.agents.human;

import ch.idsia.java.mario.engine.environments.IEnvironment;
import ch.idsia.java.mario.engine.sprites.Mario;
import ch.idsia.java.mario.engine.GlobalOptions;
import ch.idsia.java.ai.agents.IAgent;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 8, 2009
 * Time: 3:36:16 AM
 * Package: com.mojang.mario.Agents
 */
public class CheaterKeyboardAgent extends KeyAdapter implements IAgent {
    private boolean Action[] = null;

    private String Name = "Instance of CheaterKeyboardAgent";

    public CheaterKeyboardAgent()
    {
        reset();
    }

    public void reset()
    {
        // Just check you keyboard.
        Action = new boolean[IEnvironment.NumberOfActionSlots];
    }

    public boolean[] GetAction(IEnvironment observation)
    {
        return Action;
    }

    public AGENT_TYPE getType() {        return AGENT_TYPE.HUMAN;    }

    public String getName() {   return Name; }

    public void setName(String name) {        Name = name;    }
    

    public void keyPressed (KeyEvent e)
    {
        toggleKey(e.getKeyCode(), true);
    }

    public void keyReleased (KeyEvent e)
    {
        toggleKey(e.getKeyCode(), false);
    }

    private void toggleKey(int keyCode, boolean isPressed)
    {
        switch (keyCode) {
            //Cheats;
            case KeyEvent.VK_D:
                GlobalOptions.gameViewerTick();
                break;
            case KeyEvent.VK_U:
                Action[Mario.KEY_LIFE_UP] = isPressed;
                break;
            case KeyEvent.VK_W:
                Action[Mario.KEY_WIN] = isPressed;
                break;
            case KeyEvent.VK_P:
                if (isPressed)
                {
                    System.out.println("Paused On/Off");
                    GlobalOptions.pauseWorld = !GlobalOptions.pauseWorld;
                    if (GlobalOptions.pauseWorld)
                        Action[Mario.KEY_PAUSE] = true;
                    else
                        Action[Mario.KEY_PAUSE] = false;
                }
                break;
            case KeyEvent.VK_L:
                if (isPressed)
                {
                    System.out.println("Labels On/Off");
                    GlobalOptions.Labels = !GlobalOptions.Labels;
                }
                break;
            case KeyEvent.VK_C:
                if (isPressed)
                {
                    System.out.println("Center On/Off");
                    GlobalOptions.MarioAlwaysInCenter = !GlobalOptions.MarioAlwaysInCenter;
                }
                break;
            case 61:
                if (isPressed)
                {
                    System.out.println("FPS increase by 1. Current FPS is " + ++GlobalOptions.FPS);
                    GlobalOptions.AdjustMarioComponentFPS();
                }
                break;
            case 45:
                if (isPressed)
                {
                    System.out.println("FPS decrease . Current FPS is " + --GlobalOptions.FPS);
                    GlobalOptions.AdjustMarioComponentFPS();
                }
                break;
        }
    }

}
