package me.bjtmastermind.i2ic;

import java.io.IOException;

import me.bjtmastermind.i2ic.indev.CreateChunkFile;
import me.bjtmastermind.i2ic.indev.CreateFolderStructure;
import me.bjtmastermind.i2ic.indev.CreateLevelFile;
import me.bjtmastermind.i2ic.test.Test;

public class Main {

	public static void main(String[] args) throws IOException {
		String file = "~/Desktop/square.mclevel";
		String path = "~/Desktop/";
		//Test.run(file);
		CreateLevelFile.createLevel(file, path);
		CreateFolderStructure.create(file);
		for(int x = 0; x <= (512 + 1) - 16; x += 16) {
			for(int z = 0; z <= (512 + 1) - 16; z += 16) {
				CreateChunkFile.createChunk(file, path, x, z);
			}
		}
	}
}
