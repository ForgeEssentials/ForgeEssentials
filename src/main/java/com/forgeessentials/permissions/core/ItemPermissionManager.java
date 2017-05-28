package com.forgeessentials.permissions.core;

import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.util.Constants.NBT;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.GroupEntry;
import com.forgeessentials.api.permissions.PermissionCheckEvent;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPreInitEvent;
import com.forgeessentials.util.events.ServerEventHandler;

public class ItemPermissionManager extends ServerEventHandler implements ConfigLoader
{

    public static final String HELP = "Enable the item permission manager";

    /* ------------------------------------------------------------ */

    public static final String TAG_BASE = "fepermitem";
    public static final String TAG_MODE = "mode";
    public static final String TAG_SETTINGS = "settings";

    /* ------------------------------------------------------------ */

    public static final byte MODE_DISABLED = 0;
    public static final byte MODE_INVENTORY = 1;
    public static final byte MODE_EQUIP = 2;
    public static final byte MODE_USE = 3;

    /* ------------------------------------------------------------ */

    protected static boolean enabled;

    /* ------------------------------------------------------------ */

    public ItemPermissionManager()
    {
        ForgeEssentials.getConfigManager().registerLoader(ForgeEssentials.getConfigManager().getMainConfigName(), this);
    }

    @Override
    @SubscribeEvent
    public void serverAboutToStart(FEModuleServerPreInitEvent event)
    {
        if (enabled)
            super.serverAboutToStart(event);
    }

    /* ------------------------------------------------------------ */

    @SubscribeEvent
    public void permissionCheckEvent(PermissionCheckEvent event)
    {
        if (!enabled || event.ident == null || !event.ident.hasPlayer())
            return;
        InventoryPlayer inventory = event.ident.getPlayer().inventory;
        for (int slotIdx = 0; slotIdx < inventory.getSizeInventory(); slotIdx++)
        {
            ItemStack stack = inventory.getStackInSlot(slotIdx);
            if (stack == null)
                continue;
            boolean isEquipped = slotIdx == inventory.currentItem || slotIdx > inventory.mainInventory.length;
            check(event, stack, isEquipped);
        }
    }

    public static List<String> getPlayerGroups(UserIdent ident)
    {
        List<String> groups = GroupEntry.toList(APIRegistry.perms.getPlayerGroups(ident));
        if (!enabled || ident == null || !ident.hasPlayer())
            return groups;
        InventoryPlayer inventory = ident.getPlayer().inventory;
        for (int slotIdx = 0; slotIdx < inventory.getSizeInventory(); slotIdx++)
        {
            ItemStack stack = inventory.getStackInSlot(slotIdx);
            if (stack == null)
                continue;
            boolean isEquipped = slotIdx == inventory.currentItem || slotIdx > inventory.mainInventory.length;

            NBTTagCompound tag = getPermissionTag(stack);
            if (tag == null)
                continue;

            // Check mode
            int mode = tag.getByte(TAG_MODE);
            switch (mode)
            {
            case MODE_INVENTORY:
                break;
            case MODE_EQUIP:
                if (!isEquipped)
                    continue;
                break;
            case MODE_DISABLED:
            case MODE_USE:
            default:
                break;
            }

            // Check permissions
            NBTTagList settings = getSettingsTag(tag);
            for (int i = 0; i < settings.tagCount(); i++)
            {
                String setting = settings.getStringTagAt(i);
                String[] parts = setting.split("=", 2);
                if (parts.length == 1)
                    groups.add(1, parts[0]);
            }
        }
        return groups;
    }

    public static NBTTagCompound getPermissionTag(ItemStack stack)
    {
        NBTTagCompound stackTag = stack.getTagCompound();
        if (stackTag != null)
        {
            NBTBase baseTag = stackTag.getTag(TAG_BASE);
            if (baseTag instanceof NBTTagCompound)
                return (NBTTagCompound) baseTag;
        }
        return null;
    }

    public static NBTTagList getSettingsTag(NBTTagCompound tag)
    {
        NBTTagList settings = tag.getTagList(TAG_SETTINGS, NBT.TAG_STRING);
        return settings;
    }

    public static void check(PermissionCheckEvent event, ItemStack stack, boolean isEquipped)
    {
        NBTTagCompound tag = getPermissionTag(stack);
        if (tag == null)
            return;

        // Check mode
        int mode = tag.getByte(TAG_MODE);
        switch (mode)
        {
        case MODE_INVENTORY:
            break;
        case MODE_EQUIP:
            if (!isEquipped)
                return;
            break;
        case MODE_DISABLED:
        case MODE_USE:
        default:
            return;
        }

        // Check permissions
        NBTTagList settings = getSettingsTag(tag);
        for (int i = 0; i < settings.tagCount(); i++)
        {
            String setting = settings.getStringTagAt(i);
            String[] parts = setting.split("=", 2);
            if (parts.length == 2)
            {
                for (String node : event.nodes)
                {
                    if (node.equals(parts[0]))
                    {
                        event.result = parts[1];
                        return;
                    }
                }
            }
            else
            {
                event.groups.add(1, parts[0]);
            }
        }
    }

    /* ------------------------------------------------------------ */

    @Override
    public void load(Configuration config, boolean isReload)
    {
        enabled = config.get("ItemPermissions", "enabled", false, HELP).getBoolean();
        if (ServerUtil.isServerRunning())
        {
            if (enabled)
                register();
            else
                unregister();
        }
    }

    @Override
    public boolean supportsCanonicalConfig()
    {
        return true;
    }

}
