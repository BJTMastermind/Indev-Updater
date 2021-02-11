package me.bjtmastermind.i2ic;

import java.io.IOException;

import me.bjtmastermind.i2ic.indev.CreateChunkFile;
import me.bjtmastermind.i2ic.indev.CreateFolderStructure;
import me.bjtmastermind.i2ic.indev.CreateLevelFile;
import me.bjtmastermind.i2ic.test.Test;
import me.bjtmastermind.nbt.io.NBTUtil;
import me.bjtmastermind.nbt.io.NamedTag;
import me.bjtmastermind.nbt.tag.CompoundTag;
import me.bjtmastermind.nbt.tag.ListTag;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		String file = "/home/bjtmastermind/Desktop/square.mclevel";
		String path = "/home/bjtmastermind/Desktop/";
		
		//Test.run();
		CreateFolderStructure.create(file);
		CreateLevelFile.createLevel(file, path);
		Thread.sleep(200);
		for(int x = 0; x <= (512 + 1) - 16; x += 16) {
			for(int z = 0; z <= (512 + 1) - 16; z += 16) {
				CreateChunkFile.createChunk(file, path, x, z);
			}
		}
	}
}
