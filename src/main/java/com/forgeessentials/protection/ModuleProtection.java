package com.forgeessentials.protection;

import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameData;

@FEModule(name = "protection", parentMod = ForgeEssentials.class, isCore = true)
public class ModuleProtection {

    public final static String PERM_EDITS = "fe.protection.allowEdits";
    public final static String PERM_ITEM_USE = "fe.protection.itemUse";
    public final static String PERM_INTERACT_BLOCK = "fe.protection.allowBlockInteractions";
    public final static String PERM_INTERACT_ENTITY = "fe.protection.allowEntityInteractions";
    public final static String PERM_OVERRIDE = "fe.protection.overrideProtection";
    public final static String PERM_PVP = "fe.protection.pvp";
    public final static String PERM_MOB_SPAWN_NATURAL = "fe.protection.mobSpawn.natural";
    public final static String PERM_MOB_SPAWN_FORCED = "fe.protection.mobSpawn.forced";
    public final static String PERM_DIMENSION = "fe.protection.dimension.";
    public final static String PERM_OVERRIDE_BANNEDITEMS = "fe.protection.overrideProtection.banneditems";
    public final static String PERMPROP_ZONE_GAMEMODE = "fe.protection.data.zonegamemode";

    @SuppressWarnings("unused")
    private ProtectionEventHandler protectionHandler;

    @SubscribeEvent
    public void preLoad(FEModulePreInitEvent e)
    {
        // To disable now - just delete module-jar / might be canged in the future
        //if (!enable)
        //    ModuleLauncher.instance.unregister("protection");
    }

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        protectionHandler = new ProtectionEventHandler();
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void registerPermissions(FEModuleServerInitEvent ev)
    {
        FunctionHelper.registerServerCommand(new ProtectCommand());

        APIRegistry.perms.registerPermission(PERM_PVP, RegisteredPermValue.TRUE);
        APIRegistry.perms.registerPermission(PERM_EDITS, RegisteredPermValue.TRUE);
        APIRegistry.perms.registerPermission(PERM_INTERACT_BLOCK, RegisteredPermValue.TRUE);
        APIRegistry.perms.registerPermission(PERM_INTERACT_ENTITY, RegisteredPermValue.TRUE);
        APIRegistry.perms.registerPermission(PERM_OVERRIDE, RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission(PERM_OVERRIDE_BANNEDITEMS, RegisteredPermValue.OP);

        for (Entry<String, Class<?>> e : (Set<Entry<String, Class<?>>>) EntityList.stringToClassMapping.entrySet())
        {
            if (EntityLiving.class.isAssignableFrom(e.getValue()))
            {
                APIRegistry.perms.registerPermission(PERM_MOB_SPAWN_NATURAL + "." + e.getKey(), RegisteredPermValue.TRUE);
                APIRegistry.perms.registerPermission(PERM_MOB_SPAWN_FORCED + "." + e.getKey(), RegisteredPermValue.TRUE);
            }
        }
        APIRegistry.perms.registerPermission(PERM_MOB_SPAWN_NATURAL + "." + IPermissionsHelper.PERMISSION_ASTERIX, RegisteredPermValue.TRUE);
        APIRegistry.perms.registerPermission(PERM_MOB_SPAWN_FORCED + "." + IPermissionsHelper.PERMISSION_ASTERIX, RegisteredPermValue.TRUE);

        for (Item item : GameData.getItemRegistry().typeSafeIterable())
        {
            APIRegistry.perms.registerPermission(PERM_ITEM_USE + "." + item.getUnlocalizedName(), RegisteredPermValue.TRUE);
        }

        APIRegistry.perms.registerPermission(PERM_ITEM_USE + "." + IPermissionsHelper.PERMISSION_ASTERIX, RegisteredPermValue.TRUE);

        for (int i : DimensionManager.getIDs())
        {
            APIRegistry.perms.registerPermission(PERM_DIMENSION + i, RegisteredPermValue.TRUE);
        }

        APIRegistry.perms.registerPermissionProperty(PERMPROP_ZONE_GAMEMODE, Integer.toString(0));
    }
    
}
