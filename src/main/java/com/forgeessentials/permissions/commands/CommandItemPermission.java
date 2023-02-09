package com.forgeessentials.permissions.commands;

import static com.forgeessentials.permissions.core.ItemPermissionManager.TAG_MODE;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.permissions.core.ItemPermissionManager;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.ItemUtil;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class CommandItemPermission extends ForgeEssentialsCommandBuilder
{

    public CommandItemPermission(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "permitem";
    }

    @Override
    public String getPermissionNode()
    {
        return PermissionCommandParser.PERM + ".permitem";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        ItemStack stack = arguments.senderPlayer.getMainHandItem();
        if (stack == ItemStack.EMPTY)
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
            CompoundNBT stackTag = stack.getTag();
            if (stackTag != null)
                stackTag.remove(ItemPermissionManager.TAG_BASE);
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
            getOrCreatePermissionTag(stack).putByte(TAG_MODE, ItemPermissionManager.MODE_INVENTORY);
            break;
        case "equip":
            getOrCreatePermissionTag(stack).putByte(TAG_MODE, ItemPermissionManager.MODE_EQUIP);
            break;
        case "use":
            getOrCreatePermissionTag(stack).putByte(TAG_MODE, ItemPermissionManager.MODE_USE);
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
        getSettingsTag(stack).add(new StringNBT(permission + "=" + value));
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
        getSettingsTag(stack).add(new StringNBT(group));
        arguments.confirm("Added group %s to item", group);
    }

    public static CompoundNBT getOrCreatePermissionTag(ItemStack stack)
    {
        CompoundNBT stackTag = ItemUtil.getTagCompound(stack);
        CompoundNBT tag = stackTag.getCompound(ItemPermissionManager.TAG_BASE);
        stackTag.put(ItemPermissionManager.TAG_BASE, tag);
        return tag;
    }

    public static ListNBT getSettingsTag(ItemStack stack)
    {
        CompoundNBT tag = getOrCreatePermissionTag(stack);
        ListNBT settings = ItemPermissionManager.getSettingsTag(tag);
        tag.put(ItemPermissionManager.TAG_SETTINGS, settings);
        return settings;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
