package ch.idsia.tools;

import ch.idsia.mario.simulation.SimulationOptions;
import ch.idsia.mario.engine.GlobalOptions;

import java.awt.*;


/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 12, 2009
 * Time: 7:49:07 PM
 * Package: com.mojang.mario.Tools
 */
public class EvaluationOptions extends SimulationOptions
{
    public EvaluationOptions() {        super();    }

    public Boolean isExitProgramWhenFinished()    {
        return b(getParameterValue("-ewf"));    }

    public void setExitProgramWhenFinished(boolean exitProgramWhenFinished)    {
        setParameterValue("-ewf", s(exitProgramWhenFinished));    }

    public String getMatlabFileName() {
        return getParameterValue("-m");      }

    public void setMatlabFileName(String matlabFileName) {
        setParameterValue("-m", matlabFileName);    }

    public Point getViewLocation()
    {
        int x = i(getParameterValue("-vlx"));
        int y = i(getParameterValue("-vly"));
        return new Point(x, y);
    }

    public Boolean isViewAlwaysOnTop() {
        return b(getParameterValue("-vaot"));      }

    public void setMaxFPS(boolean isMaxFPS ) {
        setParameterValue("-maxFPS", s(isMaxFPS));
        GlobalOptions.FPS = (isMaxFPS()) ? GlobalOptions.InfiniteFPS : 24;
    }

    public Boolean isMaxFPS() {
        return b(getParameterValue("-maxFPS"));      }
}
