package ch.idsia.tools;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, firstname_at_idsia_dot_ch
 * Date: May 7, 2009
 * Time: 8:59:20 PM
 * Package: ch.idsia.tools
 */
public class ConsoleHistory
{
    TextArea textAreaConsole = null;

    public ConsoleHistory(TextArea textAreaConsole)
    {
        this.textAreaConsole = textAreaConsole;
    }

    private String history = "console:";
    public void addRecord(String record)
    {
        history += "\n" + record;
        if (textAreaConsole != null)
            textAreaConsole.setText(history);
        System.out.println(record);
    }
    public String getHistory() { return history; }
}
