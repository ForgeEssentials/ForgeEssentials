package com.forgeessentials.chat.command;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.chat.Mailer;
import com.forgeessentials.chat.Mailer.Mail;
import com.forgeessentials.chat.Mailer.Mails;
import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.OutputHandler;

public class CommandMail extends ParserCommandBase
{

    @Override
    public String getCommandName()
    {
        return "mail";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/mail send|read: Send or read mails";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.chat.mail";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void parse(CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("usage");
            return;
        }

        arguments.tabComplete("readnext", "readall", "send");
        String subArg = arguments.remove().toLowerCase();
        switch (subArg)
        {
        case "readnext":
        {
            if (arguments.senderPlayer == null)
                throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
            if (arguments.isTabCompletion)
                return;
            Mails mails = Mailer.getMails(arguments.ident);
            if (mails.isEmpty())
                throw new TranslatedCommandException("You have no mails to read");
            readMail(arguments.sender, mails.remove(0));
            Mailer.saveMails(arguments.ident, mails);
            break;
        }
        case "readall":
        {
            if (arguments.senderPlayer == null)
                throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
            if (arguments.isTabCompletion)
                return;
            Mails mails = Mailer.getMails(arguments.ident);
            if (mails.isEmpty())
                throw new TranslatedCommandException("You have no mails to read");
            for (Mail mail : mails)
                readMail(arguments.sender, mail);
            mails.clear();
            Mailer.saveMails(arguments.ident, mails);
            break;
        }
        case "send":
        {
            UserIdent receiver = arguments.parsePlayer(false);
            if (arguments.isTabCompletion)
                return;
            if (arguments.isEmpty())
                throw new TranslatedCommandException("No message specified");
            UserIdent sender = arguments.ident != null ? arguments.ident : UserIdent.get("Server");
            Mailer.sendMail(sender, receiver, arguments.toString());
            break;
        }
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND);
        }
    }

    public static void readMail(ICommandSender sender, Mail mail)
    {
        OutputHandler.chatNotification(sender,
                Translator.format("Mail from %s on the %s:", mail.sender.getUsernameOrUuid(), FEConfig.FORMAT_DATE_TIME.format(mail.timestamp)));
        OutputHandler.chatConfirmation(sender, OutputHandler.formatColors(mail.message));
    }

}
