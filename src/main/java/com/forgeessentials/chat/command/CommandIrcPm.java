package com.forgeessentials.chat.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import java.util.ArrayList;
import java.util.List;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.chat.irc.IrcHandler;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

public class CommandIrcPm extends ForgeEssentialsCommandBuilder
{

    public CommandIrcPm(boolean enabled)
    {
        super(enabled);
    }


    @Override
    public String getPrimaryAlias()
    {
        return "ircpm";
    }


    @Override
    public String getPermissionNode()
    {
        return "fe.chat.ircpm";
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
        return baseBuilder
                .then(Commands.argument("ircUser", StringArgumentType.string())
                        .suggests(SUGGEST_USERS)
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(CommandContext -> execute(CommandContext, "blank")
                                        )
                                )
                        );
    }

    public static final SuggestionProvider<CommandSource> SUGGEST_USERS = (ctx, builder) -> {
        List<String> ircUsers = new ArrayList<>();
        if(IrcHandler.getInstance().getIrcUserNames()!=null) {
        	for (String users : IrcHandler.getInstance().getIrcUserNames())
                ircUsers.add(users);
        }
        return ISuggestionProvider.suggest(ircUsers, builder);
     };

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (!IrcHandler.getInstance().isConnected()) {
            ChatOutputHandler.chatError(ctx.getSource(),"Not connected to IRC!");
            return Command.SINGLE_SUCCESS;
        }
        String name = StringArgumentType.getString(ctx, "ircUser");
        CommandSource target = IrcHandler.getInstance().getIrcUser(name);
        if (target == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(),"Player not found");
            return Command.SINGLE_SUCCESS;
        }
        else if (target == ctx.getSource())
        {
            ChatOutputHandler.chatError(ctx.getSource(),"Cant send a pm to yourself");
            return Command.SINGLE_SUCCESS;
        }
        else
        {
        	TextComponent message = new StringTextComponent(StringArgumentType.getString(ctx, "name"));
            ModuleChat.tell(ctx.getSource(), message, target);
        }
        return Command.SINGLE_SUCCESS;
    }
}
