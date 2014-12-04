package com.forgeessentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandKill extends FEcmdModuleCommands {

    @Override
    public String getCommandName()
    {
        return "kill";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (args.length >= 1 && PermissionsManager.checkPermission(sender, getPermissionNode() + ".others"))
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                player.attackEntityFrom(DamageSource.outOfWorld, 1000);
                OutputHandler.chatError(player, "You were killed. You probably deserved it.");
            }
            else
            {
                OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
            }
        }
        else
        {
            sender.attackEntityFrom(DamageSource.outOfWorld, 1000);
            OutputHandler.chatError(sender, "You were killed. You probably deserved it.");
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length >= 1)
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                player.attackEntityFrom(DamageSource.outOfWorld, 1000);
                OutputHandler.chatError(player, "You were killed. You probably deserved it.");
            }
            else
            {
                OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
            }
        }
        else
        {
        	throw new WrongUsageException(getCommandUsage(sender));
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".others", RegisteredPermValue.OP);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else
        {
            return null;
        }
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/kill <player> Commit suicide or kill other players (with special permission).";
    }

}
