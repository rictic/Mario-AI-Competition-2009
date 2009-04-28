package com.mojang.mario;

import com.mojang.mario.sprites.Sprite;
import com.mojang.mario.Tools.GameViewer;
import com.mojang.mario.Simulation.SimulationOptions;
import com.mojang.mario.level.LevelGenerator;

public class GlobalOptions {
    public static boolean Labels = false;
    public static boolean MarioAlwaysInCenter = false;
    public static Integer FPS = 24;
    public static int InfiniteFPS = 100;
    public static boolean pauseWorld = false;

    public static boolean VizualizationOn = true;
    public static boolean GameVeiwerOn = true;

    private static MarioComponent marioComponent = null;
    private static GameViewer gameViewer = null;
    public static boolean TimerOn = true;
    public static String CurrentAgentStr = "No Agent";

    public static Defaults defaults = new Defaults();
    public static boolean GameVeiwerContinuousUpdatesOn = false;
    public static boolean PowerRestoration;

    public static boolean StopSimulationIfWin;

    public static void registerMarioComponent(MarioComponent mc)
    {
        marioComponent = mc;
    }

    public static void registerGameViewer(GameViewer gv)
    {
        gameViewer = gv;
    }

    public static void AdjustMarioComponentFPS() {
        marioComponent.AdjustFPS();
    }

    public static void gameViewerTick() {
        gameViewer.tick();
    }

    public static class Defaults extends SimulationOptions
    {
        private static boolean gui;
        private static boolean toolsConfigurator;
        private static boolean gameViewer;
        private static boolean gameViewerContinuousUpdates;
        private static boolean timer;
        private static int attemptsNumber;
        private static boolean echo;
        private static boolean maxFPS;
        private static String agentName;

        public Defaults()
        {
            setLevelLength(320);
            setLevelDifficulty(0);
            setLevelRandSeed(1);
            setVisualization(true);
            setLevelType(LevelGenerator.TYPE_OVERGROUND);
            setGui(false);
            setEcho(false);
            setMaxFPS(false);
            setPauseWorld(false);
            setPowerRestoration(false);
            setStopSimulationIfWin(false);
            setAgentName("ForwardAgent");
        }

        public static boolean isGui() {  return gui; }

        public static void setGui(boolean gui) { Defaults.gui = gui;  }

        public static boolean isToolsConfigurator() {return toolsConfigurator; }

        public static void setToolsConfigurator(boolean toolsConfigurator) { Defaults.toolsConfigurator = toolsConfigurator; }

        public static boolean isGameViewer() {
            return gameViewer;
        }

        public static void setGameViewer(boolean gameViewer) {
            Defaults.gameViewer = gameViewer;
        }

        public static boolean isGameViewerContinuousUpdates() {
            return gameViewerContinuousUpdates;
        }

        public static void setGameViewerContinuousUpdates(boolean gameViewerContinuousUpdates) {
            Defaults.gameViewerContinuousUpdates = gameViewerContinuousUpdates;
        }

        public static boolean isTimer() {
            return timer;
        }

        public static void setTimer(boolean timer) {
            Defaults.timer = timer;
        }

        public static int getAttemptsNumber() {
            return attemptsNumber;
        }

        public static void setAttemptsNumber(int attemptsNumber) {
            Defaults.attemptsNumber = attemptsNumber;
        }

        public static boolean isEcho() {
            return echo;
        }

        public static void setEcho(boolean echo) {
            Defaults.echo = echo;
        }

        public static boolean isMaxFPS() {
            return maxFPS;
        }

        public static void setMaxFPS(boolean maxFPS) {
            Defaults.maxFPS = maxFPS;
        }

        public static String getAgentName() {
            return agentName;
        }

        public static void setAgentName(String agentName) {
            Defaults.agentName = agentName;
        }

    }
}
