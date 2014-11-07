package com.forgeessentials.afterlife;

import java.util.ArrayList;

import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.config.IConfigLoader.ConfigLoaderBase;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * This module handles Deathchest and respawn debuffs.
 *
 * @author Dries007
 */

@FEModule(name = "Afterlife", parentMod = ForgeEssentials.class)
public class ModuleAfterlife extends ConfigLoaderBase {

    @FEModule.Instance
    public static ModuleAfterlife instance;

    public static final String BASEPERM = "fe.afterlife";

    public Deathchest deathchest;

    public RespawnDebuffHandler respawnDebuff;

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        deathchest = new Deathchest();
        respawnDebuff = new RespawnDebuffHandler();
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        deathchest.load();
        APIRegistry.perms.registerPermission(BASEPERM, RegisteredPermValue.OP);

        APIRegistry.perms.registerPermission(RespawnDebuffHandler.BYPASSPOTION, RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission(RespawnDebuffHandler.BYPASSSTATS, RegisteredPermValue.OP);

        APIRegistry.perms.registerPermission(Deathchest.PERMISSION_BYPASS, null);
        APIRegistry.perms.registerPermission(Deathchest.PERMISSION_MAKE, RegisteredPermValue.TRUE, "Allows graves to spawn, if a player dies");
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent e)
    {
        deathchest.save();
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        OutputHandler.felog.finer("Loading Afterlife-module configuration");

        String category = "Afterlife.DeathChest";
        config.addCustomCategoryComment(category, "Permission needed:\n" + Deathchest.PERMISSION_MAKE);
        Deathchest.enable = config.get(category, "Enable", true, "Enable the deathchest.").getBoolean(true);
        Deathchest.enableXP = config.get(category, "EnableXP", true, "Gives xp when the skull is destoyed or emplyed").getBoolean(true);
        Deathchest.enableFencePost = config.get(category, "enableFencePost", true, "Put the skull on a spike.").getBoolean(true);
        Deathchest.protectionTime = config.get(category, "protectionTime", 300,
                "Time in seconds a grave is protected. After this time anyone can take all stuff.").getInt();

        category = "Afterlife.respawnStats";
        config.addCustomCategoryComment(category, "Bypass permissions:\n" + RespawnDebuffHandler.BYPASSSTATS);
        RespawnDebuffHandler.hp = config.get(category, "hp", 20, "On respawn, respawn with X half hearts.").getInt();
        RespawnDebuffHandler.food = config.get(category, "foodlvl", 20, "On respawn, respawn with X half whatevertheyare.").getInt();

        category = "Afterlife.RespawnDebuffHandler";
        config.addCustomCategoryComment(category, "Bypass permissions:\n" + RespawnDebuffHandler.BYPASSPOTION
                + "\nFor more info on potions effects:\nhttp://www.minecraftwiki.net/wiki/Potion_effects");

        RespawnDebuffHandler.potionEffects = new ArrayList<PotionEffect>();
        String[] array = config.get(category, "potionEffects", new String[] { "4:150:1" }, "Format like this: 'ID:duration:amplifier'").getStringList();

        for (String string : array)
        {
            String[] split = string.split(":");
            RespawnDebuffHandler.potionEffects.add(new PotionEffect(Integer.parseInt(split[0]), Integer.parseInt(split[1]) * 20, Integer.parseInt(split[2])));
        }
    }
}
