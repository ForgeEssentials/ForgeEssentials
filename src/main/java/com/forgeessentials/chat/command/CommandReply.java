package com.forgeessentials.chat.command;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.BaseComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandReply extends ForgeEssentialsCommandBuilder
{

    public CommandReply(boolean enabled)
    {
        super(enabled);
    }

    public static Map<Player, WeakReference<Player>> replyMap = new WeakHashMap<>();

    public static void messageSent(Player argFrom, Player argTo)
    {
        replyMap.put(argTo, new WeakReference<>(argFrom));
    }

    public static Player getReplyTarget(Player sender)
    {
        WeakReference<Player> replyTarget = replyMap.get(sender);
        if (replyTarget == null)
            return null;
        return replyTarget.get();
    }

    /* ------------------------------------------------------------ */

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "reply";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
    {
        return new String[] { "r" };
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.argument("message", StringArgumentType.greedyString())
                .executes(CommandContext -> execute(CommandContext, "blank")));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        Player target = getReplyTarget(getServerPlayer(ctx.getSource()));
        if (target == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "No reply target found");
            return Command.SINGLE_SUCCESS;
        }
        if (target.equals(getServerPlayer(ctx.getSource())))
        {
            ChatOutputHandler.chatError(ctx.getSource(), "You can't be the recipient");
            return Command.SINGLE_SUCCESS;
        }
        BaseComponent message = new TextComponent(StringArgumentType.getString(ctx, "message"));
        ModuleChat.tell(ctx.getSource(), message, target.createCommandSourceStack());
        return Command.SINGLE_SUCCESS;
    }
}
