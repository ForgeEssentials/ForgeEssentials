package com.forgeessentials.chat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.chat.command.CommandMail;
import com.forgeessentials.util.FECommandManager;
import com.forgeessentials.util.data.DataManager;
import com.forgeessentials.util.ChatUtil;
import com.forgeessentials.util.Translator;
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

    public static class Mails
    {

        public UserIdent user;

        public List<Mail> mails = new ArrayList<Mail>();

        public Mails(UserIdent user)
        {
            this.user = user;
        }

    }

    private static Map<UserIdent, Mails> mailBags = new HashMap<>();

    @SubscribeEvent
    public void serverStartingEvent(FEModuleServerInitEvent event)
    {
        loadAllMails();
    }

    @SubscribeEvent
    public void playerLoggedInEvent(PlayerLoggedInEvent event)
    {
        UserIdent user = UserIdent.get(event.player);
        Mails mailBag = getMailBag(user);
        if (mailBag.mails.isEmpty())
            return;
        Set<UserIdent> senders = new HashSet<>();
        for (Mail mail : mailBag.mails)
            senders.add(mail.sender);
        String message = Translator.format("You hav unread mails from %s. Use /mail to read.", UserIdent.join(senders, ", ", " and "));
        ChatUtil.chatConfirmation(event.player, message);
    }

    public static void loadAllMails()
    {
        Map<String, Mails> loadedMails = DataManager.getInstance().loadAll(Mails.class);
        mailBags.clear();
        for (Mails mailBag : loadedMails.values())
            try
            {
                mailBags.put(mailBag.user, mailBag);
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
            DataManager.getInstance().save(mails, user.getOrGenerateUuid().toString());
    }

    public static Mails getMailBag(UserIdent user)
    {
        Mails mails = mailBags.get(user);
        if (mails == null)
            mails = new Mails(user);
        return mails;
    }

    public static void sendMail(UserIdent sender, UserIdent recipent, String message)
    {
        Mails mailBag = getMailBag(recipent);
        mailBag.mails.add(new Mail(sender, message));
        saveMails(recipent, mailBag);
        if (recipent.hasPlayer())
            ChatUtil.chatNotification(recipent.getPlayer(),
                    Translator.format("You have a new mail from %s", sender == null ? "the server" : sender.getUsernameOrUuid()));
    }

}
