package me.bjtmastermind.i2ic.utils;

public class ArrayUtils {

    public static boolean arrayContains(int[] src, int find) {
        for (int value : src) {
            if (value == find) {
                return true;
            }
        }
        return false;
    }
}
