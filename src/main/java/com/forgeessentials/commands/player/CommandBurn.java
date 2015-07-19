package com.forgeessentials.commands.player;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraftforge.fml.common.FMLCommonHandler;

public class CommandBurn extends FEcmdModuleCommands
{
    @Override
    public String getCommandName()
    {
        return "burn";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (args.length == 1)
        {
            if (args[0].toLowerCase().equals("me"))
            {
                sender.setFire(15);
                ChatOutputHandler.chatError(sender, "Ouch! Hot!");
            }
            else if (PermissionManager.checkPermission(sender, getPermissionNode() + ".others"))
            {
                EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
                if (player != null)
                {
                    ChatOutputHandler.chatConfirmation(sender, "You should feel bad about doing that.");
                    player.setFire(15);
                }
                else
                    throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
            }
        }
        else if (args.length == 2)
        {
            if (args[0].toLowerCase().equals("me"))
            {
                sender.setFire(parseInt(sender, args[1]));
                ChatOutputHandler.chatError(sender, "Ouch! Hot!");
            }
            else if (PermissionManager.checkPermission(sender, getPermissionNode() + ".others"))
            {
                EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
                if (player != null)
                {
                    player.setFire(parseIntWithMin(sender, args[1], 0));
                    ChatOutputHandler.chatConfirmation(sender, "You should feel bad about doing that.");
                }
                else
                    throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
            }
        }
        else
        {
            throw new TranslatedCommandException(getCommandUsage(sender));
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        int time = 15;
        if (args.length == 2)
        {
            time = parseIntWithMin(sender, args[1], 0);
        }
        EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
        if (player != null)
        {
            player.setFire(time);
            ChatOutputHandler.chatConfirmation(sender, "You should feel bad about doing that.");
        }
        else
            throw new CommandException("Player %s does not exist, or is not online.", args[0]);
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".others", PermissionLevel.OP);
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
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
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        if (sender instanceof EntityPlayer)
        {
            return "/burn <me|player> Set a player on fire.";
        }
        else
        {
            return "/burn <player> Set a player on fire.";
        }
    }
}
