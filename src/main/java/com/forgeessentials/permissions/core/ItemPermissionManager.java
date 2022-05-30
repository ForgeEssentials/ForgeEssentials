package com.forgeessentials.permissions.core;

import java.util.List;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.GroupEntry;
import com.forgeessentials.api.permissions.PermissionCheckEvent;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerAboutToStartEvent;
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
    public void serverAboutToStart(FEModuleServerAboutToStartEvent event)
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
        PlayerInventory inventory = event.ident.getPlayer().inventory;
        for (int slotIdx = 0; slotIdx < inventory.getContainerSize(); slotIdx++)
        {
            ItemStack stack = inventory.getItem(slotIdx);
            if (stack == ItemStack.EMPTY)
                continue;
            boolean isEquipped = slotIdx == inventory.selected || slotIdx > inventory.getContainerSize();
            check(event, stack, isEquipped);
        }
    }

    public static List<String> getPlayerGroups(UserIdent ident)
    {
        List<String> groups = GroupEntry.toList(APIRegistry.perms.getPlayerGroups(ident));
        if (!enabled || ident == null || !ident.hasPlayer())
            return groups;
        PlayerInventory inventory = ident.getPlayer().inventory;
        for (int slotIdx = 0; slotIdx < inventory.getContainerSize(); slotIdx++)
        {
            ItemStack stack = inventory.getItem(slotIdx);
            if (stack == ItemStack.EMPTY)
                continue;
            boolean isEquipped = slotIdx == inventory.selected || slotIdx > inventory.getContainerSize();

            CompoundNBT tag = getPermissionTag(stack);
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
            ListNBT settings = getSettingsTag(tag);
            for (int i = 0; i < settings.size(); i++)
            {
                String setting = settings.getString(i);
                String[] parts = setting.split("=", 2);
                if (parts.length == 1)
                    groups.add(1, parts[0]);
            }
        }
        return groups;
    }

    public static CompoundNBT getPermissionTag(ItemStack stack)
    {
    	CompoundNBT stackTag = stack.getTag();
        if (stackTag != null)
        {
            INBT baseTag = stackTag.get(TAG_BASE);
            if (baseTag instanceof CompoundNBT)
                return (CompoundNBT) baseTag;
        }
        return null;
    }

    public static ListNBT getSettingsTag(CompoundNBT tag)
    {
    	ListNBT settings = tag.getList(TAG_SETTINGS, NBT.TAG_STRING);
        return settings;
    }

    public static void check(PermissionCheckEvent event, ItemStack stack, boolean isEquipped)
    {
    	CompoundNBT tag = getPermissionTag(stack);
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
        ListNBT settings = getSettingsTag(tag);
        for (int i = 0; i < settings.size(); i++)
        {
            String setting = settings.getString(i);
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
