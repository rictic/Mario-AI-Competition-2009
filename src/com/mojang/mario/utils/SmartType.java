package com.mojang.mario.utils;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 25, 2009
 * Time: 7:31:18 PM
 * Package: com.mojang.mario.Tools
 */
public class SmartType<T> implements ISmart<T> {
    T value;

    public T getValue() {
        return value;
    }

    public ISmart setValue(T value) {
        this.value = value;
        return this;
    }

    public ISmart setValueFromStr(String value) {
        if (this.value instanceof Boolean)
            this.value = (T) (new Boolean(value.equals("on")));
        else if (this.value instanceof String)
            this.value = (T) value;
        else if (this.value instanceof Integer)
            this.value = (T) new Integer(value);
        return this;
    }
}
