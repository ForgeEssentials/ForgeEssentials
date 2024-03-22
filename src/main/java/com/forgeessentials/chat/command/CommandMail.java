package com.forgeessentials.chat.command;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.chat.Mailer;
import com.forgeessentials.chat.Mailer.Mail;
import com.forgeessentials.chat.Mailer.Mails;
import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandMail extends ForgeEssentialsCommandBuilder
{

    public CommandMail(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "mail";
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
        return baseBuilder.then(Commands.literal("read").executes(CommandContext -> execute(CommandContext, "read")))
                .then(Commands.literal("readall").executes(CommandContext -> execute(CommandContext, "readall")))
                .then(Commands.literal("send")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("message", StringArgumentType.greedyString())
                                        .executes(CommandContext -> execute(CommandContext, "send")))))
                .executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("blank"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("/mail read: Read next mail"));
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("/mail readall: Read all mails"));
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    Translator.format("/mail send <player> <msg...>: Send a mail"));
            return Command.SINGLE_SUCCESS;
        }

        if (params.equals("read"))
        {
            if (!(ctx.getSource().getEntity() instanceof Player))
            {
                ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_CONSOLE_COMMAND);
                return Command.SINGLE_SUCCESS;
            }
            Mails mailBag = Mailer.getMailBag(UserIdent.get(ctx.getSource()));
            if (mailBag.mails.isEmpty())
            {
                ChatOutputHandler.chatWarning(ctx.getSource(), "You have no mails to read");
                return Command.SINGLE_SUCCESS;
            }
            readMail(ctx.getSource(), mailBag.mails.remove(0));
            Mailer.saveMails(getIdent(ctx.getSource()), mailBag);
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("readall"))
        {
            if (!(ctx.getSource().getEntity() instanceof Player))
            {
                ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_NO_CONSOLE_COMMAND);
                return Command.SINGLE_SUCCESS;
            }
            Mails mailBag = Mailer.getMailBag(UserIdent.get(ctx.getSource()));
            if (mailBag.mails.isEmpty())
            {
                ChatOutputHandler.chatWarning(ctx.getSource(), "You have no mails to read");
                return Command.SINGLE_SUCCESS;
            }
            for (Mail mail : mailBag.mails)
                readMail(ctx.getSource(), mail);
            mailBag.mails.clear();
            Mailer.saveMails(getIdent(ctx.getSource()), mailBag);
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("send"))
        {
            Player player = EntityArgument.getPlayer(ctx, "player");
            UserIdent receiver = UserIdent.get(player);
            Mailer.sendMail(getIdent(ctx.getSource()), receiver, StringArgumentType.getString(ctx, "message"));
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    Translator.format("You sent a mail to %s", receiver.getUsernameOrUuid()));
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }

    public static void readMail(CommandSourceStack sender, Mail mail)
    {
        ChatOutputHandler.chatNotification(sender,
                Translator.format("Mail from %s on the %s",
                        mail.sender == null ? "server" : mail.sender.getUsernameOrUuid(),
                        FEConfig.FORMAT_DATE_TIME.format(mail.timestamp)));
        ChatOutputHandler.chatConfirmation(sender, ChatOutputHandler.formatColors(mail.message));
    }
}
