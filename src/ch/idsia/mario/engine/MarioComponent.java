package ch.idsia.mario.engine;

import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.ai.agents.IAgent;
import ch.idsia.ai.agents.human.CheaterKeyboardAgent;
import ch.idsia.tools.GameViewer;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.Network.ServerAgent;
import ch.idsia.mario.environments.IEnvironment;
import ch.idsia.mario.environments.EnvCell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.List;


public class MarioComponent extends JComponent implements Runnable, /*KeyListener,*/ FocusListener, IEnvironment {
    private static final long serialVersionUID = 790878775993203817L;
    public static final int TICKS_PER_SECOND = 24;

    private boolean running = false;
    private int width, height;
    private GraphicsConfiguration graphicsConfiguration;
    private Scene scene;
    private boolean focused = false;
    private boolean useScale2x = false;

    int frame;
    int delay;
    Thread animator;

    public void setGameViewer(GameViewer gameViewer) {
        this.gameViewer = gameViewer;
    }

    private GameViewer gameViewer = null;

    private IAgent agent = null;
    private CheaterKeyboardAgent cheatAgent = null;

    private KeyAdapter prevHumanKeyBoardAgent;
    private Mario mario = null;
    private LevelScene levelScene = null;

    public MarioComponent(int width, int height, IAgent agent) {
        adjustFPS();


        this.setFocusable(true);
        this.setEnabled(true);
        this.width = width;
        this.height = height;

        Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        setFocusable(true);

        this.cheatAgent = new CheaterKeyboardAgent();
        this.addKeyListener(cheatAgent);
        this.setAgent(agent);

        GlobalOptions.registerMarioComponent(this);

    }

    public void adjustFPS() {
        int fps = GlobalOptions.FPS;
        delay = (fps > 0) ? (fps >= GlobalOptions.InfiniteFPS) ? 0 : (1000 / fps) : 100;
//        System.out.println("Delay: " + delay);
    }

    public void paint(Graphics g) {
    }

    public void update(Graphics g) {
    }

    public void init() {
        graphicsConfiguration = getGraphicsConfiguration();
//        if (graphicsConfiguration != null) {
            Art.init(graphicsConfiguration);
//        }
    }

    public void start() {
        if (!running) {
            running = true;
            animator = new Thread(this, "Game Thread");
            animator.start();
        }
    }

    public void stop() {
        running = false;
    }

    public void run() {

    }

    public EvaluationInfo run1(int currentAttempt, int totalNumberOfAttempts) {
        running = true;
        adjustFPS();
        EvaluationInfo evaluationInfo = new EvaluationInfo();

        VolatileImage image = null;
        Graphics g = null;
        Graphics og = null;

        image = createVolatileImage(320, 240);
        g = getGraphics();
        og = image.getGraphics();

        if (!GlobalOptions.VisualizationOn) {
            String msgClick = "Vizualization is not available";
            drawString(og, msgClick, 160 - msgClick.length() * 4, 110, 1);
            drawString(og, msgClick, 160 - msgClick.length() * 4, 110, 7);
        }

        addFocusListener(this);

        // Remember the starting time
        long tm = System.currentTimeMillis();
        long tick = tm;
        int marioStatus = Mario.STATUS_RUNNING;

        mario = ((LevelScene) scene).mario;
        int totalActionsPerfomed = 0;
// TODO: Manage better place for this:
        Mario.resetCoins();

        while (/*Thread.currentThread() == animator*/ running) {
            // Display the next frame of animation.
//                repaint();
            scene.tick();
            if (gameViewer.getContinuousUpdatesState())
                gameViewer.tick();

            float alpha = 0;

//            og.setColor(Color.RED);
            if (GlobalOptions.VisualizationOn) {
                og.fillRect(0, 0, 320, 240);
                scene.render(og, alpha);
            }

            if (agent instanceof ServerAgent && !((ServerAgent) agent).isAvailable()) {
                System.err.println("Agent became unavailable. Simulation Stopped");
                running = false;
                break;
            }

            boolean[] action = agent.getAction(this/*DummyEnvironment*/);
            for (int i = 0; i < IEnvironment.NumberOfActions; ++i)
                if (action[i]) {
                    ++totalActionsPerfomed;
                    break;
                }
            //Apply Action;
//            scene.keys = action;
            ((LevelScene) scene).mario.keys = action;
            ((LevelScene) scene).mario.cheatKeys = cheatAgent.getAction(null);

            if (GlobalOptions.VisualizationOn) {
//                String msg = GlobalOptions.CurrentAgentStr + ". ";
                String msg = "Attempts: " + currentAttempt + " of " + totalNumberOfAttempts;
                drawString(og, msg, 7, 31, 0);
                drawString(og, msg, 6, 30, 1);

                msg = agent.getName();
                drawString(og, msg, 7, 41, 0);
                drawString(og, msg, 6, 40, 2);
                msg = "Selected Actions: ";
                drawString(og, msg, 7, 51, 0);
                drawString(og, msg, 6, 50, 2);

                msg = "";
                for (int i = 0; i < IEnvironment.NumberOfActions; ++i)
                    msg += (action[i]) ? scene.keysStr[i] : "      ";

                drawString(og, msg, 6, 70, 1);


                if (!this.hasFocus() && tick / 4 % 2 == 0) {
                    String msgClick = "CLICK TO PLAY";

//                    og.setColor(Color.YELLOW);
//                    og.drawString(msgClick, 320 + 1, 20 + 1);
                    drawString(og, msgClick, 160 - msgClick.length() * 4, 110, 1);
                    drawString(og, msgClick, 160 - msgClick.length() * 4, 110, 7);
                }
                og.setColor(Color.DARK_GRAY);
                drawString(og, "FPS: " + ((GlobalOptions.FPS > 99) ? "\\infty" : GlobalOptions.FPS.toString()), 5, 22, 0);
                drawString(og, "FPS: " + ((GlobalOptions.FPS > 99) ? "\\infty" : GlobalOptions.FPS.toString()), 4, 21, 7);

                if (width != 320 || height != 240) {
                        g.drawImage(image, 0, 0, 640 * 2, 480 * 2, null);
                } else {
                    g.drawImage(image, 0, 0, null);
                }
            } else {
                // Win or Die without renderer!! independently.
                marioStatus = ((LevelScene) scene).mario.getStatus();
                if (marioStatus != Mario.STATUS_RUNNING)
                    stop();
            }
            // Delay depending on how far we are behind.
            if (delay > 0)
                try {
                    tm += delay;
                    Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
                } catch (InterruptedException e) {
                    break;
                }

            // Advance the frame
            frame++;
        }
//=========
        // TODO: distinguish map coordinates, physical coordinates. done
        evaluationInfo.agentType = agent.getClass().getSimpleName();
        evaluationInfo.agentName = agent.getName();
        evaluationInfo.marioStatus = mario.getStatus();
        evaluationInfo.livesLeft = mario.lives;
        evaluationInfo.lengthOfLevelPassedPhys = mario.x;
        evaluationInfo.lengthOfLevelPassedCells = mario.mapX;
        evaluationInfo.totalLengthOfLevelCells = levelScene.level.getWidthCells();
        evaluationInfo.totalLengthOfLevelPhys = levelScene.level.getWidthPhys();
        evaluationInfo.timeSpentOnLevel = levelScene.getStartTime();
        evaluationInfo.timeLeft = levelScene.getTimeLeft();
        evaluationInfo.totalTimeGiven = levelScene.getTotalTime();
        evaluationInfo.numberOfGainedCoins = Mario.coins;
//        evaluationInfo.totalNumberOfCoins   = -1 ; // TODO: total Number of coins.
        evaluationInfo.totalActionsPerfomed = totalActionsPerfomed; // Counted during the play/simulation process
        evaluationInfo.totalFramesPerfomed = frame;
        evaluationInfo.Memo = "Number of attempt: " + Mario.numberOfAttempts;
        return evaluationInfo;
    }

    private void drawString(Graphics g, String text, int x, int y, int c) {
        char[] ch = text.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            g.drawImage(Art.font[ch[i] - 32][c], x + i * 8, y, null);
        }
    }

//    public void keyPressed(KeyEvent arg0)
//    {
//        toggleKey(arg0.getKeyCode(), true);
//    }
//
//    public void keyReleased(KeyEvent arg0)
//    {
//        toggleKey(arg0.getKeyCode(), false);
//    }

    public void startLevel(long seed, int difficulty, int type, int levelLength) {
        scene = new LevelScene(graphicsConfiguration, this, seed, difficulty, type, levelLength);
        levelScene = ((LevelScene) scene);
        scene.init();
    }

    public void levelFailed() {
//        scene = mapScene;
        Mario.lives--;
        stop();
    }

    public void focusGained(FocusEvent arg0) {
        focused = true;
    }

    public void focusLost(FocusEvent arg0) {
        focused = false;
    }

    public void levelWon() {
        stop();
//        scene = mapScene;
//        mapScene.levelWon();
    }

    public void toTitle() {
//        Mario.resetStatic();
//        scene = new TitleScene(this, graphicsConfiguration);
//        scene.init();
    }

    public List<String> getObservation(boolean Enemies, boolean LevelMap, boolean Complete, int ZLevel) {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).LevelSceneAroundMarioASCIIDump(Enemies, LevelMap, Complete, ZLevel);
        else {
            List<String> ret = new ArrayList<String>();
//
            return ret;
        }
    }

//    public EnvCell[][] getCompleteObservation() {
//        return new EnvCell[0][];  //To change body of implemented methods use File | Settings | File Templates.
//    }

    public EnvCell[][] getCompleteObservation() {
        return new EnvCell[0][];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public byte[][] getEnemiesObservation() {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).enemiesObservation(1);
        return null;
    }

    public byte[][] getLevelSceneObservation() {
        if (scene instanceof LevelScene)
            return ((LevelScene) scene).levelSceneObservation(1);
        return null;
    }

    public boolean isMarioOnGround() {
        return mario.isOnGround();
    }

    public boolean mayMarioJump() {
        return mario.mayJump();
    }

//    public Point getMarioPosition()
//    {
//        if (scene instanceof LevelScene)
//            return new Point(((LevelScene)scene).mario.mapX, ((LevelScene)scene).mario.mapY);
//
//        return null;
//    }

    public void setAgent(IAgent agent) {
        this.agent = agent;
        if (agent instanceof KeyAdapter) {
            if (prevHumanKeyBoardAgent != null)
                this.removeKeyListener(prevHumanKeyBoardAgent);
            this.prevHumanKeyBoardAgent = (KeyAdapter) agent;
            this.addKeyListener(prevHumanKeyBoardAgent);
        }
    }

    public void setPaused(boolean paused) {
        levelScene.paused = paused;
    }
}