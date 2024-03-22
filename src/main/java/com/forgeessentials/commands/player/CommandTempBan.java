package com.forgeessentials.commands.player;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandTempBan extends ForgeEssentialsCommandBuilder
{

    public CommandTempBan(boolean enabled)
    {
        super(enabled);
    }

    public static final String PERM_BAN_REASON = "tempban.reason";

    @Override
    public @NotNull String getPrimaryAlias()
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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.argument("player", StringArgumentType.word())
                .then(Commands.argument("duration", StringArgumentType.word())
                        .then(Commands.argument("reasion", StringArgumentType.greedyString())
                                .executes(CommandContext -> execute(CommandContext)))));
    }

    public int execute(CommandContext<CommandSourceStack> ctx, String... params) throws CommandSyntaxException
    {
        String name = StringArgumentType.getString(ctx, "player");
        String reason = StringArgumentType.getString(ctx, "reasion");
        String durationS = StringArgumentType.getString(ctx, "duration");
        UserIdent ident = UserIdent.get(name, ctx.getSource(), true);
        if (ident == null || !ident.hasUuid())
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Player %s not found", name);
        }
        else if (!ident.hasPlayer())
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Player %s is not online", name);
        }

        long duration;
        try
        {
            duration = parseTimeReadable(durationS);
        }
        catch (FECommandParsingException e)
        {
            ChatOutputHandler.chatError(ctx.getSource(), e.error);
            return Command.SINGLE_SUCCESS;
        }

        PlayerInfo pi = PlayerInfo.get(ident.getUuid());
        pi.startTimeout("tempban", duration);

        String durationString = ChatOutputHandler.formatTimeDurationReadable(duration / 1000, true);
        if (ident.hasPlayer())
            ident.getPlayerMP().connection.disconnect(
                    new TextComponent(Translator.format("You have been banned for %s", durationString)));

        if (!reason.isEmpty())
        {
            ChatOutputHandler.sendMessage(ctx.getSource(),
                    Translator.format("Player %s, has been temporarily banned for %s. Reason: %s", ident.getUsername(),
                            durationString, reason));
            APIRegistry.perms.setPlayerPermissionProperty(ident, PERM_BAN_REASON, reason);
        }
        else
        {
            ChatOutputHandler.sendMessage(ctx.getSource(), Translator
                    .format("Player %s, has been temporarily banned for %s", ident.getUsername(), durationString));
        }
        return Command.SINGLE_SUCCESS;
    }
}
