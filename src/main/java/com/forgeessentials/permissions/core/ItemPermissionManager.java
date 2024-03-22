package com.forgeessentials.permissions.core;

import java.util.List;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.GroupEntry;
import com.forgeessentials.api.permissions.PermissionCheckEvent;
import com.forgeessentials.permissions.ModulePermissions;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerAboutToStartEvent;
import com.forgeessentials.util.events.ServerEventHandler;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ItemPermissionManager extends ServerEventHandler
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

    protected static boolean enabled = false;

    /* ------------------------------------------------------------ */

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
        Inventory inventory = event.ident.getPlayer().getInventory();
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
        Inventory inventory = ident.getPlayer().getInventory();
        for (int slotIdx = 0; slotIdx < inventory.getContainerSize(); slotIdx++)
        {
            ItemStack stack = inventory.getItem(slotIdx);
            if (stack == ItemStack.EMPTY)
                continue;
            boolean isEquipped = slotIdx == inventory.selected || slotIdx > inventory.getContainerSize();

            CompoundTag tag = getPermissionTag(stack);
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
            ListTag settings = getSettingsTag(tag);
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

    public static CompoundTag getPermissionTag(ItemStack stack)
    {
        CompoundTag stackTag = stack.getTag();
        if (stackTag != null)
        {
            Tag baseTag = stackTag.get(TAG_BASE);
            if (baseTag instanceof CompoundTag)
                return (CompoundTag) baseTag;
        }
        return null;
    }

    public static ListTag getSettingsTag(CompoundTag tag)
    {
        return tag.getList(TAG_SETTINGS, EndTag.TAG_STRING);
    }

    public static void check(PermissionCheckEvent event, ItemStack stack, boolean isEquipped)
    {
        CompoundTag tag = getPermissionTag(stack);
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
        ListTag settings = getSettingsTag(tag);
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

    static ForgeConfigSpec.BooleanValue FEenabled;

    public static void load(Builder BUILDER, boolean isReload)
    {
        BUILDER.push("ItemPermissions");
        FEenabled = BUILDER.comment(HELP).define("enabled", false);
        BUILDER.pop();
    }

    public static void bakeConfig(boolean reload)
    {
        enabled = FEenabled.get();

        if (ServerUtil.isServerRunning())
        {
            if (enabled)
                ModulePermissions.getItemPermissionManager().register();
            else
                ModulePermissions.getItemPermissionManager().unregister();
        }
    }

	public static boolean isEnabled() {
		return enabled;
	}

}
