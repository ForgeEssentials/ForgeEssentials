package com.forgeessentials.chat.command;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.chat.Mailer;
import com.forgeessentials.chat.Mailer.Mail;
import com.forgeessentials.chat.Mailer.Mails;
import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandMail extends ForgeEssentialsCommandBuilder
{

    public CommandMail(String name, int permissionLevel, boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "mail";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.chat.mail";
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
                .then(Commands.literal("read")
                        .executes(CommandContext -> execute(CommandContext, "read")
                                )
                        )
                .then(Commands.literal("readall")
                        .executes(CommandContext -> execute(CommandContext, "readall")
                                )
                        )
                .then(Commands.literal("send")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("message", MessageArgument.message())
                                        .executes(CommandContext -> execute(CommandContext, "send")
                                                )
                                        )
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "blank")
                        )
                ;
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params.toString() == "blank")
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("/mail read: Read next mail"));
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("/mail readall: Read all mails"));
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("/mail send <player> <msg...>: Send a mail"));
            return Command.SINGLE_SUCCESS;
        }

        if (params.toString() == "read")
        {
            if (!(ctx.getSource().getEntity() instanceof PlayerEntity))
                throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
            Mails mailBag = Mailer.getMailBag(UserIdent.get(ctx.getSource()));
            if (mailBag.mails.isEmpty())
                throw new TranslatedCommandException("You have no mails to read");
            readMail(ctx.getSource(), mailBag.mails.remove(0));
            Mailer.saveMails(getIdent(ctx.getSource()), mailBag);
            return Command.SINGLE_SUCCESS;
        }
        if (params.toString() == "readall")
        {
            if (!(ctx.getSource().getEntity() instanceof PlayerEntity))
                throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
            Mails mailBag = Mailer.getMailBag(UserIdent.get(ctx.getSource()));
            if (mailBag.mails.isEmpty())
                throw new TranslatedCommandException("You have no mails to read");
            for (Mail mail : mailBag.mails)
                readMail(ctx.getSource(), mail);
            mailBag.mails.clear();
            Mailer.saveMails(getIdent(ctx.getSource()), mailBag);
            return Command.SINGLE_SUCCESS;
        }
        if (params.toString() == "send")
        {
            PlayerEntity player = EntityArgument.getPlayer(ctx, "player");
            UserIdent receiver = UserIdent.get(player);
            ITextComponent message = MessageArgument.getMessage(ctx, "message");
            Mailer.sendMail(getIdent(ctx.getSource()), receiver,message.toString());
            ChatOutputHandler.chatConfirmation(ctx.getSource(),Translator.format("You sent a mail to %s", receiver.getUsernameOrUuid()));
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }

    public static void readMail(CommandSource sender, Mail mail)
    {
        ChatOutputHandler.chatNotification(sender,
                Translator.format("Mail from %s on the %s", mail.sender.getUsernameOrUuid(), FEConfig.FORMAT_DATE_TIME.format(mail.timestamp)));
        ChatOutputHandler.chatConfirmation(sender, ChatOutputHandler.formatColors(mail.message));
    }
}
