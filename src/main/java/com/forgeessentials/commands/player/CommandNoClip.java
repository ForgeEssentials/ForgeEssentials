package com.forgeessentials.commands.player;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.packets.Packet05Noclip;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.WorldUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandNoClip extends ForgeEssentialsCommandBuilder
{

    public CommandNoClip(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.argument("toggle", BoolArgumentType.bool())
                        .executes(CommandContext -> execute(CommandContext, "blank")))
                .executes(CommandContext -> execute(CommandContext, "toggle"));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayer player = (ServerPlayer) ctx.getSource().getEntity();
        if (!PlayerInfo.get(player).getHasFEClient())
        {
            ChatOutputHandler.chatError(ctx.getSource(), "You need the FE client addon to use this command.");
            ChatOutputHandler.chatError(ctx.getSource(),
                    "Please visit https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/FE-Client-mod for more information.");
            return Command.SINGLE_SUCCESS;
        }

        if (!player.getAbilities().flying && !player.noPhysics)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "You must be flying.");
            return Command.SINGLE_SUCCESS;
        }

        PlayerInfo pi = PlayerInfo.get(player);
        if (player.noPhysics && !pi.isNoClip())
        {
            ChatOutputHandler.chatError(ctx.getSource(),
                    "Unable to enable noClip, another mod is using this functionality!");
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("toggle"))
        {
            pi.setNoClip(!pi.isNoClip());
        }
        else
        {
            pi.setNoClip(BoolArgumentType.getBool(ctx, "toggle"));
        }

        player.noPhysics = pi.isNoClip();
        if (!pi.isNoClip())
        {
            WorldUtil.placeInWorld(player);
        }

        NetworkUtils.sendTo(new Packet05Noclip(pi.isNoClip()), player);
        ChatOutputHandler.chatConfirmation(player, "Noclip " + (pi.isNoClip() ? "enabled" : "disabled"));
        return Command.SINGLE_SUCCESS;
    }

    public static void checkClip(Player player)
    {
        PlayerInfo pi = PlayerInfo.get(player);
        if (pi.isNoClip() && APIRegistry.perms.checkPermission(player, ModuleCommands.PERM + ".noclip"))
        {
            if (!player.getAbilities().flying)
            {
                pi.setNoClip(false);
                player.noPhysics = false;
                WorldUtil.placeInWorld(player);
                if (player.isControlledByLocalInstance())
                {
                    NetworkUtils.sendTo(new Packet05Noclip(pi.isNoClip()), (ServerPlayer) player);
                    ChatOutputHandler.chatNotification(player,
                            "NoClip auto-disabled: the targeted player is not flying");
                }
            }
        }
    }
}
