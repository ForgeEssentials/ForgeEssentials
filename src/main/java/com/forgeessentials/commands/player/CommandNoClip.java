package com.forgeessentials.commands.player;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet5Noclip;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.WorldUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandNoClip extends ForgeEssentialsCommandBuilder
{

    public CommandNoClip(boolean enabled)
    {
        super(enabled);
    }

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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.argument("toggle", BoolArgumentType.bool())
                        .executes(CommandContext -> execute(CommandContext, "blank")
                                )
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        boolean toggle = BoolArgumentType.getBool(ctx, "toggle");
        ServerPlayerEntity player = (ServerPlayerEntity) ctx.getSource().getEntity();
        if (!PlayerInfo.get(player).getHasFEClient())
        {
            ChatOutputHandler.chatError(ctx.getSource(), "You need the FE client addon to use this command.");
            ChatOutputHandler.chatError(ctx.getSource(), "Please visit https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/FE-Client-mod for more information.");
            return Command.SINGLE_SUCCESS;
        }

        if (!player.abilities.flying && !player.noPhysics)
            throw new TranslatedCommandException("You must be flying.");

        PlayerInfo pi = PlayerInfo.get(player);
        pi.setNoClip(toggle);
        if (!pi.isNoClip())
            WorldUtil.placeInWorld(player);
        NetworkUtils.sendTo(new Packet5Noclip(pi.isNoClip()), player);
        ChatOutputHandler.chatConfirmation(player, "Noclip " + (pi.isNoClip() ? "enabled" : "disabled"));
        return Command.SINGLE_SUCCESS;
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
