package com.mojang.mario.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 25, 2009
 * Time: 10:30:34 AM
 * Package: com.mojang.mario.Tools
 */
public final class SmartInt extends Number implements Comparable<SmartInt>, ISmart<Integer>
{
    public Integer getValue() {
        return value;
    }

    public SmartInt setValue(Integer value) {
        this.value = value;
        return this;
    }

    public SmartInt setValueFromStr(String s) {
        this.value = new Integer(s);
        return this;
    }

    private Integer value;

    public SmartInt() {
        this.value = 0;
    }


    public SmartInt(Integer value) {
        this.value = value;
    }

    public int intValue() {
        return value.intValue();
    }

    public long longValue() {
        return value.longValue();
    }

    public float floatValue() {
        return value.floatValue();
    }

    public double doubleValue() {
        return value.doubleValue();
    }

    public int compareTo(SmartInt o) {
        return value.compareTo(o.value);
    }


}
