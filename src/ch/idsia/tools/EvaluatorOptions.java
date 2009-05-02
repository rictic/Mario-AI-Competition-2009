package ch.idsia.tools;

import ch.idsia.mario.simulation.SimulationOptions;
import ch.idsia.utils.SmartBool;
import ch.idsia.utils.SmartType;


/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 12, 2009
 * Time: 7:49:07 PM
 * Package: com.mojang.mario.Tools
 */
public class EvaluatorOptions extends SimulationOptions
{
    public int maxAttempts;

    protected SmartBool exitProgramWhenFinished = new SmartBool(false);
    protected SmartType<String> matlabFileName = new SmartType<String>("DefaultMatlabFileName");

    public Boolean isExitProgramWhenFinished()
    {
        return exitProgramWhenFinished.getValue();
    }

    public void setExitProgramWhenFinished(boolean exitProgramWhenFinished)
    {
        this.exitProgramWhenFinished.setValue(exitProgramWhenFinished);
    }

    public String getMatlabFileName() {
        return matlabFileName.getValue();
    }

    public void setMatlabFileName(String matlabFileName) {
        this.matlabFileName.setValueFromStr(matlabFileName);
    }

}
