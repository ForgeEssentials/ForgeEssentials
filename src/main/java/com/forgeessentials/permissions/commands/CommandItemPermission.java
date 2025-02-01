package com.forgeessentials.permissions.commands;

import static com.forgeessentials.permissions.core.ItemPermissionManager.TAG_MODE;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.permissions.core.ItemPermissionManager;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.ItemUtil;

public class CommandItemPermission extends ParserCommandBase
{

    @Override
    public String getCommandName()
    {
        return "permitem";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/permitem mode|perm|group|reset: Manage permission-items";
    }

    @Override
    public String getPermissionNode()
    {
        return PermissionCommandParser.PERM + ".permitem";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        ItemStack stack = arguments.senderPlayer.getCurrentEquippedItem();
        if (stack == null)
            throw new TranslatedCommandException("No item equipped!");

        if (arguments.isEmpty())
        {
            arguments.confirm("/permitem mode inventory|equip"); // |use");
            arguments.confirm("/permitem perm <perm> <value>");
            arguments.confirm("/permitem group <group>");
            arguments.confirm("/permitem reset");
            return;
        }

        arguments.tabComplete("mode", "perm", "group", "reset");
        String subCmd = arguments.remove().toLowerCase();
        switch (subCmd)
        {
        case "mode":
            parseMode(arguments, stack);
            break;
        case "perm":
            parsePermission(arguments, stack);
            break;
        case "group":
            parseGroup(arguments, stack);
            break;
        case "reset":
            NBTTagCompound stackTag = stack.getTagCompound();
            if (stackTag != null)
                stackTag.removeTag(ItemPermissionManager.TAG_BASE);
            arguments.confirm("Deleted permission item settings");
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCmd);
        }
    }

    public static void parseMode(CommandParserArgs arguments, ItemStack stack) throws CommandException
    {
        if (arguments.isEmpty())
            throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
        arguments.tabComplete("inventory", "equip"); // , "use");
        String subCmd = arguments.remove().toLowerCase();
        if (arguments.isTabCompletion)
            return;

        switch (subCmd)
        {
        case "inventory":
            getOrCreatePermissionTag(stack).setByte(TAG_MODE, ItemPermissionManager.MODE_INVENTORY);
            break;
        case "equip":
            getOrCreatePermissionTag(stack).setByte(TAG_MODE, ItemPermissionManager.MODE_EQUIP);
            break;
        case "use":
            getOrCreatePermissionTag(stack).setByte(TAG_MODE, ItemPermissionManager.MODE_USE);
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCmd);
        }
    }

    public static void parsePermission(CommandParserArgs arguments, ItemStack stack) throws CommandException
    {
        if (arguments.isEmpty())
            throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
        String permission = arguments.parsePermission();

        if (arguments.isEmpty())
            throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);
        arguments.tabComplete(Zone.PERMISSION_TRUE, Zone.PERMISSION_FALSE);
        String value = arguments.remove();

        if (arguments.isTabCompletion)
            return;
        getSettingsTag(stack).appendTag(new NBTTagString(permission + "=" + value));
        arguments.confirm("Set permission %s=%s for item", permission, value);
    }

    public static void parseGroup(CommandParserArgs arguments, ItemStack stack) throws CommandException
    {
        if (arguments.isEmpty())
            throw new TranslatedCommandException(FEPermissions.MSG_NOT_ENOUGH_ARGUMENTS);

        arguments.tabComplete(APIRegistry.perms.getServerZone().getGroups());
        String group = arguments.remove();

        if (arguments.isTabCompletion)
            return;
        getSettingsTag(stack).appendTag(new NBTTagString(group));
        arguments.confirm("Added group %s to item", group);
    }

    public static NBTTagCompound getOrCreatePermissionTag(ItemStack stack)
    {
        NBTTagCompound stackTag = ItemUtil.getTagCompound(stack);
        NBTTagCompound tag = stackTag.getCompoundTag(ItemPermissionManager.TAG_BASE);
        stackTag.setTag(ItemPermissionManager.TAG_BASE, tag);
        return tag;
    }

    public static NBTTagList getSettingsTag(ItemStack stack)
    {
        NBTTagCompound tag = getOrCreatePermissionTag(stack);
        NBTTagList settings = ItemPermissionManager.getSettingsTag(tag);
        tag.setTag(ItemPermissionManager.TAG_SETTINGS, settings);
        return settings;
    }

}
