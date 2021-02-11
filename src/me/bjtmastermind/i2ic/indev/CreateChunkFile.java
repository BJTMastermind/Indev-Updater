package me.bjtmastermind.i2ic.indev;

import java.io.IOException;
import java.util.ArrayList;

import me.bjtmastermind.i2ic.util.Utils;
import me.bjtmastermind.nbt.io.NBTUtil;
import me.bjtmastermind.nbt.io.NamedTag;
import me.bjtmastermind.nbt.tag.CompoundTag;
import me.bjtmastermind.nbt.tag.DoubleTag;
import me.bjtmastermind.nbt.tag.ListTag;
import me.bjtmastermind.nbt.tag.StringTag;

public class CreateChunkFile {
	public static void createChunk(String filename, String path, int blockX, int blockZ) throws IOException {
		NamedTag ml = NBTUtil.read(filename);
		CompoundTag mclevel = (CompoundTag) ml.getTag();
		short widthOfMap = mclevel.getCompoundTag("Map").getShort("Width");
		short lengthOfMap = mclevel.getCompoundTag("Map").getShort("Length");
		short heightOfMap = mclevel.getCompoundTag("Map").getShort("Height");
		byte[] blocks = mclevel.getCompoundTag("Map").getByteArray("Blocks");
		byte[] data = mclevel.getCompoundTag("Map").getByteArray("Data");
		ListTag<CompoundTag> entities = (ListTag<CompoundTag>) mclevel.getListTag("Entities");
		ListTag<CompoundTag> tileEntities = (ListTag<CompoundTag>) mclevel.getListTag("TileEntities");

		CompoundTag chunk = new CompoundTag();		
		CompoundTag level = new CompoundTag();
		level.put("LastUpdate", (long) 200);
		level.put("TerrainPopulated", (byte) 1);
		level.put("xPos", getChunkCoord(blockX));
		level.put("zPos", getChunkCoord(blockZ));
		level.put("BlockLight", new byte[16384]);
		level.put("Blocks", getChunk(widthOfMap, heightOfMap, lengthOfMap, blocks, blockX, blockZ, true));
		level.put("Data", getChunk(widthOfMap, heightOfMap, lengthOfMap, data, blockX, blockZ, false));
		level.put("HeightMap", getHeightMap(widthOfMap, heightOfMap, lengthOfMap, blocks, blockX, blockZ));
		level.put("SkyLight", new byte[16384]);
		level.put("Entities", getEntities(entities, blockX, blockZ));
		level.put("TileEntities", getTileEntities(tileEntities, blockX, blockZ));
		chunk.put("Level", level);
		
		int chunkX = getChunkCoord(blockX);
		int chunkZ = getChunkCoord(blockZ);
		
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
		// Return ArrayList<Byte> as byte[]
		return Utils.toByteArray(chunk);
	}
	
	private static byte[] getHeightMap(short worldWidth, short worldHeight, short worldLength, byte[] blocks, int cx, int cz) {
		int startAtX = cx;
		int startAtZ = cz;
		ArrayList<Byte> heightMap = new ArrayList<Byte>();
		for(int x = startAtX; x <= startAtX + 15; x++) {
			for(int z = startAtZ; z <= startAtZ + 15; z++) {
				for(int y = worldHeight - 1; y >= 0; y--) {
					byte b = blocks[(y * worldWidth + z) * worldLength + x];
					if(b != (byte) 0) {
						heightMap.add((byte) y);
						break;
					}
				}
			}
		}
		return Utils.toByteArray(heightMap);
	}
	
	private static ListTag<CompoundTag> getEntities(ListTag<CompoundTag> entities, int startAtX, int startAtZ) {
		ListTag<CompoundTag> chunkEntities = new ListTag<CompoundTag>();
		for(int i = 0; i < entities.size(); i++) {
			CompoundTag entity = entities.get(i);
			if(((int) entity.getListTag("Pos").get(0).valueToFloat() >= startAtX && (int) entity.getListTag("Pos").get(0).valueToFloat() <= startAtX + 15) && ((int) entity.getListTag("Pos").get(2).valueToFloat() >= startAtZ && (int) entity.getListTag("Pos").get(2).valueToFloat() <= startAtZ + 15)) {
				if(!entity.getStringTag("id").equals(new StringTag("LocalPlayer"))) {
					// Convert float position to double
					double posX = (double) entity.getListTag("Pos").get(0).valueToFloat();
					double posY = (double) entity.getListTag("Pos").get(1).valueToFloat();
					double posZ = (double) entity.getListTag("Pos").get(2).valueToFloat();
					entity.remove("Pos");
					ListTag<DoubleTag> pos = new ListTag<DoubleTag>();
					pos.add(posX);
					pos.add(posY);
					pos.add(posZ);
					entity.put("Pos", pos);
					
					// Convert float motion to double
					double motX = (double) entity.getListTag("Motion").get(0).valueToFloat();
					double motY = (double) entity.getListTag("Motion").get(1).valueToFloat();
					double motZ = (double) entity.getListTag("Motion").get(2).valueToFloat();
					entity.remove("Motion");
					ListTag<DoubleTag> motion = new ListTag<DoubleTag>();
					motion.add(motX);
					motion.add(motY);
					motion.add(motZ);
					entity.put("Motion", motion);
					
					// If entity is in this chunk and is not a player add to list
					chunkEntities.add(entity);
				}
			}
		}
		return chunkEntities;
	}
	
	private static ListTag<CompoundTag> getTileEntities(ListTag<CompoundTag> tileEntities, int startAtX, int startAtZ) {
		ListTag<CompoundTag> chunkTileEntities = new ListTag<CompoundTag>();
		for(int i = 0; i < tileEntities.size(); i++) {
			CompoundTag tileEntity = tileEntities.get(i);
			if((Utils.toXYZ(tileEntity.getInt("Pos"))[0] >= startAtX && Utils.toXYZ(tileEntity.getInt("Pos"))[0] <= startAtX + 15) && (Utils.toXYZ(tileEntity.getInt("Pos"))[2] >= startAtZ && Utils.toXYZ(tileEntity.getInt("Pos"))[2] <= startAtZ + 15)) {
				// Convert Pos tag to x y z
				int[] xyz = Utils.toXYZ(tileEntity.getInt("Pos"));
				tileEntity.remove("Pos");
				tileEntity.put("x", xyz[0]);
				tileEntity.put("y", xyz[1]);
				tileEntity.put("z", xyz[2]);
				
				// If tile entity is in this chunk add to list
				chunkTileEntities.add(tileEntity);
			}
		}
		return chunkTileEntities;
	}
		
	private static int getChunkCoord(int coord) {
		int chunkCoord = coord >> 4;
		return chunkCoord;
	}
}
