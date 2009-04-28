package com.mojang.mario.Tools;

import com.mojang.mario.GlobalOptions;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Mar 29, 2009
 * Time: 3:34:13 PM
 * Package: com.mojang.mario.Tools
 */
public class GameViewer extends JFrame
{
    Dimension defaultSize = new Dimension(900, 800);
    Point defaultLocation = new Point(350, 10);

    //    Thread animator;
    //    int frame;
    int delay;
    int FPS = 5;

    public void AdjustFPS()
    {
        int fps = FPS; // GlobalOptions.FPS;
        delay = (fps > 0) ? (fps >= GlobalOptions.InfiniteFPS) ? 0 : (1000 / fps) : 100;
        System.out.println("Game Viewer animator delay: " + delay);
    }

    GameViewerView gameViewerViewPanel = new GameViewerView();

    private class GameViewerView extends JPanel implements Runnable
    {
        Thread animator;
        public void start()
        {
            animator = new Thread(this);
            animator.start();
        }

        public void stop()
        {
            animator = null;
        }

        public void paint(Graphics g)
        {
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.YELLOW);
            int y_dump = 0;
            g.drawString("Current GAME STATE: ", 320, y_dump += 11 );
            g.setColor(Color.GREEN);
            if (toolsConfigurator.getMarioComponent() != null)
                for (String s: toolsConfigurator.getMarioComponent().getObservation(
                        ShowEnemiesObservation.getState(),
                        ShowLevelMapObservation.getState(),
                        ShowCompleteObservation.getState(),
                        ZLevelValue) )
                {
                    g.setColor((s.charAt(0) == '~') ? Color.YELLOW : Color.GREEN);
                    g.drawString(s, 0, y_dump += 11);
                }
        }
        
        public void run()
        {
            // Remember the starting time
            long tm = System.currentTimeMillis();
            while (Thread.currentThread() == animator)
            {
                // Display the next frame of animation.
                repaint();

                // Delay depending on how far we are behind.
                try {
                    tm += delay;
                    Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
                } catch (InterruptedException e) {
                    break;
                }

                // Advance the frame
//                frame++;
            }
        }

    };

    public void tick()
    {
        gameViewerViewPanel.repaint();
    }


    public TextField Console = new TextField();
    public Label LabelConsole = new Label("TextFieldConsole:");
    public Checkbox ShowLevelMapObservation = new Checkbox("Show Level Map Observation", true);
    public Checkbox ShowEnemiesObservation = new Checkbox("Show Enemies Observation");
    public Checkbox ShowCompleteObservation = new Checkbox("Show Complete Observation");
    public Button btnUpdate = new Button("Update");
    public Checkbox ContinuousUpdates = new Checkbox("Continuous Updates", false);
    CheckboxGroup ZLevel = new CheckboxGroup();
    Checkbox Z0 = new Checkbox("Z0", ZLevel, true);
    Checkbox Z1 = new Checkbox("Z1", ZLevel, false);
    Checkbox Z2 = new Checkbox("Z2", ZLevel, false);

    private ToolsConfigurator toolsConfigurator = null;
    private int ZLevelValue = 0;

    public GameViewer(Dimension size, Point location)
    {
        super(" Game Viewer");

        setSize((size == null) ? defaultSize : size);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        defaultLocation.setLocation(screenSize.getWidth() - defaultSize.getWidth(), 0 );

        setLocation((location == null) ? defaultLocation : location);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GameViewerActions gameViewerActions = new GameViewerActions();
        ShowEnemiesObservation.addItemListener(gameViewerActions);
        Console.addActionListener(gameViewerActions);
        ShowLevelMapObservation.addItemListener(gameViewerActions);
        ShowCompleteObservation.addItemListener(gameViewerActions);
        btnUpdate.addActionListener(gameViewerActions);
        ContinuousUpdates.addItemListener(gameViewerActions);
        Z0.addItemListener(gameViewerActions);
        Z1.addItemListener(gameViewerActions);
        Z2.addItemListener(gameViewerActions);

        JPanel GameViewerOptionsPanel = new JPanel(new GridLayout(0,2));
        GameViewerOptionsPanel.add(Z0); GameViewerOptionsPanel.add(LabelConsole);
        GameViewerOptionsPanel.add(Z1); GameViewerOptionsPanel.add(Console);
        GameViewerOptionsPanel.add(Z2); GameViewerOptionsPanel.add(btnUpdate);
        GameViewerOptionsPanel.add(ShowLevelMapObservation); GameViewerOptionsPanel.add(ShowEnemiesObservation);
        GameViewerOptionsPanel.add(ShowCompleteObservation); GameViewerOptionsPanel.add(ContinuousUpdates);
        ContinuousUpdates.setState(GlobalOptions.GameVeiwerContinuousUpdatesOn);

        GameViewerOptionsPanel.setBorder(new TitledBorder(new EtchedBorder(), "Game Viewer Options"));

        Dimension sizeOfView = new Dimension(1600, 960);
        gameViewerViewPanel.setPreferredSize(sizeOfView);
        gameViewerViewPanel.setMinimumSize(sizeOfView);
        gameViewerViewPanel.setMaximumSize(sizeOfView);
        gameViewerViewPanel.setBorder(new TitledBorder(new EtchedBorder(), "Game Viewer View"));

//        JPanel lowerPanel = new JPanel(new GridLayout(0, 1));
//        lowerPanel.add(GameViewerOptionsPanel);
//        lowerPanel.add(new JScrollPane(gameViewerViewPanel));

        JPanel borderPanel = new JPanel(new BorderLayout());
//        levelEditView = new LevelEditView(tilePicker);
//        borderPanel.add(BorderLayout.CENTER, new JScrollPane(levelEditView));
        borderPanel.add(BorderLayout.NORTH, GameViewerOptionsPanel);
        borderPanel.add(BorderLayout.CENTER, new JScrollPane(gameViewerViewPanel));
        setContentPane(borderPanel);
        
        GlobalOptions.registerGameViewer(this);
    }


    public class GameViewerActions implements ActionListener, ItemListener
    {
        public void actionPerformed(ActionEvent ae)
        {
            Object ob = ae.getSource();
            if (ob == Console)
            {
                LabelConsole.setText("TextFieldConsole sent message:");
                toolsConfigurator.setConsoleText(Console.getText());
            }
            else if (ob == btnUpdate)
            {
                gameViewerViewPanel.repaint();
            }
//            else
//            {
//                iw.setVisible(false);
//                b.setLabel("Show");
//            }
        }

        public void itemStateChanged(ItemEvent ie)
        {
            Object ob = ie.getSource();
            if (ob == ShowEnemiesObservation)
            {
                Console.setText("Enemies " + (ShowEnemiesObservation.getState() ? "Shown" : "Hidden") );
                gameViewerViewPanel.repaint();
            }
            else if (ob == ShowLevelMapObservation)
            {
                Console.setText("Level Map " + (ShowLevelMapObservation.getState() ? "Shown" : "Hidden") );
                gameViewerViewPanel.repaint();
            }
            else if (ob == ShowCompleteObservation)
            {
                Console.setText("Complete Observation " + (ShowCompleteObservation.getState() ? "Shown" : "Hidden") );
                gameViewerViewPanel.repaint();
            }
            else if (ob == ContinuousUpdates)
            {
                Console.setText("Continuous Updates " + (ContinuousUpdates.getState() ? "On" : "Off") );
//                if (ContinuousUpdates.getState())
//                    gameViewerViewPanel.start();
//                else
//                    gameViewerViewPanel.stop();

            }
            else if (ob == Z0)
            {
                if (Z0.getState())
                    ZLevelValue = 0;
                Console.setText("Zoom Level: Z" + ZLevelValue);
                gameViewerViewPanel.repaint();
            }
            else if (ob == Z1)
            {
                if (Z1.getState())
                    ZLevelValue = 1;
                Console.setText("Zoom Level: Z" + ZLevelValue);
                gameViewerViewPanel.repaint();
            }
            else if (ob == Z2)
            {
                if (Z2.getState())
                    ZLevelValue = 2;
                Console.setText("Zoom Level: Z" + ZLevelValue);
                gameViewerViewPanel.repaint();
            }
        }

    }

    public void setToolsConfigurator(ToolsConfigurator toolsConfigurator){this.toolsConfigurator = toolsConfigurator;}

    public void setConsoleText(String text)
    {
        LabelConsole.setText("TextFieldConsole got message:");
        Console.setText(text);
    }

    public boolean getContinuousUpdatesState()
    {
        return ContinuousUpdates.getState();
    }

}
