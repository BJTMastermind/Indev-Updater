package me.bjtmastermind.i2ic.utils;

public class CoordinateManipulation {
    public static int[] posToXYZ(int pos) {
        int x = pos % 1024;
        int y = (pos >> 10) % 1024;
        int z = (pos >> 20) % 1024;
        int[] xyz = {x, y, z};
        return xyz;
    }

    public static int toChunkCoord(int coord) {
        return coord >> 4;
    }
}
