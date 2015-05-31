package com.forgeessentials.chat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.chat.command.CommandMail;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class Mailer extends ServerEventHandler
{

    public Mailer()
    {
        super();
        FECommandManager.registerCommand(new CommandMail());
    }

    public static class Mail
    {

        public UserIdent sender;

        public String message;

        public Date timestamp = new Date();

        public Mail(UserIdent sender, String message)
        {
            this.sender = sender;
            this.message = message;
        }

    }

    public static class Mails extends ArrayList<Mail>
    {
    }

    private static Map<UserIdent, Mails> mailsMap = new HashMap<>();

    @SubscribeEvent
    public void serverStartingEvent(FEModuleServerInitEvent event)
    {
        loadAllMails();
    }

    @SubscribeEvent
    public void playerLoggedInEvent(PlayerLoggedInEvent event)
    {
        UserIdent user = UserIdent.get(event.player);
        Mails mails = getMails(user);
        Set<UserIdent> senders = new HashSet<>();
        for (Mail mail : mails)
            senders.add(mail.sender);
        String message = Translator.format("You hav unread mails from %s. Use /mail to read.", UserIdent.join(senders, ", ", " and "));
        OutputHandler.chatConfirmation(event.player, message);
    }

    public static void loadAllMails()
    {
        Map<String, Mails> loadedMails = DataManager.getInstance().loadAll(Mails.class);
        mailsMap.clear();
        for (Entry<String, Mails> mailsEntry : loadedMails.entrySet())
            try
            {
                mailsMap.put(UserIdent.fromString(mailsEntry.getKey()), mailsEntry.getValue());
            }
            catch (IllegalArgumentException e)
            {
                /* do nothing */
            }
    }

    public static void saveMails(UserIdent user, Mails mails)
    {
        if (mails == null)
            DataManager.getInstance().delete(Mails.class, user.toString());
        else
            DataManager.getInstance().save(mails, user.toString());
    }

    public static Mails getMails(UserIdent user)
    {
        Mails mails = mailsMap.get(user);
        if (mails == null)
            mails = new Mails();
        return mails;
    }

    public static void sendMail(UserIdent sender, UserIdent recipent, String message)
    {
        Mails mails = getMails(recipent);
        mails.add(new Mail(sender, message));
        saveMails(recipent, mails);
    }

}
