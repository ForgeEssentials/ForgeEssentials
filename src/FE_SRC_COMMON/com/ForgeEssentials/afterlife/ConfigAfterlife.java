package com.ForgeEssentials.afterlife;

import java.io.File;
import java.util.ArrayList;

import net.minecraft.command.ICommandSender;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StringTranslate;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.api.modules.ModuleConfigBase;
import com.ForgeEssentials.util.OutputHandler;

public class ConfigAfterlife extends ModuleConfigBase
{
	public Configuration	config;
	String					cat		= "Afterlife";
	DamageSource[]			dslist	= new DamageSource[] { DamageSource.anvil, DamageSource.cactus, DamageSource.drown, DamageSource.explosion, DamageSource.explosion2, DamageSource.fall, DamageSource.fallingBlock, DamageSource.generic, DamageSource.inFire, DamageSource.inWall, DamageSource.lava, DamageSource.magic, DamageSource.outOfWorld, DamageSource.starve, DamageSource.wither };

	public ConfigAfterlife(File file)
	{
		super(file);
	}

	@Override
	public void init()
	{
		OutputHandler.finer("Loading Afterlife Config");
		config = new Configuration(file, true);

		String subcat = cat + ".DeathChest";
		config.addCustomCategoryComment(subcat, "Permission needed:\n" + Deathchest.PERMISSION);

		Deathchest.enable = config.get(subcat, "Enable", true, "Enable the deathchest. Still need permission: ").getBoolean(true);
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
		String[] array = config.get(subcat, "potionEffects", new String[] { "4:150:1" }, "Format like this: 'ID:duration:amplifier'").valueList;

		for (String string : array)
		{
			String[] split = string.split(":");
			RespawnDebuff.potionEffects.add(new PotionEffect(Integer.parseInt(split[0]), Integer.parseInt(split[1]) * 20, Integer.parseInt(split[2])));
		}

		subcat = cat + ".deathMessages";
		config.addCustomCategoryComment(subcat, "This is a list of all DamageSources for Vanilla.");

		for (DamageSource ds : dslist)
		{
			sdm(ds, config.get(subcat, ds.getDamageType(), gdm(ds)).value);
		}

		config.save();
	}

	@Override
	public void forceSave()
	{
		String subcat = cat + ".DeathChest";
		config.addCustomCategoryComment(subcat, "Permission needed:\n" + Deathchest.PERMISSION);

		config.get(subcat, "Enable", true, "Enable the deathchest. Still need permission: \"" + Deathchest.PERMISSION + "\"").value = Deathchest.enable + "";
		config.get(subcat, "EnableXP", true, "Gives xp when the skull is destoyed or emplyed").value = Deathchest.enableXP + "";
		config.get(subcat, "enableFencePost", true, "Put the skull on a spike.").value = Deathchest.enableFencePost + "";

		subcat = cat + ".respawnStats";
		config.addCustomCategoryComment(subcat, "Bypass permission:\n" + RespawnDebuff.BYPASSSTATS);

		config.get(subcat, "hp", 20, "On respawn, respawn with X half hearts.").value = RespawnDebuff.hp + "";
		config.get(subcat, "foodlvl", 20, "On respawn, respawn with X half whatevertheyare.").value = RespawnDebuff.food + "";

		subcat = cat + ".respawnDebuff";
		config.addCustomCategoryComment(subcat, "Bypass permission:\n" + RespawnDebuff.BYPASSPOTION + "\nFor more info on potions effects:\nhttp://www.minecraftwiki.net/wiki/Potion_effects");

		ArrayList<String> list = new ArrayList<String>();
		for (PotionEffect effect : RespawnDebuff.potionEffects)
		{
			list.add(effect.getPotionID() + ":" + effect.getDuration() * 20 + ":" + effect.getAmplifier());
		}
		config.get(subcat, "potionEffects", new String[] { "4:150:1" }, "Format like this: 'ID:duration:amplifier'").valueList = list.toArray(new String[list.size()]);

		subcat = cat + ".deathMessages";
		config.addCustomCategoryComment(subcat, "This is a list of all DamageSources for Vanilla.");

		for (DamageSource ds : dslist)
		{
			config.get(subcat, ds.getDamageType(), gdm(ds)).value = gdm(ds);
		}
		
		config.save();
	}

	@Override
	public void forceLoad(ICommandSender sender)
	{
		config.load();

		String subcat = cat + ".DeathChest";
		config.addCustomCategoryComment(subcat, "Permission needed:\n" + Deathchest.PERMISSION);

		Deathchest.enable = config.get(subcat, "Enable", true, "Enable the deathchest. Still need permission: \"" + Deathchest.PERMISSION + "\"").getBoolean(true);
		Deathchest.enableXP = config.get(subcat, "EnableXP", true, "Gives xp when the skull is destoyed or emplyed").getBoolean(true);
		Deathchest.enableFencePost = config.get(subcat, "enableFencePost", true, "Put the skull on a spike.").getBoolean(true);

		subcat = cat + ".respawnStats";
		config.addCustomCategoryComment(subcat, "Bypass permission:\n" + RespawnDebuff.BYPASSSTATS);

		RespawnDebuff.hp = config.get(subcat, "hp", 20, "On respawn, respawn with X half hearts.").getInt();
		RespawnDebuff.food = config.get(subcat, "foodlvl", 20, "On respawn, respawn with X half whatevertheyare.").getInt();

		subcat = cat + ".respawnDebuff";
		config.addCustomCategoryComment(subcat, "Bypass permission:\n" + RespawnDebuff.BYPASSPOTION + "\nFor more info on potions effects:\nhttp://www.minecraftwiki.net/wiki/Potion_effects");

		RespawnDebuff.potionEffects = new ArrayList<PotionEffect>();
		String[] array = config.get(subcat, "potionEffects", new String[] { "4:150:1" }, "Format like this: 'ID:duration:amplifier'").valueList;

		for (String string : array)
		{
			String[] split = string.split(":");
			RespawnDebuff.potionEffects.add(new PotionEffect(Integer.parseInt(split[0]), Integer.parseInt(split[1]) * 20, Integer.parseInt(split[2])));
		}

		subcat = cat + ".deathMessages";
		config.addCustomCategoryComment(subcat, "This is a list of all DamageSources for Vanilla.");

		for (DamageSource ds : dslist)
		{
			sdm(ds, config.get(subcat, ds.getDamageType(), gdm(ds)).value);
		}

		config.save();
	}

	public void loadDM()
	{
		String subcat = cat + ".deathMessages";
		config.addCustomCategoryComment(subcat, "This is a list of all DamageSources for Vanilla.");
		for (DamageSource ds : dslist)
		{
			config.get(subcat, ds.getDamageType(), gdm(ds)).value = gdm(ds);
		}
		config.save();
	}

	public void sdm(DamageSource ds, String msg)
	{
		StringTranslate.getInstance().translateTable.setProperty("death." + ds.getDamageType(), msg);
	}

	public String gdm(DamageSource ds)
	{
		return StringTranslate.getInstance().translateTable.getProperty("death." + ds.getDamageType());
	}
}
