package com.forgeessentials.afterlife;

import com.forgeessentials.core.moduleLauncher.ModuleConfigBase;
import com.forgeessentials.util.OutputHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;

public class ConfigAfterlife extends ModuleConfigBase {
    String cat = "Afterlife";

    @Override
    public void init()
    {
        OutputHandler.felog.finer("Loading Afterlife Config");

        String subcat = cat + ".DeathChest";
        config.addCustomCategoryComment(subcat, "Permission needed:\n" + Deathchest.PERMISSION_MAKE);

        Deathchest.enable = config.get(subcat, "Enable", true, "Enable the deathchest.").getBoolean(true);
        Deathchest.enableXP = config.get(subcat, "EnableXP", true, "Gives xp when the skull is destoyed or emplyed").getBoolean(true);
        Deathchest.enableFencePost = config.get(subcat, "enableFencePost", true, "Put the skull on a spike.").getBoolean(true);
        Deathchest.protectionTime = config
                .get(subcat, "protectionTime", 300, "Time in seconds a grave is protected. After this time anyone can take all stuff.").getInt();

        subcat = cat + ".respawnStats";
        config.addCustomCategoryComment(subcat, "Bypass permissions:\n" + RespawnDebuffHandler.BYPASSSTATS);

        RespawnDebuffHandler.hp = config.get(subcat, "hp", 20, "On respawn, respawn with X half hearts.").getInt();
        RespawnDebuffHandler.food = config.get(subcat, "foodlvl", 20, "On respawn, respawn with X half whatevertheyare.").getInt();

        subcat = cat + ".RespawnDebuffHandler";
        config.addCustomCategoryComment(subcat,
                "Bypass permissions:\n" + RespawnDebuffHandler.BYPASSPOTION
                        + "\nFor more info on potions effects:\nhttp://www.minecraftwiki.net/wiki/Potion_effects");

        RespawnDebuffHandler.potionEffects = new ArrayList<PotionEffect>();
        String[] array = config.get(subcat, "potionEffects", new String[] { "4:150:1" }, "Format like this: 'ID:duration:amplifier'").getStringList();

        for (String string : array)
        {
            String[] split = string.split(":");
            RespawnDebuffHandler.potionEffects.add(new PotionEffect(Integer.parseInt(split[0]), Integer.parseInt(split[1]) * 20, Integer.parseInt(split[2])));
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
        config.addCustomCategoryComment(subcat, "Bypass permissions:\n" + RespawnDebuffHandler.BYPASSSTATS);

        config.get(subcat, "hp", 20, "On respawn, respawn with X half hearts.").set(RespawnDebuffHandler.hp);
        config.get(subcat, "foodlvl", 20, "On respawn, respawn with X half whatevertheyare.").set(RespawnDebuffHandler.food);

        subcat = cat + ".RespawnDebuffHandler";
        config.addCustomCategoryComment(subcat,
                "Bypass permissions:\n" + RespawnDebuffHandler.BYPASSPOTION
                        + "\nFor more info on potions effects:\nhttp://www.minecraftwiki.net/wiki/Potion_effects");

        ArrayList<String> list = new ArrayList<String>();
        for (PotionEffect effect : RespawnDebuffHandler.potionEffects)
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
        config.addCustomCategoryComment(subcat, "Bypass permissions:\n" + RespawnDebuffHandler.BYPASSSTATS);

        RespawnDebuffHandler.hp = config.get(subcat, "hp", 20, "On respawn, respawn with X half hearts.").getInt();
        RespawnDebuffHandler.food = config.get(subcat, "foodlvl", 20, "On respawn, respawn with X half whatevertheyare.").getInt();

        subcat = cat + ".RespawnDebuffHandler";
        config.addCustomCategoryComment(subcat,
                "Bypass permissions:\n" + RespawnDebuffHandler.BYPASSPOTION
                        + "\nFor more info on potions effects:\nhttp://www.minecraftwiki.net/wiki/Potion_effects");

        RespawnDebuffHandler.potionEffects = new ArrayList<PotionEffect>();
        String[] array = config.get(subcat, "potionEffects", new String[] { "4:150:1" }, "Format like this: 'ID:duration:amplifier'").getStringList();

        for (String string : array)
        {
            String[] split = string.split(":");
            RespawnDebuffHandler.potionEffects.add(new PotionEffect(Integer.parseInt(split[0]), Integer.parseInt(split[1]) * 20, Integer.parseInt(split[2])));
        }

        config.save();
    }

    public boolean universalConfigAllowed(){return true;}
}
