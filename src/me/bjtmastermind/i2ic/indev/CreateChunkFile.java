package me.bjtmastermind.i2ic.indev;

import java.io.IOException;
import java.util.ArrayList;

import me.bjtmastermind.i2ic.util.Utils;
import me.bjtmastermind.nbt.io.NBTUtil;
import me.bjtmastermind.nbt.io.NamedTag;
import me.bjtmastermind.nbt.tag.CompoundTag;
import me.bjtmastermind.nbt.tag.ListTag;

public class CreateChunkFile {
	public static void createChunk(String filename, String path, int blockX, int blockZ) throws IOException {
		NamedTag ml = NBTUtil.read(filename);
		CompoundTag mclevel = (CompoundTag) ml.getTag();
		short widthOfMap = mclevel.getCompoundTag("Map").getShort("Width");
		short lengthOfMap = mclevel.getCompoundTag("Map").getShort("Length");
		short heightOfMap = mclevel.getCompoundTag("Map").getShort("Height");
		byte[] blocks = mclevel.getCompoundTag("Map").getByteArray("Blocks");
		byte[] data = mclevel.getCompoundTag("Map").getByteArray("Data");
		
		CompoundTag chunk = new CompoundTag();		
		CompoundTag level = new CompoundTag();
		level.put("LastUpdate", (long) 200);
		level.put("TerrainPopulated", (byte) 1);
		level.put("xPos", 0);
		level.put("zPos", 0);
		level.put("BlockLight", new byte[16384]);
		level.put("Blocks", getChunk(widthOfMap, heightOfMap, lengthOfMap, blocks, blockX, blockZ, true));
		level.put("Data", getChunk(widthOfMap, heightOfMap, lengthOfMap, data, blockX, blockZ, false));
		level.put("HeightMap", new byte[256]);
		level.put("SkyLight", new byte[16384]);
		ListTag<CompoundTag> entities = new ListTag<CompoundTag>();
		
		level.put("Entities", entities);
		ListTag<CompoundTag> tileEntities = new ListTag<CompoundTag>();
		
		level.put("TileEntities", tileEntities);
		chunk.put("Level", level);
		
		int chunkX = getChunkFileName(blockX, blockZ)[0];
		int chunkZ = getChunkFileName(blockX, blockZ)[1];
		
		CreateFolderStructure.makeChunkFolder(path+"World5/", chunkX);
		CreateFolderStructure.makeChunkFolder(path+"World5/"+Utils.base36(chunkX)+"/", chunkZ);
		NBTUtil.write(chunk, path+"World5/"+Utils.base36(chunkX)+"/"+Utils.base36(chunkZ)+"/c."+Utils.base36(chunkX)+"."+Utils.base36(chunkZ)+".dat");
	}
	
	private static byte[] getChunk(short worldWidth, short worldHeight, short worldLength, byte[] worldArray, int cx, int cz, boolean resize) {
		short widthOfMap = worldWidth;
		short lengthOfMap = worldLength;
		short heightOfMap = worldHeight;
		byte[] blocks = worldArray;
		ArrayList<Byte> chunk = new ArrayList<Byte>();
		int startAtX = cx;
		int startAtZ = cz;
		int startAtY = 0;
		if(heightOfMap == 256) startAtY = 128;
		for(int x = startAtX; x <= startAtX + 15; x++) {
			for(int z = startAtZ; z <= startAtZ + 15; z++) {
				for(int y = startAtY; y < heightOfMap; y++) {
					// Ajust build height to be 128 for Infdev
					if(resize == true && y == 63) {
						for(int i = 0; i < 64; i++) {
							chunk.add((byte) 0);
						}
					}
					byte b = blocks[(y * widthOfMap + z) * lengthOfMap + x];
					chunk.add(b);
					//if(resize == true) System.out.println("XYZ: "+x+" "+y+" "+z+" Block: "+ID2Name.getBlockName(b));
				}
			}
		}
		// Ajust build height to be 128 for Infdev
		if(resize == true && startAtY == 128) {
			for(int x = 0; x <= 15; x++) {
				for(int z = 0; z <= 15; z++) {
					chunk.set(x * widthOfMap + z, (byte) 7);
				}
			}
		}
		// Convert ArrayList<Byte> to byte[]
		byte[] chunkOut = new byte[chunk.size()];
		for(int i = 0; i < chunk.size(); i++) {
			chunkOut[i] = chunk.get(i);
		}
		return chunkOut;
	}
	
	private static int[] getChunkFileName(int blockX, int blockZ) {
		int chunkX = blockX >> 4;
		int chunkZ = blockZ >> 4;
		int[] chunk = {chunkX, chunkZ};
		return chunk;
		//return "c."+chunkX+"."+chunkZ+".dat";
	}
}
