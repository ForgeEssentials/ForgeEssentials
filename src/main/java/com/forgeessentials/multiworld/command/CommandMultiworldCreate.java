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
import com.forgeessentials.multiworld.core.exception.WorldTypeNotFoundException;

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
        String name;
        String provider  = "normal";
        String worldType = "default";

        if (args.length < 1)
            throw new CommandException("Missing name argument");

        name = args[0];
        if (args.length > 1) provider  = args[1];
        if (args.length > 2) worldType = args[2];
        if (args.length > 3)
            throw new CommandException("Too many arguments");

        Multiworld world = new Multiworld(name, provider, worldType);
      
        try
        {
            ModuleMultiworld.getMultiworldManager().addWorld(world);
            if (commandSender instanceof EntityPlayerMP)
            {
                new MultiworldTeleporter(world.getWorldServer()).teleport((EntityPlayerMP) commandSender);
            }
        }
        catch (ProviderNotFoundException e)
        {
            throw new CommandException("World-provider not found!");
        }
        catch (WorldTypeNotFoundException e)
        {
            throw new CommandException("World-type not found!");
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
