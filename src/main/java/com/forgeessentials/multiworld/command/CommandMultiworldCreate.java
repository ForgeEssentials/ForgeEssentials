package com.forgeessentials.multiworld.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.multiworld.ModuleMultiworld;
import com.forgeessentials.multiworld.core.MultiworldTeleporter;

/**
 * @author Björn Zeutzheim
 */
public class CommandMultiworldCreate extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "mwcreate";
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender)
    {
        return "Create a new multiworld";
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] args)
    {
        if (args.length < 1)
            throw new CommandException("Missing name argument");
        WorldServer world = ModuleMultiworld.getMultiworldManager().generateWorld(args[0]);
        if (commandSender instanceof EntityPlayerMP)
        {
            new MultiworldTeleporter(world).teleport((EntityPlayerMP) commandSender);
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
        return ModuleMultiworld.PERM_CREATE;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

}
