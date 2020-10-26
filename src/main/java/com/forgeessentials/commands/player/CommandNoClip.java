package com.forgeessentials.commands.player;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.Packet5Noclip;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.WorldUtil;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandNoClip extends ForgeEssentialsCommandBase
{

    @Override
    public String getPrimaryAlias()
    {
        return "noclip";
    }

    @Override
    public String getUsage(ICommandSender p_71518_1_)
    {
        return "/noclip [true/false]";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".noclip";
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP player, String[] args) throws CommandException
    {
        if (!PlayerInfo.get(player).getHasFEClient())
        {
            ChatOutputHandler.chatError(player, "You need the FE client addon to use this command.");
            ChatOutputHandler.chatError(player, "Please visit https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/FE-Client-mod for more information.");
            return;
        }

        if (!player.capabilities.isFlying && !player.noClip)
            throw new TranslatedCommandException("You must be flying.");

        if (args.length == 0)
        {
            if (!player.noClip)
                player.noClip = true;
            else
                player.noClip = false;
        }
        else
        {
            player.noClip = Boolean.parseBoolean(args[0]);
        }
        if (!player.noClip)
            WorldUtil.placeInWorld(player);

        NetworkUtils.netHandler.sendTo(new Packet5Noclip(player.noClip), player);
        ChatOutputHandler.chatConfirmation(player, "Noclip " + (player.noClip ? "enabled" : "disabled"));
    }

    public static void checkClip(EntityPlayer player)
    {
        if (player.noClip && PermissionAPI.hasPermission(player, ModuleCommands.PERM + ".noclip"))
        {
            if (!player.capabilities.isFlying)
            {
                player.noClip = false;
                WorldUtil.placeInWorld(player);
                if (!player.world.isRemote)
                {
                    NetworkUtils.netHandler.sendTo(new Packet5Noclip(player.noClip), (EntityPlayerMP) player);
                    ChatOutputHandler.chatNotification(player, "NoClip auto-disabled: the targeted player is not flying");
                }
            }
        }
    }

}
