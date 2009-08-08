package ch.idsia.utils;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, firstname_at_idsia_dot_ch
 * Date: Jul 25, 2009
 * Time: 3:26:43 PM
 * Package: ch.idsia.utils
 */
public class MathX
{
    
    public static char pow(int base, int power)
    {
        char ret = 1;
        for (int i = 0; i < power; ++i)
           ret *= base;
        return ret;
    }
}
