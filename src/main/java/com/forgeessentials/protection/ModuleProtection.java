package com.forgeessentials.protection;

import com.forgeessentials.api.APIRegistry.ForgeEssentialsRegistrar.PermRegister;
import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.data.AbstractDataDriver;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.permissions.Permission;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModulePreInitEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

@FEModule(name = "protection", parentMod = ForgeEssentials.class, isCore = true, configClass = ConfigProtection.class)
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

    @FEModule.Config
    public static ConfigProtection config;
    public static boolean enable = false;
    public static boolean enableMobSpawns = false;
    public static HashMap<String, AdditionalZoneData> itemsList = new HashMap<String, AdditionalZoneData>();
    private static AbstractDataDriver data;
    private static ClassContainer zoneBannedItems = new ClassContainer(AdditionalZoneData.class);

    @FEModule.PreInit
    public void preLoad(FEModulePreInitEvent e)
    {
        if (!FMLCommonHandler.instance().getEffectiveSide().isServer())
        {
            e.getModuleContainer().isLoadable = false;
            return;
        }
    }

    @FEModule.Init
    public void load(FEModuleInitEvent e)
    {

        if (!enable)
        {
            e.getModuleContainer().isLoadable = false;
        }

        data = DataStorageManager.getReccomendedDriver();

        MinecraftForge.EVENT_BUS.register(new EventHandler());

        Object[] objs = data.loadAllObjects(zoneBannedItems);
        for (Object obj : objs)
        {
            AdditionalZoneData bi = (AdditionalZoneData) obj;
            itemsList.put(bi.getName(), bi);
        }
    }

    @SuppressWarnings("unchecked")
    @PermRegister
    public void registerPermissions(IPermRegisterEvent event)
    {
        event.registerPermissionLevel(PERM_PVP, RegGroup.GUESTS);
        event.registerPermissionLevel(PERM_EDITS, RegGroup.MEMBERS);
        event.registerPermissionLevel(PERM_INTERACT_BLOCK, RegGroup.MEMBERS);
        event.registerPermissionLevel(PERM_INTERACT_ENTITY, RegGroup.MEMBERS);
        event.registerPermissionLevel(PERM_OVERRIDE, RegGroup.OWNERS);
        event.registerPermissionLevel(PERM_OVERRIDE_BANNEDITEMS, RegGroup.OWNERS);

        for (Entry<String, Class<?>> e : (Set<Entry<String, Class<?>>>) EntityList.stringToClassMapping.entrySet())
        {
            if (EntityLiving.class.isAssignableFrom(e.getValue()))
            {
                event.registerPermission(PERM_MOB_SPAWN_NATURAL + "." + e.getKey());
                event.registerPermission(PERM_MOB_SPAWN_FORCED + "." + e.getKey());
            }
        }
        event.registerPermissionLevel(PERM_MOB_SPAWN_NATURAL + "." + Permission.ALL, RegGroup.ZONE);
        event.registerPermissionLevel(PERM_MOB_SPAWN_FORCED + "." + Permission.ALL, RegGroup.ZONE);

        for (Item item : GameData.getItemRegistry().typeSafeIterable())
        {
            event.registerPermissionLevel(PERM_ITEM_USE + "." + item.getUnlocalizedName(), RegGroup.MEMBERS);
        }

        for (Block item : GameData.getBlockRegistry().typeSafeIterable())
        {
            event.registerPermissionLevel(PERM_ITEM_USE + "." + item.getUnlocalizedName(), RegGroup.MEMBERS);
        }

        event.registerPermissionLevel(PERM_ITEM_USE + "." + Permission.ALL, RegGroup.MEMBERS);

        for (int i : DimensionManager.getIDs())
        {
            event.registerPermissionLevel(PERM_DIMENSION + i, RegGroup.MEMBERS);
        }
    }
}
