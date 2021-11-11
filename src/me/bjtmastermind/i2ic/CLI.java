package me.bjtmastermind.i2ic;

import java.io.File;
import java.io.IOException;

import me.bjtmastermind.i2ic.toInfdev.CreateChunkFile;
import me.bjtmastermind.i2ic.toInfdev.CreateFolderStructure;
import me.bjtmastermind.i2ic.toInfdev.CreateLevelFile;
import me.bjtmastermind.nbt.io.NBTUtil;
import me.bjtmastermind.nbt.io.NamedTag;
import me.bjtmastermind.nbt.tag.CompoundTag;

public class CLI {
	public static void getArgs(String[] args) {
		String inputFile = "";
		boolean gotInput = false;
		String outVersion = "";
		boolean gotOutput = false;
		
		for (int i = 0; i < args.length; i++) {
			if(args[i].startsWith("-i=") || args[i].startsWith("--input=")) {
				String check = args[i].split("=")[1];
				if(!gotInput && check.contains(".mclevel")) {
					inputFile = args[i].split("=")[1];
					gotInput = true;
				} else if(gotInput) {
					System.err.println("Found duplicate flag \"-i\" or \"--input\"");
					return;
				} else if(args.length > 2) {
					System.err.println("Found to many flags!");
					return;
				}
			}
			
			if(args[i].startsWith("-o=") || args[i].startsWith("--outputVersion=")) {
				String check = args[i].split("=")[1];
				if(!gotOutput && check.equals("infdev") || check.equals("alpha") || check.equals("beta") || check.equals("1.12.2")) {
					outVersion = args[i].split("=")[1];
					gotOutput = true;
				} else if(gotOutput) {
					System.err.println("Found duplicate flag \"-o\" or \"--outputVersion\"");
					return;
				}
			}
		}
		
		if(!inputFile.isEmpty() && !outVersion.isEmpty()) {
			runConvert(inputFile, outVersion);
		} else {
			System.err.println("Not all argument were given, or argument values are in the wrong place.");
		}
	}
	
	private static void runConvert(String inFile, String outType) {
		try {			
			NamedTag ml = NBTUtil.read(inFile);
			CompoundTag mclevel = (CompoundTag) ml.getTag();
			
			int widthOfMap = (int) mclevel.getCompoundTag("Map").getShort("Width");
			int lengthOfMap = (int) mclevel.getCompoundTag("Map").getShort("Length");
		
			if(outType.equals("infdev")) {			
				System.out.println("Converting Chunks . . .");
				CreateFolderStructure.create(inFile);
				Thread.sleep(200);
			
				String fileName = new File(inFile).getName();
				String path = inFile.split(fileName)[0];
		
				for(int x = 0; x <= widthOfMap - 16; x += 16) {
					for(int z = 0; z <= lengthOfMap - 16; z += 16) {
						CreateChunkFile.createChunk(inFile, path, x, z);
					}
				}					
				System.out.println("Chunks Converted!");
			
				CreateLevelFile.createLevel(inFile, path);
				System.out.println("Conversion of " + new File(inFile).getName() + " Complete!");				
			} else if(outType.equals("alpha")) {
				System.out.println("To Alpha");
			} else if(outType.equals("beta")) {
				System.out.println("To Beta");
			} else if(outType.equals("1.12.2")) {
				System.out.println("To 1.12.2");
			}
		} catch(IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
