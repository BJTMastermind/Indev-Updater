package me.bjtmastermind.i2ic.indev;

import java.io.File;
import java.io.IOException;

import me.bjtmastermind.nbt.io.NBTUtil;
import me.bjtmastermind.nbt.io.NamedTag;
import me.bjtmastermind.nbt.tag.CompoundTag;
import me.bjtmastermind.nbt.tag.ListTag;
import me.bjtmastermind.nbt.tag.ShortTag;

public class IndevLevel {
    // The indev file the information is from.
    public File file;
    // Root - About
    public long createdOn;
    public String name;
    public String author;
    // Root - Environment
    public short timeOfDay;
    public byte skyBrightness;
    public int skyColor;
    public int fogColor;
    public int cloudColor;
    public short cloudHeight;
    public byte surroundingGroundType;
    public short surroundingGroundHeight;
    public byte surroundingWaterType;
    public short surroundingWaterHeight;
    // Root - Map
    public short width;
    public short length;
    public short height;
    public short[] spawn = new short[3];
    public byte[] blocks;
    public byte[] data;
    // Root
    public ListTag<CompoundTag> entities = new ListTag<>();
    public ListTag<CompoundTag> tileEntities = new ListTag<>();

    public IndevLevel(File mclevelFile) throws IOException {
        this.file = mclevelFile;

        NamedTag ml = NBTUtil.read(mclevelFile.getPath());
        CompoundTag mclevel = (CompoundTag) ml.getTag();

        CompoundTag about = mclevel.getCompoundTag("About");
        this.createdOn = about.getLong("CreatedOn");
        this.name = about.getString("Name");
        this.author = about.getString("Author");

        CompoundTag environment = mclevel.getCompoundTag("Environment");
        this.timeOfDay = environment.getShort("TimeOfDay");
        this.skyBrightness = environment.getByte("SkyBrightness");
        this.skyColor = environment.getInt("SkyColor");
        this.fogColor = environment.getInt("FogColor");
        this.cloudColor = environment.getInt("CloudColor");
        this.cloudHeight = environment.getShort("CloudHeight");
        this.surroundingGroundType = environment.getByte("SurroundingGroundType");
        this.surroundingGroundHeight = environment.getShort("SurroundingGroundHeight");
        this.surroundingWaterType = environment.getByte("SurroundingWaterType");
        this.surroundingWaterHeight = environment.getShort("SurroundingWaterHeight");

        CompoundTag map = mclevel.getCompoundTag("Map");
        this.width = map.getShort("Width");
        this.length = map.getShort("Length");
        this.height = map.getShort("Height");
        ListTag<ShortTag> spawn = (ListTag<ShortTag>) map.getListTag("Spawn").asShortTagList();
        this.spawn[0] = spawn.get(0).valueToShort();
        this.spawn[1] = spawn.get(1).valueToShort();
        this.spawn[2] = spawn.get(2).valueToShort();
        this.blocks = map.getByteArrayTag("Blocks").getValue();
        this.data = map.getByteArrayTag("Data").getValue();
        this.entities = (ListTag<CompoundTag>) mclevel.getListTag("Entities").asCompoundTagList();
        this.tileEntities = (ListTag<CompoundTag>) mclevel.getListTag("TileEntities").asCompoundTagList();
    }
}
