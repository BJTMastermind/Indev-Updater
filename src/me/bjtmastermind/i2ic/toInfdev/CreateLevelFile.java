package me.bjtmastermind.i2ic.toInfdev;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import me.bjtmastermind.i2ic.GUI;
import me.bjtmastermind.i2ic.Main;
import me.bjtmastermind.nbt.io.NBTUtil;
import me.bjtmastermind.nbt.io.NamedTag;
import me.bjtmastermind.nbt.tag.CompoundTag;
import me.bjtmastermind.nbt.tag.DoubleTag;
import me.bjtmastermind.nbt.tag.FloatTag;
import me.bjtmastermind.nbt.tag.ListTag;
import me.bjtmastermind.nbt.tag.StringTag;

public class CreateLevelFile {
	@SuppressWarnings("unchecked")
	public static void createLevel(String filename, String path) throws IOException {
		if(Main.userType == 1) GUI.appendConsole("Creating level.dat . . .");
		if(Main.userType == 0) System.out.println("Creating level.dat . . .");	
		
		Random rand = new Random();

		// Loads in Minecraft Indev .mclevel world file.
		NamedTag ml = NBTUtil.read(filename);
		CompoundTag mclevel = (CompoundTag) ml.getTag();
		
		// Get the height of the map. Only to determine if its a deep world
		short heightOfMap = mclevel.getCompoundTag("Map").getShort("Height");
		
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
		if (heightOfMap == 256) {
			datatag.put("SpawnY", (int) worldSpawnY - 128);
		} else {
			datatag.put("SpawnY", (int) worldSpawnY);
		}
		short worldSpawnZ = mclevel.getCompoundTag("Map").getListTag("Spawn").get(2).valueToShort();
		datatag.put("SpawnZ", (int) worldSpawnZ);
		short timeOfDay = mclevel.getCompoundTag("Environment").getShort("TimeOfDay");
		datatag.put("Time", (long) timeOfDay);
		
		// "Player" Compound Tag
		CompoundTag player = new CompoundTag();
		ListTag<CompoundTag> getPlayer = (ListTag<CompoundTag>) mclevel.getListTag("Entities");
		for(int i = 0; i < getPlayer.size(); i++) {
			if(getPlayer.get(i).getStringTag("id").equals(new StringTag("LocalPlayer"))) {
				CompoundTag playerInfo = getPlayer.get(i);
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
				
				double motX = (double) playerInfo.getListTag("Motion").get(0).valueToFloat();
				double motY = (double) playerInfo.getListTag("Motion").get(1).valueToFloat();
				double motZ = (double) playerInfo.getListTag("Motion").get(2).valueToFloat();
				playerInfo.remove("Motion");
				ListTag<DoubleTag> motion = new ListTag<DoubleTag>();
				motion.add(motX);
				motion.add(motY);
				motion.add(motZ);
				player.put("Motion", motion);
				
				double posX = (double) playerInfo.getListTag("Pos").get(0).valueToFloat();
				double posY = (double) playerInfo.getListTag("Pos").get(1).valueToFloat();
				double posZ = (double) playerInfo.getListTag("Pos").get(2).valueToFloat();
				playerInfo.remove("Motion");
				ListTag<DoubleTag> pos = new ListTag<DoubleTag>();
				pos.add(posX);
				if (heightOfMap == 256) {
					pos.add(posY - 128);
				} else {
					pos.add(posY);
				}
				pos.add(posZ);
				player.put("Pos", pos);
				
				ListTag<FloatTag> rotation = (ListTag<FloatTag>) playerInfo.getListTag("Rotation");
				player.put("Rotation", rotation);
			}
		}
		datatag.put("Player", player);
		level.put("Data", datatag);
		
		// Write out the converted information to the level.dat file.
		NBTUtil.write(level, path+"World5/level.dat");
		
		if(Main.userType == 1) GUI.appendConsole("level.dat Creating!");
		if(Main.userType == 0) System.out.println("level.dat Creating!");	
	}
	
	// Fix to give correct file size of world in MB
	/*private static long getDirSize(File dir) {
		long size = 0;
		File[] contents = dir.listFiles();
		
		for(int i = 0; i < contents.length; i++) {
			if(contents[i].isFile()) {
				System.out.println(contents[i]);
				size += contents[i].length();
			} else {
				System.out.println(contents[i]);
				getDirSize(contents[i]);
			}
		}
		System.out.println(size);
		return size / (1024 * 1024);
	}*/
}
