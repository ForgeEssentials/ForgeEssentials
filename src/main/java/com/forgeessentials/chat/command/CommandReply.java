package com.forgeessentials.chat.command;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandReply extends ForgeEssentialsCommandBuilder
{

    public CommandReply(String name, int permissionLevel, boolean enabled)
    {
        super(enabled);
    }

    public static Map<CommandSource, WeakReference<CommandSource>> replyMap = new WeakHashMap<>();

    public static void messageSent(CommandSource argFrom, CommandSource argTo)
    {
        replyMap.put(argTo, new WeakReference<CommandSource>(argFrom));
    }

    public static CommandSource getReplyTarget(CommandSource sender)
    {
        WeakReference<CommandSource> replyTarget = replyMap.get(sender);
        if (replyTarget == null)
            return null;
        return replyTarget.get();
    }

    /* ------------------------------------------------------------ */

    @Override
    public String getPrimaryAlias()
    {
        return "reply";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "r" };
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleChat.PERM + ".reply";
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.argument("message", MessageArgument.message())
                        .executes(CommandContext -> execute(CommandContext, "message")
                                )
                        );
    }
    
    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        ITextComponent message = MessageArgument.getMessage(ctx, "message");
        CommandSource target = getReplyTarget(ctx.getSource());
        if (target == null)
            throw new CommandException(Translator.translateITC("No reply target found"));

        if (target == ctx.getSource())
            throw new CommandException(Translator.translateITC("commands.message.sameTarget"));

        ModuleChat.tell(ctx.getSource(), message, target);
        return Command.SINGLE_SUCCESS;
    }
}
