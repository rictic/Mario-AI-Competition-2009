package ch.idsia.mario.utils;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 25, 2009
 * Time: 11:22:48 AM
 * Package: com.mojang.mario.Tools
 */
public interface ISmart<T>
{
    public T getValue();

    public ISmart setValue(T value);
    public ISmart setValueFromStr(String value);

}
