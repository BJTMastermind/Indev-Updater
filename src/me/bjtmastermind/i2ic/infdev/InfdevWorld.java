package me.bjtmastermind.i2ic.infdev;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import me.bjtmastermind.i2ic.indev.IndevLevel;
import me.bjtmastermind.i2ic.utils.CoordinateManipulation;
import me.bjtmastermind.i2ic.utils.MessageToUser;

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
        MessageToUser.sendMessage("level.dat Creating!");
    }

    public void generateChunkFiles() throws IOException {
        MessageToUser.sendMessage("Converting Chunks . . .");
        InfdevChunk chunk = new InfdevChunk(indevLevel);
        for (int x = 0; x < indevLevel.width; x += 16) {
            for (int z = 0; z < indevLevel.length; z += 16) {
                this.chunks.put(CoordinateManipulation.toChunkCoord(x)+","+CoordinateManipulation.toChunkCoord(z), chunk.createChunk(x, z));
                chunk.writeToFile(indevLevel.file.getParent()+File.separator+"World5");
            }
        }
        MessageToUser.sendMessage("Chunks Converted!");
    }
}
