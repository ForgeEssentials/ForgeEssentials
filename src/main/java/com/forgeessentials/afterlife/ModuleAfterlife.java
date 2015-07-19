package com.forgeessentials.afterlife;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.CommandFeSettings;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader.ConfigLoaderBase;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Module to handle death-chest and respawn debuffs.
 */

@FEModule(name = "Afterlife", parentMod = ForgeEssentials.class)
public class ModuleAfterlife extends ConfigLoaderBase
{

    @FEModule.Instance
    public static ModuleAfterlife instance;

    public static final String PERM = "fe.afterlife";
    public static final String PERM_DEBUFFS = PERM + ".debuffs";
    public static final String PERM_HP = PERM + ".hp";
    public static final String PERM_FOOD = PERM + ".food";

    public static final String PERM_DEATHCHEST = PERM + ".deathchest";
    public static final String PERM_DEATHCHEST_XP = PERM_DEATHCHEST + ".xp";
    public static final String PERM_DEATHCHEST_FENCE = PERM_DEATHCHEST + ".fence";
    public static final String PERM_DEATHCHEST_SAFETIME = PERM_DEATHCHEST + ".safetime";
    public static final String PERM_DEATHCHEST_BYPASS = PERM_DEATHCHEST + ".bypass";

    public AfterlifeEventHandler deathchest;

    public RespawnDebuffHandler respawnDebuff;

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        deathchest = new AfterlifeEventHandler();
        respawnDebuff = new RespawnDebuffHandler();
        TileEntity.addMapping(FEskullTe.class, "FESkull");
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        Grave.loadAll();

        APIRegistry.perms.registerPermissionDescription(PERM, "Permissions for afterlife configuration");
        APIRegistry.perms.registerPermissionDescription(PERM_DEBUFFS, "Potion effects to apply on respawn (comma separated list of id:duration:amplifier)");
        APIRegistry.perms.registerPermissionDescription(PERM_HP, "Respawn HP");
        APIRegistry.perms.registerPermissionDescription(PERM_FOOD, "Respawn food");

        APIRegistry.perms.registerPermission(PERM_DEATHCHEST, PermissionLevel.TRUE, "Allow creation of deathchests");
        APIRegistry.perms.registerPermission(PERM_DEATHCHEST_FENCE, PermissionLevel.TRUE, "Put the skull on a spike");
        APIRegistry.perms.registerPermission(PERM_DEATHCHEST_BYPASS, PermissionLevel.OP, "Bypass grave protection");
        APIRegistry.perms.registerPermissionProperty(PERM_DEATHCHEST_XP, "0.25",
                "Ratio of XP that you want to allow someone to keep in a grave. 1 keeps all XP, 0 disables XP recovery.");
        APIRegistry.perms.registerPermissionProperty(PERM_DEATHCHEST_SAFETIME, "300",
                "Time in seconds a grave is protected. After this time anyone can take all stuff");

        CommandFeSettings.addAlias("respawn_hp", PERM_HP);
        CommandFeSettings.addAlias("respawn_food", PERM_FOOD);
        CommandFeSettings.addAlias("grave_enable", PERM_DEATHCHEST);
        CommandFeSettings.addAlias("grave_safetime", PERM_DEATHCHEST_SAFETIME);
        CommandFeSettings.addAlias("grave_recoverable_xp", PERM_DEATHCHEST_XP);
        CommandFeSettings.addAlias("grave_fence", PERM_DEATHCHEST_FENCE);
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent e)
    {
        Grave.saveAll();
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        config.addCustomCategoryComment("Afterlife", //
                "Afterlife configuration outdated." + //
                        "\n" + //
                        "\nFor afterlife configuration, use the new permission-properties \"/p global value fe.afterlife.<perm>\"." + //
                        "\nFollowing permissions are available:" + //
                        "\n - " + PERM_DEATHCHEST + //
                        "\n - " + PERM_DEATHCHEST_XP + //
                        "\n - " + PERM_DEATHCHEST_SAFETIME + //
                        "\n - " + PERM_DEATHCHEST_FENCE + //
                        "\n - " + PERM_DEATHCHEST_BYPASS + //
                        "\n" + //
                        "\n - " + PERM_DEBUFFS + //
                        "\n - " + PERM_HP + //
                        "\n - " + PERM_FOOD + //
                        "\n" + //
                        "\n For more information look at the wiki on github." //
        );

        if (config.hasCategory("Afterlife.DeathChest"))
            config.removeCategory(config.getCategory("Afterlife.DeathChest"));

        if (config.hasCategory("Afterlife.RespawnDebuffHandler"))
            config.removeCategory(config.getCategory("Afterlife.RespawnDebuffHandler"));

        if (config.hasCategory("Afterlife.respawnStats"))
            config.removeCategory(config.getCategory("Afterlife.respawnStats"));
    }

}
