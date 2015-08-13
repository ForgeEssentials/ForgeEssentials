package com.forgeessentials.afterlife;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.CommandFeSettings;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Module to handle death-chest and respawn debuffs.
 */

@FEModule(name = "Afterlife", parentMod = ForgeEssentials.class)
public class ModuleAfterlife
{

    @FEModule.Instance
    public static ModuleAfterlife instance;

    public static final String PERM = "fe.afterlife";
    public static final String PERM_DEBUFFS = PERM + ".debuffs";
    public static final String PERM_HP = PERM + ".hp";
    public static final String PERM_FOOD = PERM + ".food";

    public static final String PERM_DEATHCHEST = PERM + ".deathchest";
    public static final String PERM_DEATHCHEST_XP = PERM_DEATHCHEST + ".xp";
    public static final String PERM_DEATHCHEST_BLOCK = PERM_DEATHCHEST + ".block";
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
        APIRegistry.perms.registerPermissionProperty(PERM_DEATHCHEST_BLOCK, "", "If set, use this block ID for graves");
        APIRegistry.perms.registerPermissionProperty(PERM_DEATHCHEST_XP, "0.25",
                "Ratio of XP that you want to allow someone to keep in a grave. 1 keeps all XP, 0 disables XP recovery.");
        APIRegistry.perms.registerPermissionProperty(PERM_DEATHCHEST_SAFETIME, "300",
                "Time in seconds a grave is protected. After this time anyone can take all stuff");

        CommandFeSettings.addAlias("respawn_hp", PERM_HP);
        CommandFeSettings.addAlias("respawn_food", PERM_FOOD);
        CommandFeSettings.addAlias("respawn_debuffs", PERM_DEBUFFS);
        CommandFeSettings.addAlias("grave_enable", PERM_DEATHCHEST);
        CommandFeSettings.addAlias("grave_safetime", PERM_DEATHCHEST_SAFETIME);
        CommandFeSettings.addAlias("grave_recoverable_xp", PERM_DEATHCHEST_XP);
        CommandFeSettings.addAlias("grave_fence", PERM_DEATHCHEST_FENCE);
        CommandFeSettings.addAlias("grave_block", PERM_DEATHCHEST_BLOCK);
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent e)
    {
        Grave.saveAll();
    }

}
