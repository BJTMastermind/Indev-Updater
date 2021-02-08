package me.bjtmastermind.i2ic.util;

public class Utils {
	public static int[] toXYZ(int pos) {
		int x = pos % 1024;
		int y = (pos >> 10) % 1024;
		int z = (pos >> 20) % 1024;
		int[] xyz = {x, y, z};
		return xyz;
	}
	
	public static int toPos(int x, int y, int z) {
		int pos = x + (y << 10) + (z << 20);
		return pos;
	}
	
	public static String base36(int i) {
		return Integer.toString(i, 36);
	}
	
	public static int asInt(String s) {
		return Integer.valueOf(s, 36);
	}
}
