package com.forgeessentials.chat.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import java.util.ArrayList;
import java.util.List;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

public class CommandGroupMessage extends ForgeEssentialsCommandBuilder
{

    public CommandGroupMessage(boolean enabled)
    {
        super(enabled);
    }

    public static final String PERM = "fe.chat.groupmessage";

    @Override
    public String getPrimaryAlias()
    {
        return "gmsg";
    }

    @Override
    public String getPermissionNode()
    {
        return PERM;
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
                .then(Commands.argument("group", StringArgumentType.greedyString())
                        .suggests(SUGGEST_GROUPS)
                        .then(Commands.argument("message", MessageArgument.message())
                                .executes(CommandContext -> execute(CommandContext)
                                        )
                                )
                        );
    }

    public static final SuggestionProvider<CommandSource> SUGGEST_GROUPS = (ctx, builder) -> {
        List<String> groups = new ArrayList<>();
        for (String group : APIRegistry.perms.getServerZone().getGroups())
            groups.add(group);
        return ISuggestionProvider.suggest(groups, builder);
     };

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        String group = StringArgumentType.getString(ctx, "group");
        APIRegistry.perms.getServerZone().getGroups();

        ITextComponent msgComponent = MessageArgument.getMessage(ctx, "message");
        ModuleChat.tellGroup(ctx.getSource(), msgComponent.getString(), group, getIdent(ctx.getSource()).checkPermission(ModuleChat.PERM_COLOR));
        return Command.SINGLE_SUCCESS;
    }
}
