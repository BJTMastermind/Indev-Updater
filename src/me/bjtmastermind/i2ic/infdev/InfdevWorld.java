package me.bjtmastermind.i2ic.infdev;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import me.bjtmastermind.i2ic.indev.IndevLevel;
import me.bjtmastermind.i2ic.utils.CoordinateManipulation;
import me.bjtmastermind.i2ic.utils.MessageToUser;
import me.bjtmastermind.nbt.io.NBTUtil;
import me.bjtmastermind.nbt.io.NamedTag;
import me.bjtmastermind.nbt.tag.CompoundTag;
import me.bjtmastermind.nbt.tag.LongTag;

public class InfdevWorld {
    IndevLevel indevLevel;
    // Infdev world info and chunks
    InfdevLevel level;
    HashMap<String, InfdevChunk> chunks;

    public InfdevWorld(IndevLevel indevLevel) {
        this.indevLevel = indevLevel;
        this.level = new InfdevLevel(indevLevel);
        this.chunks = new HashMap<>();
    }

    public void generateLevelFile() throws IOException {
        MessageToUser.sendMessage("Creating level.dat . . .");
        this.level = this.level.createLevel();
        this.level.writeToFile(indevLevel.file.getParent()+File.separator+"World5");
        MessageToUser.sendMessage("level.dat Created!");
    }

    public void generateChunkFiles() throws IOException {
        MessageToUser.sendMessage("Converting Chunks . . .");
        for (int x = 0; x < indevLevel.width; x += 16) {
            for (int z = 0; z < indevLevel.length; z += 16) {
                InfdevChunk chunk = new InfdevChunk(indevLevel);
                this.chunks.put(CoordinateManipulation.toChunkCoord(x)+","+CoordinateManipulation.toChunkCoord(z), chunk.createChunk(x, z));
                chunk.writeToFile(indevLevel.file.getParent()+File.separator+"World5");
            }
        }
        MessageToUser.sendMessage("Chunks Converted!");
    }

    public void recalculateLighting() {
        MessageToUser.sendMessage("Calculating Lighting . . .");
        this.recalculateSkyLight();
        this.recalculateBlockLight();
        MessageToUser.sendMessage("Lighting Calculated Successfully!");
    }

    public void calculateWorldSize(File worldRoot) throws IOException {
        long size = Files.walk(worldRoot.toPath())
            .filter(p -> p.toFile().isFile())
            .mapToLong(p -> p.toFile().length())
            .sum();

        NamedTag nt = NBTUtil.read(worldRoot+File.separator+"level.dat");
        CompoundTag level = (CompoundTag) nt.getTag();
        CompoundTag data = level.getCompoundTag("Data");

        LongTag sizeOnDisk = data.getLongTag("SizeOnDisk");
        sizeOnDisk.setValue(size);

        NBTUtil.write(level, worldRoot+File.separator+"level.dat");
    }

    private void recalculateBlockLight() {

    }

    private void recalculateSkyLight() {

    }
}
