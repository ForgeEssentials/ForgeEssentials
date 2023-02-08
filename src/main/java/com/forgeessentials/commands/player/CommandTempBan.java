package com.forgeessentials.commands.player;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandTempBan extends BaseCommand
{

    public CommandTempBan(String name, int permissionLevel, boolean enabled)
    {
        super(enabled);
    }

    public static final String PERM_BAN_REASON = "tempban.reason";

    @Override
    public String getPrimaryAlias()
    {
        return "tempban";
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
        return ModuleCommands.PERM + ".tempban";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.argument("player", MessageArgument.message())
                        .then(Commands.argument("duration", MessageArgument.message())
                                .then(Commands.argument("reasion", MessageArgument.message())
                                        .executes(CommandContext -> execute(CommandContext)
                                                )
                                        )
                                )
                        );
    }

    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        String name = MessageArgument.getMessage(ctx, "player").getString();
        String reason = MessageArgument.getMessage(ctx, "reasion").getString();
        String durationS = MessageArgument.getMessage(ctx, "duration").getString();
        UserIdent ident = UserIdent.get(MessageArgument.getMessage(ctx, "player").getString(), ctx.getSource(), true);
        if (ident == null || !ident.hasUuid())
            throw new TranslatedCommandException("Player %s not found", name);
        else if (!ident.hasPlayer())
            throw new TranslatedCommandException("Player %s is not online", name);

        long duration = parseTimeReadable(durationS);

        PlayerInfo pi = PlayerInfo.get(ident.getUuid());
        pi.startTimeout("tempban", duration);

        String durationString = ChatOutputHandler.formatTimeDurationReadable(duration / 1000, true);
        if (ident.hasPlayer())
            ident.getPlayerMP().connection.disconnect(new TranslationTextComponent(Translator.format("You have been banned for %s", durationString)));

        if (!reason.isEmpty())
        {
            ChatOutputHandler.sendMessage(ctx.getSource(),
                    Translator.format("Player %s, has been temporarily banned for %s. Reason: %s", ident.getUsername(), durationString, reason));
            APIRegistry.perms.setPlayerPermissionProperty(ident, PERM_BAN_REASON, reason);
        }
        else
        {
            ChatOutputHandler.sendMessage(ctx.getSource(),
                    Translator.format("Player %s, has been temporarily banned for %s", ident.getUsername(), durationString));
        }
        return Command.SINGLE_SUCCESS;
    }
}
