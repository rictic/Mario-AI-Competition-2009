package ch.idsia.tools;

import java.awt.*;
import java.io.PrintStream;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, firstname_at_idsia_dot_ch
 * Date: May 7, 2009
 * Time: 8:59:20 PM
 * Package: ch.idsia.tools
 */
// TODO: rename to LOG bilk yourself is easier that the system.
public class LOGGER
{
    public enum VERBOSE_MODE {INFO, WARNING, ERROR, NONE}
    static TextArea textAreaConsole = null;
    private static VERBOSE_MODE verbose_mode = VERBOSE_MODE.NONE;
    public static void setVerboseMode(VERBOSE_MODE verboseMode)
    {
        LOGGER.verbose_mode = verboseMode;
    }

    public static void setTextAreaConsole(TextArea tac)
    {
        textAreaConsole = tac;
    }

    private static String history = "console:";

    public static void addRecord(String record, VERBOSE_MODE vm)
    {
        if (verbose_mode == VERBOSE_MODE.NONE)
            return; // Not recommended to use this mode.
        if (vm.compareTo(verbose_mode) >= 0)
        {
            if (vm.compareTo(VERBOSE_MODE.WARNING) >= 0)
                addRecord(record, System.err);
            else
                addRecord(record, System.out);
        }
    }

    private static void addRecord(String record, PrintStream ps)
    {
        history += "\n" + verbose_mode + ": " + record;
        if (textAreaConsole != null)
            textAreaConsole.setText(history);
        ps.println(record);
    }
    public static String getHistory() { return history; }
}
