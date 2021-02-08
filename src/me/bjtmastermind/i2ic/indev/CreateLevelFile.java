package me.bjtmastermind.i2ic.indev;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import me.bjtmastermind.i2ic.util.Utils;
import me.bjtmastermind.nbt.io.NBTUtil;
import me.bjtmastermind.nbt.io.NamedTag;
import me.bjtmastermind.nbt.tag.CompoundTag;
import me.bjtmastermind.nbt.tag.DoubleTag;
import me.bjtmastermind.nbt.tag.FloatTag;
import me.bjtmastermind.nbt.tag.ListTag;

public class CreateLevelFile {
	@SuppressWarnings("unchecked")
	public static void createLevel(String filename, String path) throws IOException {
		Random rand = new Random();

		// Loads in Minecraft Indev .mclevel world file.
		NamedTag ml = NBTUtil.read(filename);
		CompoundTag mclevel = (CompoundTag) ml.getTag();
		
		// Creates the Minecraft Infdev level.dat file from the .mclevel information.
		CompoundTag level = new CompoundTag();
		
		// "Data" Compound Tag
		CompoundTag datatag = new CompoundTag();
		long createdOn = mclevel.getCompoundTag("About").getLong("CreatedOn");
		datatag.put("LastPlayed", createdOn);
		datatag.put("RandomSeed", rand.nextLong());
		datatag.put("SizeOnDisc", (long) new File(filename).length());
		short worldSpawnX = mclevel.getCompoundTag("Map").getListTag("Spawn").get(0).valueToShort();
		datatag.put("SpawnX", (int) worldSpawnX);
		short worldSpawnY = mclevel.getCompoundTag("Map").getListTag("Spawn").get(1).valueToShort();
		datatag.put("SpawnY", (int) worldSpawnY);
		short worldSpawnZ = mclevel.getCompoundTag("Map").getListTag("Spawn").get(2).valueToShort();
		datatag.put("SpawnZ", (int) worldSpawnZ);
		short timeOfDay = mclevel.getCompoundTag("Environment").getShort("TimeOfDay");
		datatag.put("Time", (long) timeOfDay);
		level.put("Data", datatag);
		
		// "Player" Compound Tag
		CompoundTag player = new CompoundTag();
		CompoundTag playerInfo = (CompoundTag) mclevel.getListTag("Entities").get(0);
		short playerAir = playerInfo.getShort("Air");
		player.put("Air", playerAir);
		short playerAttackTime = playerInfo.getShort("AttackTime");
		player.put("AttackTime", playerAttackTime);
		short playerDeathTime = playerInfo.getShort("DeathTime");
		player.put("DeathTime", playerDeathTime);
		float playerFalldistance = playerInfo.getFloat("Falldistance");
		player.put("Falldistance", playerFalldistance);
		short playerFire = playerInfo.getShort("Fire");
		player.put("Fire", playerFire);
		short playerHealth = playerInfo.getShort("Health");
		player.put("Health", playerHealth);
		short playerHurtTime = playerInfo.getShort("HurtTime");
		player.put("HurtTime", playerHurtTime);
		int playerScore = playerInfo.getInt("Score");
		player.put("Score", playerScore);
		
		ListTag<CompoundTag> inventory = (ListTag<CompoundTag>) playerInfo.getListTag("Inventory");		
		player.put("Inventory", inventory);
		ListTag<DoubleTag> motion = (ListTag<DoubleTag>) playerInfo.getListTag("Motion");	
		player.put("Motion", motion);
		ListTag<DoubleTag> pos = (ListTag<DoubleTag>) playerInfo.getListTag("Pos");
		player.put("Pos", pos);
		ListTag<FloatTag> rotation = (ListTag<FloatTag>) playerInfo.getListTag("Rotation");
		player.put("Rotation", rotation);
		level.put("Player", player);
		
		// Write out the converted information to the level.dat file.
		NBTUtil.write(level, path+"World5/level.dat");
	}
}
