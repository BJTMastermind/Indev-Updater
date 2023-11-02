package me.bjtmastermind.i2ic.utils;

public class Base36 {

    public static String encode(int number) {
        return Integer.toString(number, 36);
    }

    public static int decode(String value) {
        return Integer.valueOf(value, 36);
    }
}
