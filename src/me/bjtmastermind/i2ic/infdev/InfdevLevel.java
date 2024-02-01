package me.bjtmastermind.i2ic.infdev;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import me.bjtmastermind.i2ic.indev.IndevLevel;
import me.bjtmastermind.nbt.io.NBTUtil;
import me.bjtmastermind.nbt.tag.CompoundTag;
import me.bjtmastermind.nbt.tag.DoubleTag;
import me.bjtmastermind.nbt.tag.FloatTag;
import me.bjtmastermind.nbt.tag.ListTag;
import me.bjtmastermind.nbt.tag.StringTag;

class InfdevLevel {
    IndevLevel indevLevel;
    // Infdev level.dat
    long lastPlayed;
    long sizeOnDisk;
    long randomSeed;
    int spawnX;
    int spawnY;
    int spawnZ;
    long time;
    CompoundTag player;

    public InfdevLevel(IndevLevel indevLevel) {
        this.indevLevel = indevLevel;
    }

    public InfdevLevel createLevel() {
        Random random = new Random();

        this.lastPlayed = indevLevel.createdOn;
        this.sizeOnDisk = 0L;
        this.randomSeed = random.nextLong();
        this.spawnX = (int) indevLevel.spawn[0];
        this.spawnY = (int) indevLevel.spawn[1];
        this.spawnZ = (int) indevLevel.spawn[2];
        this.time = (long) indevLevel.timeOfDay;

        for (CompoundTag entity : indevLevel.entities) {
            if (entity.getStringTag("id").equals(new StringTag("LocalPlayer"))) {
                ListTag<FloatTag> playerPos = entity.getListTag("Pos").asFloatTagList();
                ListTag<FloatTag> playerMotion = entity.getListTag("Motion").asFloatTagList();

                ListTag<DoubleTag> newPlayerPos = new ListTag<>();
                newPlayerPos.add((double) playerPos.get(0).asFloat());
                newPlayerPos.add((double) playerPos.get(1).asFloat());
                newPlayerPos.add((double) playerPos.get(2).asFloat());
                entity.remove("Pos");
                entity.put("Pos", newPlayerPos);

                ListTag<DoubleTag> newPlayerMotion = new ListTag<>();
                newPlayerMotion.add((double) playerMotion.get(0).asFloat());
                newPlayerMotion.add((double) playerMotion.get(1).asFloat());
                newPlayerMotion.add((double) playerMotion.get(2).asFloat());
                entity.remove("Motion");
                entity.put("Motion", newPlayerMotion);

                entity.remove("id");

                this.player = entity;
                break;
            }
        }
        return this;
    }

    public void writeToFile(String worldRoot) throws IOException {
        CompoundTag root = new CompoundTag();
        CompoundTag data = new CompoundTag();
        data.put("LastPlayed", this.lastPlayed);
        data.put("SizeOnDisk", this.sizeOnDisk);
        data.put("RandomSeed", this.randomSeed);
        data.put("SpawnX", this.spawnX);
        data.put("SpawnY", this.spawnY);
        data.put("SpawnZ", this.spawnZ);
        data.put("Time", this.time);
        data.put("Player", this.player);
        root.put("Data", data);

        NBTUtil.write(root, worldRoot+File.separator+"level.dat");
    }
}
