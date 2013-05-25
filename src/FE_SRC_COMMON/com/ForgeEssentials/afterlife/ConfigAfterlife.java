package com.ForgeEssentials.afterlife;

import java.io.File;
import java.util.ArrayList;

import net.minecraft.command.ICommandSender;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.moduleLauncher.ModuleConfigBase;
import com.ForgeEssentials.util.OutputHandler;

public class ConfigAfterlife extends ModuleConfigBase
{
	public Configuration	config;
	String					cat		= "Afterlife";

	public ConfigAfterlife(File file)
	{
		super(file);
	}

	@Override
	public void init()
	{
		OutputHandler.felog.finer("Loading Afterlife Config");
		config = new Configuration(file, true);

		String subcat = cat + ".DeathChest";
		config.addCustomCategoryComment(subcat, "Permission needed:\n" + Deathchest.PERMISSION_MAKE);

		Deathchest.enable = config.get(subcat, "Enable", true, "Enable the deathchest.").getBoolean(true);
		Deathchest.enableXP = config.get(subcat, "EnableXP", true, "Gives xp when the skull is destoyed or emplyed").getBoolean(true);
		Deathchest.enableFencePost = config.get(subcat, "enableFencePost", true, "Put the skull on a spike.").getBoolean(true);
		Deathchest.protectionTime = config.get(subcat, "protectionTime", 300, "Time in seconds a grave is protected. After this time anyone can take all stuff.").getInt();

		subcat = cat + ".respawnStats";
		config.addCustomCategoryComment(subcat, "Bypass permission:\n" + RespawnDebuff.BYPASSSTATS);

		RespawnDebuff.hp = config.get(subcat, "hp", 20, "On respawn, respawn with X half hearts.").getInt();
		RespawnDebuff.food = config.get(subcat, "foodlvl", 20, "On respawn, respawn with X half whatevertheyare.").getInt();

		subcat = cat + ".respawnDebuff";
		config.addCustomCategoryComment(subcat, "Bypass permission:\n" + RespawnDebuff.BYPASSPOTION + "\nFor more info on potions effects:\nhttp://www.minecraftwiki.net/wiki/Potion_effects");

		RespawnDebuff.potionEffects = new ArrayList<PotionEffect>();
		String[] array = config.get(subcat, "potionEffects", new String[] { "4:150:1" }, "Format like this: 'ID:duration:amplifier'").getStringList();

		for (String string : array)
		{
			String[] split = string.split(":");
			RespawnDebuff.potionEffects.add(new PotionEffect(Integer.parseInt(split[0]), Integer.parseInt(split[1]) * 20, Integer.parseInt(split[2])));
		}

		config.save();
	}

	@Override
	public void forceSave()
	{
		String subcat = cat + ".DeathChest";
		config.addCustomCategoryComment(subcat, "Permission needed:\n" + Deathchest.PERMISSION_MAKE);

		config.get(subcat, "Enable", true, "Enable the deathchest.").set(Deathchest.enable);
		config.get(subcat, "EnableXP", true, "Gives xp when the skull is destoyed or emplyed").set(Deathchest.enableXP);
		config.get(subcat, "enableFencePost", true, "Put the skull on a spike.").set(Deathchest.enableFencePost);
		
		subcat = cat + ".respawnStats";
		config.addCustomCategoryComment(subcat, "Bypass permission:\n" + RespawnDebuff.BYPASSSTATS);

		config.get(subcat, "hp", 20, "On respawn, respawn with X half hearts.").set(RespawnDebuff.hp);
		config.get(subcat, "foodlvl", 20, "On respawn, respawn with X half whatevertheyare.").set(RespawnDebuff.food);

		subcat = cat + ".respawnDebuff";
		config.addCustomCategoryComment(subcat, "Bypass permission:\n" + RespawnDebuff.BYPASSPOTION + "\nFor more info on potions effects:\nhttp://www.minecraftwiki.net/wiki/Potion_effects");

		ArrayList<String> list = new ArrayList<String>();
		for (PotionEffect effect : RespawnDebuff.potionEffects)
		{
			list.add(effect.getPotionID() + ":" + effect.getDuration() * 20 + ":" + effect.getAmplifier());
		}
		config.get(subcat, "potionEffects", new String[] { "4:150:1" }, "Format like this: 'ID:duration:amplifier'").set(list.toArray(new String[list.size()]));

		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config.load();

		String subcat = cat + ".DeathChest";
		config.addCustomCategoryComment(subcat, "Permission needed:\n" + Deathchest.PERMISSION_MAKE);

		Deathchest.enable = config.get(subcat, "Enable", true, "Enable the deathchest.").getBoolean(true);
		Deathchest.enableXP = config.get(subcat, "EnableXP", true, "Gives xp when the skull is destoyed or emplyed").getBoolean(true);
		Deathchest.enableFencePost = config.get(subcat, "enableFencePost", true, "Put the skull on a spike.").getBoolean(true);

		subcat = cat + ".respawnStats";
		config.addCustomCategoryComment(subcat, "Bypass permission:\n" + RespawnDebuff.BYPASSSTATS);

		RespawnDebuff.hp = config.get(subcat, "hp", 20, "On respawn, respawn with X half hearts.").getInt();
		RespawnDebuff.food = config.get(subcat, "foodlvl", 20, "On respawn, respawn with X half whatevertheyare.").getInt();

		subcat = cat + ".respawnDebuff";
		config.addCustomCategoryComment(subcat, "Bypass permission:\n" + RespawnDebuff.BYPASSPOTION + "\nFor more info on potions effects:\nhttp://www.minecraftwiki.net/wiki/Potion_effects");

		RespawnDebuff.potionEffects = new ArrayList<PotionEffect>();
		String[] array = config.get(subcat, "potionEffects", new String[] { "4:150:1" }, "Format like this: 'ID:duration:amplifier'").getStringList();

		for (String string : array)
		{
			String[] split = string.split(":");
			RespawnDebuff.potionEffects.add(new PotionEffect(Integer.parseInt(split[0]), Integer.parseInt(split[1]) * 20, Integer.parseInt(split[2])));
		}
		
		config.save();
	}
}
