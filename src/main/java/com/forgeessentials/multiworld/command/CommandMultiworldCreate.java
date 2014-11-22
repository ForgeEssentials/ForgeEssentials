package com.forgeessentials.multiworld.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.multiworld.ModuleMultiworld;
import com.forgeessentials.multiworld.core.Multiworld;
import com.forgeessentials.multiworld.core.MultiworldTeleporter;
import com.forgeessentials.multiworld.core.exception.MultiworldAlreadyExistsException;
import com.forgeessentials.multiworld.core.exception.ProviderNotFoundException;

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
        if (args.length < 2)
            throw new CommandException("Missing provider argument");
        Multiworld world = new Multiworld(args[0], args[1]); // Multiworld.PROVIDER_CUSTOM
        try
        {
            ModuleMultiworld.getMultiworldManager().addWorld(world);
            if (commandSender instanceof EntityPlayerMP)
            {
                new MultiworldTeleporter(world.getWorld()).teleport((EntityPlayerMP) commandSender);
            }
        }
        catch (ProviderNotFoundException e)
        {
            throw new CommandException("World-provider not found!");
        }
        catch (MultiworldAlreadyExistsException e)
        {
            throw new CommandException("A world with that name already exists!");
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
