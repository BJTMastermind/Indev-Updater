package me.bjtmastermind.i2ic.infdev;

import java.io.File;
import java.io.IOException;

import me.bjtmastermind.i2ic.indev.IndevLevel;
import me.bjtmastermind.i2ic.utils.ArrayUtils;
import me.bjtmastermind.i2ic.utils.Base36;
import me.bjtmastermind.i2ic.utils.CoordinateManipulation;
import me.bjtmastermind.i2ic.utils.IndevBlocks;
import me.bjtmastermind.nbt.io.NBTUtil;
import me.bjtmastermind.nbt.tag.CompoundTag;
import me.bjtmastermind.nbt.tag.DoubleTag;
import me.bjtmastermind.nbt.tag.FloatTag;
import me.bjtmastermind.nbt.tag.ListTag;
import me.bjtmastermind.nbt.tag.StringTag;
import me.bjtmastermind.nibblearray.Half;
import me.bjtmastermind.nibblearray.NibbleArray;

class InfdevChunk {
    IndevLevel indevLevel;
    // Infdev c.<x>.<z>.dat
    int xPos;
    int zPos;
    boolean terrainPopulated;
    long lastUpdate;
    byte[] blocks;
    byte[] data;
    byte[] blockLight;
    byte[] skyLight;
    byte[] heightMap;
    ListTag<CompoundTag> entities;
    ListTag<CompoundTag> tileEntities;

    public InfdevChunk(IndevLevel indevLevel) {
        this.indevLevel = indevLevel;
    }

    public InfdevChunk createChunk(int blockX, int blockZ) {
        int chunkX = CoordinateManipulation.toChunkCoord(blockX);
        int chunkZ = CoordinateManipulation.toChunkCoord(blockZ);

        this.xPos = chunkX;
        this.zPos = chunkZ;
        this.terrainPopulated = true;
        this.lastUpdate = 0L;
        this.blocks = parseChunk(blockX, blockZ);
        this.data = parseData(blockX, blockZ);
        this.blockLight = new byte[16384];
        this.skyLight = new byte[16384];
        this.heightMap = generateHeightMap(blockX, blockZ);
        this.entities = parseEntities(chunkX, chunkZ);
        this.tileEntities = parseTileEntities(chunkX, chunkZ);

        return this;
    }

    public void writeToFile(String worldRoot) throws IOException {
        CompoundTag root = new CompoundTag();
        CompoundTag level = new CompoundTag();
        level.put("xPos", this.xPos);
        level.put("zPos", this.zPos);
        level.put("TerrainPopulated", this.terrainPopulated ? (byte) 1 : (byte) 0);
        level.put("LastUpdate", this.lastUpdate);
        level.put("Blocks", this.blocks);
        level.put("Data", this.data);
        level.put("BlockLight", this.blockLight);
        level.put("SkyLight", this.skyLight);
        level.put("HeightMap", this.heightMap);
        level.put("Entities", this.entities);
        level.put("TileEntities", this.tileEntities);
        root.put("Level", level);

        File chunkDir = new File(worldRoot+File.separator+Base36.encode(xPos % 64)+File.separator+Base36.encode(zPos % 64));
        if (!chunkDir.exists()) {
            chunkDir.mkdirs();
        }
        NBTUtil.write(root, chunkDir.getPath()+File.separator+"c."+Base36.encode(xPos)+"."+Base36.encode(zPos)+".dat");
    }

    private byte[] parseChunk(int blockX, int blockZ) {
        byte[] blocks = new byte[32768];
        int startY = (indevLevel.height > 128) ? 128 : 0;
        for (int x = blockX; x < blockX + 16; x++) {
            for (int z = blockZ; z < blockZ + 16; z++) {
                for (int y = startY; y < startY + 128; y++) {
                    if (y >= 0 && y <= indevLevel.height - 1) {
                        byte block = indevLevel.blocks[(y * indevLevel.length + z) * indevLevel.width + x];
                        if (ArrayUtils.arrayContains(IndevBlocks.CLOTHS, (int) block)) {
                            // Converts all Cloth colors to White Cloth (Wool)
                            blocks[(y % 128) + ((z % 16) * 128 + ((x % 16) * 128 * 16))] = (byte) 35;
                            break;
                        }

                        switch(block) {
                            case 52:
                                // Converts Water Spawner to Water Source
                                blocks[(y % 128) + ((z % 16) * 128 + ((x % 16) * 128 * 16))] = (byte) 9;
                                break;
                            case 53:
                                // Converts Lava Spawner to Lava Source
                                blocks[(y % 128) + ((z % 16) * 128 + ((x % 16) * 128 * 16))] = (byte) 11;
                                break;
                            default:
                                blocks[(y % 128) + ((z % 16) * 128 + ((x % 16) * 128 * 16))] = (startY == 128 && y == 128) ? (byte) 7 : block;
                                break;
                        }
                    }
                }
            }
        }
        return blocks;
    }

    private byte[] parseData(int blockX, int blockZ) {
        NibbleArray data = new NibbleArray(16384);
        NibbleArray indevData = new NibbleArray(indevLevel.data);
        int startY = (indevLevel.height > 128) ? 128 : 0;
        for (int x = blockX; x < blockX + 16; x++) {
            for (int z = blockZ; z < blockZ + 16; z++) {
                for (int y = startY; y < startY + 128; y++) {
                    int index = (y * indevLevel.length + z) * indevLevel.width + x;
                    int newIndex = (y % 128) + ((z % 16) * 128 + ((x % 16) * 128 * 16));

                    int blockdata = indevData.get(index / 2, Half.HIGH);

                    if (newIndex % 2 == 0) {
                        data.set(newIndex / 2, Half.HIGH, blockdata);
                    } else {
                        data.set(newIndex / 2, Half.LOW, blockdata);
                    }
                }
            }
        }
        return data.array();
    }

    // TODO: Some how check if this is correct.
    private byte[] generateHeightMap(int blockX, int blockZ) {
        byte[] heightMap = new byte[256];
        for(int z = blockZ; z < blockZ + 16; z++) {
            for(int x = blockX; x < blockX + 16; x++) {
                for(int y = indevLevel.height - 1; y >= 0; y--) {
                    byte block = indevLevel.blocks[(y * indevLevel.length + z) * indevLevel.width + x];
                    if (block != (byte) IndevBlocks.AIR) {
                        heightMap[(16 * (x % 16)) + (z % 16)] = (byte) y;
                        break;
                    }
                }
            }
        }
        return heightMap;
    }

    private ListTag<CompoundTag> parseEntities(int chunkX, int chunkZ) {
        ListTag<CompoundTag> entities = new ListTag<>();
        for (CompoundTag entity : indevLevel.entities) {
            if (!entity.getStringTag("id").equals(new StringTag("LocalPlayer"))) {
                ListTag<FloatTag> entityPos = entity.getListTag("Pos").asFloatTagList();
                ListTag<FloatTag> entityMotion = entity.getListTag("Motion").asFloatTagList();
                if ((int) (entityPos.get(0).asFloat() / 16f) == chunkX && (int) (entityPos.get(2).asFloat() / 16f) == chunkZ) {
                    CompoundTag updatedEntity = entity.clone();

                    ListTag<DoubleTag> newPos = new ListTag<>();
                    newPos.add((double) entityPos.get(0).asFloat());
                    newPos.add((double) entityPos.get(1).asFloat());
                    newPos.add((double) entityPos.get(2).asFloat());
                    updatedEntity.remove("Pos");
                    updatedEntity.put("Pos", newPos);

                    ListTag<DoubleTag> newMotion = new ListTag<>();
                    newMotion.add((double) entityMotion.get(0).asFloat());
                    newMotion.add((double) entityMotion.get(1).asFloat());
                    newMotion.add((double) entityMotion.get(2).asFloat());
                    updatedEntity.remove("Motion");
                    updatedEntity.put("Motion", newMotion);

                    entities.add(updatedEntity);
                }
            }
        }
        return entities;
    }

    private ListTag<CompoundTag> parseTileEntities(int chunkX, int chunkZ) {
        ListTag<CompoundTag> tileEntities = new ListTag<>();
        for (CompoundTag tileEntity : indevLevel.tileEntities) {
            int[] xyz = CoordinateManipulation.posToXYZ(tileEntity.getInt("Pos"));
            if (xyz[0] / 16 == chunkX && xyz[2] / 16 == chunkZ) {
                tileEntity.remove("Pos");
                tileEntity.put("x", xyz[0]);
                tileEntity.put("y", xyz[1]);
                tileEntity.put("z", xyz[2]);

                tileEntities.add(tileEntity);
            }
        }
        return tileEntities;
    }
}
