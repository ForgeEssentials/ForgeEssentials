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
public class CommandMultiworldList extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "mwls";
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender)
    {
        return "Print a list of available worlds";
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] args)
    {
        commandSender.addChatMessage(new ChatComponentText("Available worlds:"));
        for (Multiworld world : ModuleMultiworld.getMultiworldManager().getWorlds())
        {
            commandSender.addChatMessage(new ChatComponentText("#" + world.getDimensionId() + " " + world.getName() + ": " + world.getProvider()));
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
        return ModuleMultiworld.PERM_LIST;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

}
