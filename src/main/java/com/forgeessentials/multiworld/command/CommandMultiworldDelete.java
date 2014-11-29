package com.forgeessentials.multiworld.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.multiworld.ModuleMultiworld;
import com.forgeessentials.multiworld.core.Multiworld;

/**
 * @author Olee
 */
public class CommandMultiworldDelete extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "mwdel";
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender)
    {
        return "Delete a multiworld";
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] args)
    {
        if (args.length < 1)
        {
            commandSender.addChatMessage(new ChatComponentText("Too few arguments!"));
            return;
        }
        Multiworld world = ModuleMultiworld.getMultiworldManager().getWorld(args[0]);
        if (world != null)
        {
            ModuleMultiworld.getMultiworldManager().deleteWorld(world);
            commandSender.addChatMessage(new ChatComponentText("Deleted Multiworld #" + args[0]));
        }
        else
        {
            commandSender.addChatMessage(new ChatComponentText("Dimension #" + args[0] + " does not exist!"));
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleMultiworld.PERM_DELETE;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

}
