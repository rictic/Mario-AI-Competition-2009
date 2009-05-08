package ch.idsia.tools;

import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.mario.engine.MarioComponent;
import ch.idsia.ai.agents.IAgent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.ai.agents.ai.ForwardAgent;
import ch.idsia.ai.agents.ai.RandomAgent;
import ch.idsia.ai.agents.ai.ForwardJumpingAgent;
import ch.idsia.ai.SimpleMLPAgent;
import ch.idsia.mario.engine.level.LevelGenerator;
import ch.idsia.tools.Network.ServerAgent;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.Random;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Mar 29, 2009
 * Time: 6:27:25 PM
 * Package: com.mojang.mario.Tools
 */
public class ToolsConfigurator extends JFrame
{
    private Evaluator evaluator;

    public static void main(String[] args)
    {
        CmdLineOptions cmdLineOptions = new CmdLineOptions(args);
        // Create an Agent here
        new ForwardAgent();
        new HumanKeyboardAgent();
        new RandomAgent();
        new ForwardJumpingAgent();
        new SimpleMLPAgent();
        new ServerAgent(cmdLineOptions.getServerAgentPort(), cmdLineOptions.isServerAgentEnabled());

         // TODO: more options:
        // -agent wox name, like evolvable
        // -ll digit  range [5:15], increase if succeeds.
        // -vb nothing/all/keys
        // -exit on finish simulating
        // run 9 windows.

        ToolsConfigurator toolsConfigurator = new ToolsConfigurator(null, null);
        toolsConfigurator.setVisible(cmdLineOptions.isToolsConfigurator());

        //TODO: ReImplement MVC Concept better
        toolsConfigurator.ChoiceLevelType.select(cmdLineOptions.getLevelType());
        toolsConfigurator.JSpinnerLevelDifficulty.setValue(cmdLineOptions.getLevelDifficulty());
        toolsConfigurator.JSpinnerLevelRandomizationSeed.setValue(cmdLineOptions.getLevelRandSeed());
        toolsConfigurator.JSpinnerLevelLength.setValue(cmdLineOptions.getLevelLength());
        toolsConfigurator.CheckboxShowVizualization.setState(cmdLineOptions.isVisualization());
        toolsConfigurator.JSpinnerMaxAttempts.setValue(cmdLineOptions.getAttemptsNumber());
        toolsConfigurator.ChoiceAgent.select(cmdLineOptions.getAgentName());
        toolsConfigurator.CheckboxMaximizeFPS.setState(cmdLineOptions.isMaxFPS());
        toolsConfigurator.CheckboxPauseWorld.setState(cmdLineOptions.isPauseWorld());
        toolsConfigurator.CheckboxPowerRestoration.setState(cmdLineOptions.isPowerRestoration());
        toolsConfigurator.CheckboxStopSimulationIfWin.setState(cmdLineOptions.isStopSimulationIfWin());
        toolsConfigurator.CheckboxExitOnFinish.setState(cmdLineOptions.isExitProgramWhenFinished());
        toolsConfigurator.TextFieldMatLabFileName.setText(cmdLineOptions.getMatlabFileName());

        GlobalOptions.CurrentAgentStr = toolsConfigurator.ChoiceAgent.getSelectedItem();

        gameViewer = new GameViewer(null, null);

//        CreateMarioComponentFrame(cmdLineOptions.getViewLocation(),
//                                  cmdLineOptions.isViewAlwaysOnTop());
//        marioComponent.init();

        toolsConfigurator.setMarioComponent(marioComponent);

        toolsConfigurator.setGameViewer(gameViewer);
        gameViewer.setAlwaysOnTop(false);
        gameViewer.setToolsConfigurator(toolsConfigurator);

        if (cmdLineOptions.isGameViewer())
        {
            gameViewer.setVisible(true);
        }

        if (!cmdLineOptions.isToolsConfigurator())
        {
            toolsConfigurator.simulateOrPlay();
        }
    }



    private static JFrame marioComponentFrame = null;
    public static void CreateMarioComponentFrame()
    {
        CreateMarioComponentFrame(new Point(0, 0), false, true);
    }

    static void CreateMarioComponentFrame(Point location, boolean isAlwaysOnTop, boolean visualization)
    {
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        frame.setLocation((screenSize.width-frame.getWidth())/2, (screenSize.height-frame.getHeight())/2);        
        if (marioComponentFrame == null)
            marioComponentFrame = new JFrame(GlobalOptions.CurrentAgentStr + " - Mario Intelligent 2.0");
        if (marioComponent == null)
        {
            marioComponent = new MarioComponent(320, 240);
            marioComponentFrame.setContentPane(marioComponent);
            marioComponent.init();
            marioComponentFrame.pack();
            marioComponentFrame.setResizable(false);
            marioComponentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            marioComponentFrame.setAlwaysOnTop(isAlwaysOnTop);
        }
        marioComponentFrame.setLocation(location);
        marioComponentFrame.setVisible(visualization);
    }

    enum INTERFACE_TYPE {CONSOLE, GUI}

    Dimension defaultSize = new Dimension(330, 100);
    Point defaultLocation = new Point(0, 320);

    public Checkbox CheckboxShowGameViewer = new Checkbox("Show Game Viewer", true);

    public Label LabelConsole = new Label("Console:");
    public TextArea TextAreaConsole = new TextArea("Console:"/*, 8,40*/);  // Verbose all, keys, events, actions, observations
    private ConsoleHistory consoleHistory;
    public Checkbox CheckboxShowVizualization = new Checkbox("Enable Visualization", GlobalOptions.VisualizationOn);
    public Checkbox CheckboxMaximizeFPS = new Checkbox("Maximize FPS");
    public Choice ChoiceAgent = new Choice();
    public Choice ChoiceLevelType = new Choice();
    public JSpinner JSpinnerLevelRandomizationSeed = new JSpinner();
    public Checkbox CheckboxEnableTimer = new Checkbox("Enable Timer", GlobalOptions.TimerOn);
    public JSpinner JSpinnerLevelDifficulty = new JSpinner();
    public Checkbox CheckboxPauseWorld = new Checkbox("Pause World");
    public Checkbox CheckboxPauseMario = new Checkbox("Pause Mario");
    public Checkbox CheckboxPowerRestoration = new Checkbox("Power Restoration");
    public JSpinner JSpinnerLevelLength = new JSpinner();
    public JSpinner JSpinnerMaxAttempts = new JSpinner();
    public Checkbox CheckboxExitOnFinish = new Checkbox("Exit on finish");
    public TextField TextFieldMatLabFileName = new TextField("FileName of output for Matlab");
    public Choice ChoiceVerbose = new Choice();
    private static final String strPlay        = "->  Play! ->";
    private static final String strSimulate    = "Simulate! ->";
    public Checkbox CheckboxStopSimulationIfWin = new Checkbox("Stop simulation If Win");
    public JButton JButtonPlaySimulate = new JButton(strPlay);
    public JButton JButtonResetEvaluationSummary = new JButton("Reset");

    private BasicArrowButton
            upFPS = new BasicArrowButton(BasicArrowButton.NORTH),
            downFPS = new BasicArrowButton(BasicArrowButton.SOUTH);

    // TODO            allowed time to use.
    // TODO : change agent on the fly. Artificial Contender concept? Human shows how to complete this level? Fir 13:38.
    // TODO Hot Agent PlugAndPlay.
    // TODO: cmdLineOptions : gui, agents,
// TODO: simulate until succeed.
    // TODO: time per level\    mean time per level
    // TODO: competition

    private int prevFPS = 24;

    private static GameViewer gameViewer = null; //new GameViewer(null, null);
    private static MarioComponent marioComponent;

    public ToolsConfigurator(Point location, Dimension size)
    {
        super("Tools Configurator");

        setSize((size == null) ? defaultSize : size);
        setLocation((location == null) ? defaultLocation : location);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Universal Listener
        ToolsConfiguratorActions toolsConfiguratorActions = new ToolsConfiguratorActions();

        //     ToolsConfiguratorOptionsPanel
//        JPanel ToolsConfiguratorOptionsPanel = new JPanel(/*new FlowLayout()*//*GridLayout(0,2)*/);
        Container ToolsConfiguratorOptionsPanel = getContentPane();

        //        CheckboxShowGameViewer
        CheckboxShowGameViewer.addItemListener(toolsConfiguratorActions);

        //              TextFieldConsole
//        TextFieldConsole.addActionListener(toolsConfiguratorActions);

        //          CheckboxShowVizualization
        CheckboxShowVizualization.addItemListener(toolsConfiguratorActions);

        //       CheckboxMaximizeFPS
        CheckboxMaximizeFPS.addItemListener(toolsConfiguratorActions);

        //        ChoiceAgent

        ChoiceAgent.addItemListener(toolsConfiguratorActions);

        Set<String> AgentsNames = RegisterableAgent.getAgentsNames();
        for (String s : AgentsNames)
            ChoiceAgent.addItem(s);

        //       ChoiceLevelType
        ChoiceLevelType.addItem("Overground");
        ChoiceLevelType.addItem("Underground");
        ChoiceLevelType.addItem("Castle");
        ChoiceLevelType.addItem("Random");
        ChoiceLevelType.addItemListener(toolsConfiguratorActions);

        //      JSpinnerLevelRandomizationSeed
        JSpinnerLevelRandomizationSeed.setToolTipText("Hint: levels with same seed are identical for in observation");
        JSpinnerLevelRandomizationSeed.setValue(1);
        JSpinnerLevelRandomizationSeed.addChangeListener(toolsConfiguratorActions); //TODO : Listener;

        //  CheckboxEnableTimer
        CheckboxEnableTimer.addItemListener(toolsConfiguratorActions);
        JSpinnerLevelDifficulty.addChangeListener(toolsConfiguratorActions);

        //     CheckboxPauseWorld
        CheckboxPauseWorld.addItemListener(toolsConfiguratorActions);

        //     CheckboxPauseWorld
        CheckboxPauseMario.addItemListener(toolsConfiguratorActions);
        CheckboxPauseMario.setEnabled(false);

        //     CheckboxCheckboxPowerRestoration
        CheckboxPowerRestoration.addItemListener(toolsConfiguratorActions);
        CheckboxPowerRestoration.setEnabled(true);

        //      CheckboxStopSimulationIfWin
        CheckboxStopSimulationIfWin.addItemListener(toolsConfiguratorActions);

        //      JButtonPlaySimulate
        JButtonPlaySimulate.addActionListener(toolsConfiguratorActions);

        //      JSpinnerLevelLength
        JSpinnerLevelLength.setValue(320);
        JSpinnerLevelLength.addChangeListener(toolsConfiguratorActions);

        //      JSpinnerMaxAttempts
        JSpinnerMaxAttempts.setValue(5);
        JSpinnerMaxAttempts.addChangeListener(toolsConfiguratorActions);

        //      CheckboxExitOnFinish
        CheckboxExitOnFinish.addItemListener(toolsConfiguratorActions);

        //      ChoiceVerbose
        ChoiceVerbose.addItem("Nothing");
        ChoiceVerbose.addItem("All");
        ChoiceVerbose.addItem("Keys pressed");
        ChoiceVerbose.addItem("Selected Actions");


        //      JPanel, ArrowButtons ++FPS, --FPS
        JPanel JPanelFPSFineTune = new JPanel();
        JPanelFPSFineTune.setBorder(new TitledBorder("++FPS/--FPS"));
        JPanelFPSFineTune.setToolTipText("Hint: Use '+' or '=' for ++FPS and '-' for --FPS from your keyboard");
        JPanelFPSFineTune.add(upFPS);
        JPanelFPSFineTune.add(downFPS);
        upFPS.addActionListener(toolsConfiguratorActions);
        downFPS.addActionListener(toolsConfiguratorActions);
        upFPS.setToolTipText("Hint: Use '+' or '=' for ++FPS and '-' for --FPS from your keyboard");
        downFPS.setToolTipText("Hint: Use '+' or '=' for ++FPS and '-' for --FPS from your keyboard");

        //      JPanelLevelOptions
        JPanel JPanelLevelOptions = new JPanel();
        JPanelLevelOptions.setLayout(new BoxLayout(JPanelLevelOptions, BoxLayout.Y_AXIS));
        JPanelLevelOptions.setBorder(new TitledBorder("Level Options"));

        JPanelLevelOptions.add(new Label("Level Type:"));
        JPanelLevelOptions.add(ChoiceLevelType);
        JPanelLevelOptions.add(new Label("Level Randomization Seed:"));
        JPanelLevelOptions.add(JSpinnerLevelRandomizationSeed);

        JPanelLevelOptions.add(new Label("Level Difficulty:"));
        JPanelLevelOptions.add(JSpinnerLevelDifficulty);
        JPanelLevelOptions.add(new Label("Level Length:"));
        JPanelLevelOptions.add(JSpinnerLevelLength);
        JPanelLevelOptions.add(CheckboxEnableTimer);
        JPanelLevelOptions.add(CheckboxPauseWorld);
        JPanelLevelOptions.add(CheckboxPauseMario);
        JPanelLevelOptions.add(CheckboxPowerRestoration);
        JPanelLevelOptions.add(JButtonPlaySimulate);


        JPanel JPanelMiscellaneousOptions = new JPanel();
        JPanelMiscellaneousOptions.setLayout(new BoxLayout(JPanelMiscellaneousOptions, BoxLayout.Y_AXIS));
        JPanelMiscellaneousOptions.setBorder(new TitledBorder("Miscellaneous Options"));


        JPanelMiscellaneousOptions.add(CheckboxShowGameViewer);

        JPanelMiscellaneousOptions.add(CheckboxShowVizualization);

//        JPanelMiscellaneousOptions.add(TextFieldConsole);
        JPanelMiscellaneousOptions.add(CheckboxMaximizeFPS);
        JPanelMiscellaneousOptions.add(JPanelFPSFineTune);
//        JPanelMiscellaneousOptions.add(JPanelLevelOptions);
        JPanelMiscellaneousOptions.add(new Label("Current Agent:"));
        JPanelMiscellaneousOptions.add(ChoiceAgent);
        JPanelMiscellaneousOptions.add(new Label("Verbose:"));
        JPanelMiscellaneousOptions.add(ChoiceVerbose);
        JPanelMiscellaneousOptions.add(new Label("Evaluation Summary: "));
        JPanelMiscellaneousOptions.add(JButtonResetEvaluationSummary);
        JPanelMiscellaneousOptions.add(new Label("Max # of attemps:"));
        JPanelMiscellaneousOptions.add(JSpinnerMaxAttempts);
        JPanelMiscellaneousOptions.add(CheckboxStopSimulationIfWin);
        JPanelMiscellaneousOptions.add(CheckboxExitOnFinish);

        JPanel JPanelConsole = new JPanel(new FlowLayout());
        JPanelConsole.setBorder(new TitledBorder("Console"));
        TextAreaConsole.setFont(new Font("Courier New", Font.PLAIN, 12));
        TextAreaConsole.setBackground(Color.BLACK);
        TextAreaConsole.setForeground(Color.GREEN);
        JPanelConsole.add(TextAreaConsole);

        // IF GUI
        consoleHistory = new ConsoleHistory(TextAreaConsole);

        ToolsConfiguratorOptionsPanel.add(BorderLayout.WEST, JPanelLevelOptions);
        ToolsConfiguratorOptionsPanel.add(BorderLayout.CENTER, JPanelMiscellaneousOptions);
        ToolsConfiguratorOptionsPanel.add(BorderLayout.SOUTH, JPanelConsole);

        JPanel borderPanel = new JPanel();
        borderPanel.add(BorderLayout.NORTH, ToolsConfiguratorOptionsPanel);
        setContentPane(borderPanel);
        // autosize: 
        this.pack();
    }

    public void simulateOrPlay()
    {
        //Simulate or Play!
        EvaluationOptions evaluationOptions = prepareEvaluatorOptions();
        assert(evaluationOptions != null);
        if (evaluator == null)
            evaluator = new Evaluator(evaluationOptions);
        else
            evaluator.init(evaluationOptions);
        evaluator.setConsole(consoleHistory);
        evaluator.start();
        consoleHistory.addRecord("Play/Simulation started!");
    }

    private EvaluationOptions prepareEvaluatorOptions()
    {
        EvaluationOptions evaluationOptions = new EvaluationOptions();
        IAgent agent = RegisterableAgent.getAgentByName(ChoiceAgent.getSelectedItem());
        evaluationOptions.setAgent(agent);
        int type = ChoiceLevelType.getSelectedIndex();
        if (type == 4)
            type = (new Random()).nextInt(4);
        evaluationOptions.setLevelType(type);
        evaluationOptions.setLevelDifficulty(Integer.parseInt(JSpinnerLevelDifficulty.getValue().toString()));
        evaluationOptions.setLevelRandSeed(Integer.parseInt(JSpinnerLevelRandomizationSeed.getValue().toString()));
        evaluationOptions.setLevelLength(Integer.parseInt(JSpinnerLevelLength.getValue().toString()));
        evaluationOptions.setVisualization(CheckboxShowVizualization.getState());
        evaluationOptions.setMaxAttempts(Integer.parseInt(JSpinnerMaxAttempts.getValue().toString()));
        evaluationOptions.setPauseWorld(CheckboxPauseWorld.getState());
        evaluationOptions.setPowerRestoration(CheckboxPowerRestoration.getState());
        evaluationOptions.setExitProgramWhenFinished(CheckboxExitOnFinish.getState());
        evaluationOptions.setMatlabFileName(TextFieldMatLabFileName.getText());
        
        return evaluationOptions;
    }


    public class ToolsConfiguratorActions implements ActionListener, ItemListener, ChangeListener
    {
        public void actionPerformed(ActionEvent ae)
        {
            Object ob = ae.getSource();
            if (ob == JButtonPlaySimulate)
            {
                simulateOrPlay();
            }
            else if (ob == upFPS)
            {
                if(++GlobalOptions.FPS >= GlobalOptions.InfiniteFPS)
                {
                    GlobalOptions.FPS = GlobalOptions.InfiniteFPS;
                    CheckboxMaximizeFPS.setState(true);
                }
                marioComponent.adjustFPS();
                consoleHistory.addRecord("FPS set to " + (CheckboxMaximizeFPS.getState() ? "infinity" : GlobalOptions.FPS) );
            }
            else if (ob == downFPS)
            {
                if(--GlobalOptions.FPS < 1)
                    GlobalOptions.FPS = 1;
                CheckboxMaximizeFPS.setState(false);
                marioComponent.adjustFPS();
                consoleHistory.addRecord("FPS set to " + (CheckboxMaximizeFPS.getState() ? "infinity" : GlobalOptions.FPS) );
            }
            else if (ob == JButtonResetEvaluationSummary)
            {
                evaluator = null;
            }

//            if (ob == TextFieldConsole)
//            {
//                LabelConsole.setText("TextFieldConsole sent message:");
//                gameViewer.setConsoleText(TextFieldConsole.getText());
//            }
//            else if (b.getActionCommand() == "Show")
//            {
//                iw.setVisible(true);
//                b.setLabel("Hide") ;
//            }
//            else
//            {
//                iw.setVisible(false);
//                b.setLabel("Show");
//            }
        }

        public void itemStateChanged(ItemEvent ie)
        {
            Object ob = ie.getSource();
            if (ob == CheckboxShowGameViewer)
            {
                consoleHistory.addRecord("Game Viewer " + (CheckboxShowGameViewer.getState() ? "Shown" : "Hidden") );
                gameViewer.setVisible(CheckboxShowGameViewer.getState());
            }
            else if (ob == CheckboxShowVizualization)
            {
                consoleHistory.addRecord("Vizualization " + (CheckboxShowVizualization.getState() ? "On" : "Off") );
                GlobalOptions.VisualizationOn = CheckboxShowVizualization.getState();
                marioComponentFrame.setVisible(GlobalOptions.VisualizationOn);
            }
            else if (ob == CheckboxMaximizeFPS)
            {
                prevFPS = (GlobalOptions.FPS == GlobalOptions.InfiniteFPS) ? prevFPS : GlobalOptions.FPS;
                GlobalOptions.FPS = CheckboxMaximizeFPS.getState() ? 100 : prevFPS;
                marioComponent.adjustFPS();
                consoleHistory.addRecord("FPS set to " + (CheckboxMaximizeFPS.getState() ? "infinity" : GlobalOptions.FPS) );
            }
            else if (ob == CheckboxEnableTimer)
            {
                GlobalOptions.TimerOn = CheckboxEnableTimer.getState();
                consoleHistory.addRecord("Timer " + (GlobalOptions.TimerOn ? "enabled" : "disabled") );
            }
            else if (ob == CheckboxPauseWorld)
            {
                GlobalOptions.pauseWorld = CheckboxPauseWorld.getState();

                marioComponent.setPaused(GlobalOptions.pauseWorld);
                consoleHistory.addRecord("World " + (GlobalOptions.pauseWorld ? "paused" : "unpaused") );
            }
            else if (ob == CheckboxPauseMario)
            {
                TextAreaConsole.setText("1\n2\n3\n");
            }
            else if (ob == CheckboxPowerRestoration)
            {
                GlobalOptions.PowerRestoration = CheckboxPowerRestoration.getState();
                consoleHistory.addRecord("Mario Power Restoration Turned " + (GlobalOptions.PowerRestoration ? "on" : "off"));
            }
            else if (ob == CheckboxStopSimulationIfWin)
            {
                GlobalOptions.StopSimulationIfWin = CheckboxStopSimulationIfWin.getState();
                consoleHistory.addRecord("Stop simulation if Win Criteria Turned " +
                        (GlobalOptions.StopSimulationIfWin ? "on" : "off"));
            }
            else if (ob == ChoiceAgent)
            {
                consoleHistory.addRecord("Agent chosen: " + (ChoiceAgent.getSelectedItem()));
                JButtonPlaySimulate.setText(strSimulate);
                GlobalOptions.CurrentAgentStr = ChoiceAgent.getSelectedItem();
            }
            else if (ob == ChoiceLevelType)
            {

            }
            else if (ob == ChoiceVerbose)
            {

            }
        }

        public void stateChanged(ChangeEvent changeEvent)
        {
            Object ob = changeEvent.getSource();
            if (ob == JSpinnerLevelRandomizationSeed)
            {
                //Change random seed in Evaluator/ Simulator Options
            }
            else if (ob == JSpinnerLevelDifficulty)
            {

            }
            else if (ob == JSpinnerLevelLength)
            {
                if (Integer.parseInt(JSpinnerLevelLength.getValue().toString()) < LevelGenerator.LevelLengthMinThreshold)
                    JSpinnerLevelLength.setValue(LevelGenerator.LevelLengthMinThreshold);
            }
        }
    }

    public void setGameViewer(GameViewer gameViewer) {        this.gameViewer = gameViewer;    }
    public void setMarioComponent(MarioComponent marioComponent)
    {
        this.marioComponent = marioComponent;
        this.marioComponent.setGameViewer(gameViewer);
    }
    public MarioComponent getMarioComponent() {          return marioComponent;    }

    public void setConsoleText(String text)
    {
        LabelConsole.setText("Console got message:");
        consoleHistory.addRecord("\nConsole got message:\n" + text);
//        TextFieldConsole.setText(text);
    }
}
