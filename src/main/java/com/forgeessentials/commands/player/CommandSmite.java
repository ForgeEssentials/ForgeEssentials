package com.forgeessentials.commands.player;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.PlayerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandSmite extends ForgeEssentialsCommandBase
{

    @Override
    public String getPrimaryAlias()
    {
        return "smite";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        if (sender instanceof EntityPlayer)
        {
            return "/smite [me|player] Smite yourself, another player, or the spot you are looking at.";
        }
        else
        {
            return "/smite <player> Smite someone.";
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
        return ModuleCommands.PERM + ".smite";
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP sender, String[] args) throws CommandException
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
                EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
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
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                player.world.addWeatherEffect(new EntityLightningBolt(player.world, player.posX, player.posY, player.posZ, false));
                ChatOutputHandler.chatConfirmation(sender, "You should feel bad about doing that.");
            }
            else
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
        }
        else
            throw new TranslatedCommandException(getUsage(sender));
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
