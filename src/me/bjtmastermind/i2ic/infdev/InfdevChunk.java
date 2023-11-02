package me.bjtmastermind.i2ic.infdev;

import java.io.File;
import java.io.IOException;

import me.bjtmastermind.i2ic.indev.IndevLevel;
import me.bjtmastermind.i2ic.utils.Base36;
import me.bjtmastermind.i2ic.utils.CoordinateManipulation;
import me.bjtmastermind.i2ic.utils.IndevBlocks;
import me.bjtmastermind.nbt.io.NBTUtil;
import me.bjtmastermind.nbt.tag.CompoundTag;
import me.bjtmastermind.nbt.tag.DoubleTag;
import me.bjtmastermind.nbt.tag.FloatTag;
import me.bjtmastermind.nbt.tag.ListTag;
import me.bjtmastermind.nbt.tag.StringTag;

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
    byte[] heightMap = new byte[256];
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
        this.lastUpdate = 200L;
        this.blocks = parseChunk(blockX, blockZ);
        this.data = parseData(blockX, blockZ);
        this.blockLight = generateBlockLight(chunkX, chunkZ);
        this.skyLight = generateSkyLight(chunkX, chunkZ);
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
        int i = 0;
        for (int x = blockX; x <= blockX + 15; x++) {
            for (int z = blockZ; z <= blockZ + 15; z++) {
                for (int y = startY; y < startY + 128; y++) {
                    if (y >= 0 && y <= indevLevel.height - 1) {
                        byte block = indevLevel.blocks[(y * indevLevel.length + z) * indevLevel.width + x];
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
                                // Converts all Cloth colors to White Cloth (Wool)
                                blocks[i] = (byte) 35;
                                break;
                            case 52:
                                // Converts Infinite Water to Water Source
                                blocks[i] = (byte) 9;
                                break;
                            case 53:
                                // Converts Infinite Lava to Lava Source
                                blocks[i] = (byte) 11;
                                break;
                            default:
                                blocks[i] = (startY == 128 && y == 128) ? (byte) 7 : block;
                                break;
                        }
                    }
                    i++;
                }
            }
        }
        return blocks;
    }

    private byte[] parseData(int blockX, int blockZ) {
        byte[] data = new byte[16384];
        return data;
    }

    private byte[] generateBlockLight(int chunkX, int chunkZ) {
        byte[] blockLight = new byte[16384];
        return blockLight;
    }

    private byte[] generateSkyLight(int chunkX, int chunkZ) {
        byte[] skyLight = new byte[16384];
        return skyLight;
    }

    private byte[] generateHeightMap(int blockX, int blockZ) {
        byte[] heightMap = new byte[256];
        for(int realX = blockX, x = 0; realX <= blockX + 15; realX++) {
            for(int realZ = blockZ, z = 0; realZ <= blockZ + 15; realZ++) {
                for(int y = indevLevel.height - 1; y >= 0; y--) {
                    byte block = indevLevel.blocks[(y * indevLevel.length + realZ) * indevLevel.width + realX];
                    if (block != (byte) IndevBlocks.AIR) {
                        heightMap[(16 * z) + x] = (byte) y;
                        break;
                    }
                }
                z++;
            }
            x++;
        }
        return heightMap;
    }

    // TODO: Check to see if this works
    private ListTag<CompoundTag> parseEntities(int chunkX, int chunkZ) {
        ListTag<CompoundTag> entities = new ListTag<>();
        for (CompoundTag entity : indevLevel.entities) {
            if (!entity.getStringTag("id").equals(new StringTag("LocalPlayer"))) {
                ListTag<FloatTag> entityPos = (ListTag<FloatTag>) entity.getListTag("Pos").asFloatTagList();
                ListTag<FloatTag> entityMotion = (ListTag<FloatTag>) entity.getListTag("Motion").asFloatTagList();
                if ((int) (entityPos.get(0).asFloat()) / 16f == chunkX && (int) (entityPos.get(2).asFloat()) / 16f == chunkZ) {
                    ListTag<DoubleTag> newPos = new ListTag<>();
                    newPos.add((double) entityPos.get(0).asFloat());
                    newPos.add((double) entityPos.get(1).asFloat());
                    newPos.add((double) entityPos.get(2).asFloat());
                    entity.remove("Pos");
                    entity.put("Pos", newPos);

                    ListTag<DoubleTag> newMotion = new ListTag<>();
                    newMotion.add((double) entityMotion.get(0).asFloat());
                    newMotion.add((double) entityMotion.get(1).asFloat());
                    newMotion.add((double) entityMotion.get(2).asFloat());
                    entity.remove("Motion");
                    entity.put("Motion", newMotion);

                    entities.add(entity);
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
