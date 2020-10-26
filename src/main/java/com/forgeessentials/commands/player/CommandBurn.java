package com.forgeessentials.commands.player;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandBurn extends ForgeEssentialsCommandBase
{

    @Override
    public String getPrimaryAlias()
    {
        return "burn";
    }

    @Override
    public String getUsage(ICommandSender sender)
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

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".burn";
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP sender, String[] args) throws CommandException
    {
        if (args.length == 1)
        {
            if (args[0].toLowerCase().equals("me"))
            {
                sender.setFire(15);
                ChatOutputHandler.chatError(sender, "Ouch! Hot!");
            }
            else if (PermissionAPI.hasPermission(sender, getPermissionNode() + ".others"))
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
                sender.setFire(parseInt(args[1]));
                ChatOutputHandler.chatError(sender, "Ouch! Hot!");
            }
            else if (PermissionAPI.hasPermission(sender, getPermissionNode() + ".others"))
            {
                EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
                if (player != null)
                {
                    player.setFire(parseInt(args[1], 0, Integer.MAX_VALUE));
                    ChatOutputHandler.chatConfirmation(sender, "You should feel bad about doing that.");
                }
                else
                    throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
            }
        }
        else
        {
            throw new TranslatedCommandException(getUsage(sender));
        }
    }

    @Override
    public void processCommandConsole(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        int time = 15;
        if (args.length == 2)
        {
            time = parseInt(args[1], 0, Integer.MAX_VALUE);
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
        APIRegistry.perms.registerPermission(getPermissionNode() + ".others", DefaultPermissionLevel.OP, "Apply burn effect on others");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return matchToPlayers(args);
        }
        else
        {
            return null;
        }
    }

}
