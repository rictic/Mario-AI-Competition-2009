package com.mojang.mario;

import com.mojang.mario.agents.IAgent;
import com.mojang.mario.agents.Human.HumanKeyboardAgent;

import javax.swing.JApplet;


public class AppletLauncher extends JApplet
{
    private static final long serialVersionUID = -2238077255106243788L;

    private MarioComponent mario;
    private boolean started = false;

    public void init()
    {
        showStatus("Applet Loaded");
    }

    public String getAppletInfo()
    {
        return "MarioInfinite AI Framework by Sergey Karakovskiy";
    }


    public void start()
    {
        if (!started)
        {
            started = true;
            IAgent hka = new HumanKeyboardAgent();
            hka.reset();            
            mario = new MarioComponent(getWidth(), getHeight(), hka);
            setContentPane(mario);
            setFocusable(false);
            mario.setFocusCycleRoot(true);

            mario.start();
//            addKeyListener(mario);
//            addFocusListener(mario);
        }
    }

    public void stop()
    {
        if (started)
        {
            started = false;
//            removeKeyListener(mario);
            mario.stop();
//            removeFocusListener(mario);
        }
    }
}