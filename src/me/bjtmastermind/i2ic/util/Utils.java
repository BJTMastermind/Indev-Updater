package me.bjtmastermind.i2ic.util;

import java.util.ArrayList;

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
	
	public static byte[] toByteArray(ArrayList<Byte> list) {
		byte[] array = new byte[list.size()];
		for(int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array;
	}
	
	public static int[] toChunkCoords(int x, int z) {
		int chunkX = x / 16;
		int chunkZ = z / 16;
		int[] chunkXZ = new int[2];
		chunkXZ[0] = chunkX;
		chunkXZ[1] = chunkZ;
		return chunkXZ;
	}
}
