package com.forgeessentials.commands.player;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet5Noclip;
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
    public void processCommandPlayer(MinecraftServer server, ServerPlayerEntity player, String[] args) throws CommandException
    {
        if (!PlayerInfo.get(player).getHasFEClient())
        {
            ChatOutputHandler.chatError(player, "You need the FE client addon to use this command.");
            ChatOutputHandler.chatError(player, "Please visit https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/FE-Client-mod for more information.");
            return;
        }

        if (!player.abilities.flying && !player.noPhysics)
            throw new TranslatedCommandException("You must be flying.");

        PlayerInfo pi = PlayerInfo.get(player);
        if (args.length == 0)
        {
            pi.setNoClip(!pi.isNoClip());
        }
        else
        {
            pi.setNoClip(Boolean.parseBoolean(args[0]));
        }
        if (!pi.isNoClip())
            WorldUtil.placeInWorld(player);
        NetworkUtils.sendTo(new Packet5Noclip(pi.isNoClip()), player);
        ChatOutputHandler.chatConfirmation(player, "Noclip " + (pi.isNoClip() ? "enabled" : "disabled"));
    }

    public static void checkClip(PlayerEntity player)
    {
        PlayerInfo pi = PlayerInfo.get(player);
        if (pi.isNoClip() && PermissionAPI.hasPermission(player, ModuleCommands.PERM + ".noclip"))
        {
            if (!player.abilities.flying)
            {
                pi.setNoClip(false);
                WorldUtil.placeInWorld(player);
                if (player.isControlledByLocalInstance())
                {
                    NetworkUtils.sendTo(new Packet5Noclip(pi.isNoClip()), (ServerPlayerEntity) player);
                    ChatOutputHandler.chatNotification(player, "NoClip auto-disabled: the targeted player is not flying");
                }
            }
        }
    }

}
