package ch.idsia.java.utils;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 25, 2009
 * Time: 11:05:09 AM
 * Package: com.mojang.mario.Tools
 */
public class SmartBool implements java.io.Serializable,
                                      Comparable<SmartBool>,
                                        ISmart<Boolean>
{
    public Boolean getValue() {
        return value;
    }

    public SmartBool setValue(Boolean value) {
        this.value = value;
        return this;
    }

    public ISmart setValueFromStr(String value) {
        this.value = value.equals("on");
        return this; 
    }

    private Boolean value;

    public int compareTo(SmartBool o) {
        return value.compareTo(o.value);
    }
}
