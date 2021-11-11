package me.bjtmastermind.i2ic.toInfdev;

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
	@SuppressWarnings("unchecked")
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
		byte[] newBlocks = getChunk(widthOfMap, heightOfMap, lengthOfMap, blocks, blockX, blockZ);
		byte[] newData = getData(widthOfMap, heightOfMap, lengthOfMap, data, blockX, blockZ);
		level.put("Blocks", newBlocks);
		level.put("Data", newData);
		level.put("HeightMap", getHeightMap(widthOfMap, heightOfMap, lengthOfMap, blocks, blockX, blockZ));
		level.put("BlockLight", new byte[16384]);
		level.put("SkyLight", new byte[16384]);
		level.put("Entities", getEntities(entities, heightOfMap, blockX, blockZ));
		level.put("TileEntities", getTileEntities(tileEntities, blockX, blockZ));
		chunk.put("Level", level);
		
		int chunkX = getChunkCoord(blockX);
		int chunkZ = getChunkCoord(blockZ);
		
		CreateFolderStructure.makeChunkFolder(path+"World5/", chunkX);
		CreateFolderStructure.makeChunkFolder(path+"World5/"+Utils.base36(chunkX)+"/", chunkZ);
		NBTUtil.write(chunk, path+"World5/"+Utils.base36(chunkX)+"/"+Utils.base36(chunkZ)+"/c."+Utils.base36(chunkX)+"."+Utils.base36(chunkZ)+".dat");
	}
	
	private static byte[] getChunk(short worldWidth, short worldHeight, short worldLength, byte[] worldArray, int startX, int startZ) {
		ArrayList<Byte> chunk = new ArrayList<Byte>();
		int startY = 0;
		if(worldHeight == 256) startY = 128;
		for(int x = startX; x <= startX + 15; x++) {
			for(int z = startZ; z <= startZ + 15; z++) {
				for(int y = startY; y < worldHeight; y++) {
					// Ajust build height to be 128 for Infdev
					if(y == 63) {
						for(int i = 0; i < 64; i++) {
							chunk.add((byte) 0);
						}
					}
					byte block = worldArray[(y * worldLength + z) * worldWidth + x];
					
					// Converts all Cloth colors to White as only White Cloth is in Infdev
					// May add option to convert to Beta or 1.12.2 to keep these blocks unchanged.
					switch(block) {
						case 21:
						case 22:
						case 23:
						case 24:
						case 25:
						case 26:
						case 27:
						case 28:
						case 29:
						case 30:
						case 31:
						case 32:
						case 33:
						case 34:
						case 36:
							chunk.add((byte) 35);
							System.out.println("Converted Cloth to White");
							break;
						case 52:
							// Converts Infinite Water to Water Source
							chunk.add((byte) 9);
							break;
						case 53:
							// Converts Infinite Lava to Lava Source
							chunk.add((byte) 11);
							break;
						default:
							chunk.add(block);
							break;
					}					
				}
			}
		}
		// Adjust build height to be 128 for Infdev
		if(startY == 128) {
			for(int x = 0; x <= 15; x++) {
				for(int z = 0; z <= 15; z++) {
					chunk.set(x * worldWidth + z, (byte) 7);
				}
			}
		}
		// Return ArrayList<Byte> as byte[]
		return Utils.toByteArray(chunk);
	}
	
	private static byte[] getData(short worldWidth, short worldHeight, short worldLength, byte[] data, int startX, int startZ) {
		byte[] newData = new byte[16384];
		int startY = 0;
		if(worldHeight == 256) startY = 128;
		for(int x = startX; x <= startX + 15; x++) {
			for(int z = startZ; z <= startZ + 15; z++) {
				for(int y = startY; y < worldHeight; y++) {
					int index = (y * worldLength + z) * worldWidth + x;
					if(index % 2 == 0) {
						newData[data[index / 2] & 0x0F] = data[index / 2];
					} else {
						newData[(data[index / 2] << 4) & 0xF0] = data[index / 2];
					}
				}
			}
		}
		return newData;
	}
	
	private static byte[] getHeightMap(short worldWidth, short worldHeight, short worldLength, byte[] blocks, int cx, int cz) {
		int startAtX = cx;
		int startAtZ = cz;
		ArrayList<Byte> heightMap = new ArrayList<Byte>();
		for(int x = startAtX; x <= startAtX + 15; x++) {
			for(int z = startAtZ; z <= startAtZ + 15; z++) {
				for(int y = worldHeight - 1; y >= 0; y--) {
					byte b = blocks[(y * worldLength + z) * worldWidth + x];
					if(b != (byte) 0) {
						heightMap.add((byte) y);
						break;
					}
				}
			}
		}
		return Utils.toByteArray(heightMap);
	}
	
	private static ListTag<CompoundTag> getEntities(ListTag<CompoundTag> entities, short worldHeight, int startAtX, int startAtZ) {
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
					// Adjust Y position for Deep worlds (as there cut in half to fit)
					if (worldHeight == 256) {
						pos.add(posY - 128);
					} else {
						pos.add(posY);
					}
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
