package me.bjtmastermind.i2ic.toInfdev;

import java.io.File;

import me.bjtmastermind.i2ic.util.Utils;

public class CreateFolderStructure {
	public static void create(String filename) {
		String path = filename.split(filename.split("/")[filename.split("/").length-1])[0];
		File root = new File(path+"World5");
		root.mkdir();
	}
	
	public static void makeChunkFolder(String path, int x) {
		File xf = new File(path+Utils.base36(x));
		xf.mkdir();
	}
}
