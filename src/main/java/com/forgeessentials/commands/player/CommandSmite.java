package com.forgeessentials.commands.player;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.PlayerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandSmite extends BaseCommand
{

    @Override
    public String getPrimaryAlias()
    {
        return "smite";
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
        return ModuleCommands.PERM + ".smite";
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, ServerPlayerEntity sender, String[] args) throws CommandException
    {
        if (args.length == 1)
        {
            if (args[0].toLowerCase().equals("me"))
            {
                sender.world.addWeatherEffect(new EntityLightningBolt(sender.world, sender.posX, sender.posY, sender.posZ, false));
                ChatOutputHandler.chatConfirmation(sender, "Was that really a good idea?");
            }
            else
            {
                ServerPlayerEntity player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
                if (player != null)
                {
                    player.world.addWeatherEffect(new EntityLightningBolt(player.world, player.posX, player.posY, player.posZ, false));
                    ChatOutputHandler.chatConfirmation(sender, "You should feel bad about doing that.");
                }
                else
                    throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
            }
        }
        else if (args.length > 1)
        {
            if (args.length != 3)
            {
                throw new TranslatedCommandException("Need coordinates X, Y, Z.");
            }
            int x = Integer.valueOf(args[0]);
            int y = Integer.valueOf(args[1]);
            int z = Integer.valueOf(args[2]);
            sender.world.addWeatherEffect(new EntityLightningBolt(sender.world, x, y, z, false));
            ChatOutputHandler.chatConfirmation(sender, "I hope that didn't start a fire.");
        }
        else
        {
            RayTraceResult mop = PlayerUtil.getPlayerLookingSpot(sender, 500);
            if (mop == null)
            {
                ChatOutputHandler.chatError(sender, "You must first look at the ground!");
            }
            else
            {
                BlockPos pos = mop.getBlockPos();
                sender.world.addWeatherEffect(new EntityLightningBolt(sender.world, pos.getX(), pos.getY(), pos.getZ(), false));
                ChatOutputHandler.chatConfirmation(sender, "I hope that didn't start a fire.");
            }
        }
    }

    @Override
    public void processCommandConsole(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length >= 1)
        {
            ServerPlayerEntity player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                player.world.addWeatherEffect(new EntityLightningBolt(player.world, player.posX, player.posY, player.posZ, false));
                ChatOutputHandler.chatConfirmation(sender, "You should feel bad about doing that.");
            }
            else
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
        }
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
