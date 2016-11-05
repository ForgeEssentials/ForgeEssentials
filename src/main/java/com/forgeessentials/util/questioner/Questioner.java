package com.forgeessentials.util.questioner;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.command.ICommandSender;

import com.forgeessentials.util.FECommandManager;
import com.forgeessentials.util.ChatUtil;
import com.forgeessentials.util.Translator;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class Questioner extends ServerEventHandler
{

    public static final String MSG_STILL_ACTIVE = "Error. There is still an unanswered question left";

    private static Map<ICommandSender, QuestionData> questions = new HashMap<>();

    public static int DEFAULT_TIMEOUT = 120;

    public Questioner()
    {
        super();
        FECommandManager.registerCommand(new CommandQuestioner(true));
        FECommandManager.registerCommand(new CommandQuestioner(false));
    }

    public static synchronized void add(QuestionData question) throws QuestionerStillActiveException
    {
        if (questions.containsKey(question.getTarget()))
            throw new QuestionerStillActiveException();
        questions.put(question.getTarget(), question);
        question.sendQuestion();
    }

    public static void add(ICommandSender target, String question, QuestionerCallback callback, int timeout, ICommandSender source)
            throws QuestionerStillActiveException
    {
        add(new QuestionData(target, question, callback, timeout, source));
    }

    public static void add(ICommandSender target, String question, QuestionerCallback callback, int timeout) throws QuestionerStillActiveException
    {
        add(target, question, callback, timeout, null);
    }

    public static void add(ICommandSender target, String question, QuestionerCallback callback) throws QuestionerStillActiveException
    {
        add(target, question, callback, DEFAULT_TIMEOUT);
    }

    public static void addChecked(ICommandSender target, String question, QuestionerCallback callback, int timeout, ICommandSender source)
            throws QuestionerStillActiveException.CommandException
    {
        try
        {
            add(new QuestionData(target, question, callback, timeout, source));
        }
        catch (QuestionerStillActiveException e)
        {
            throw new QuestionerStillActiveException.CommandException();
        }
    }

    public static void addChecked(ICommandSender target, String question, QuestionerCallback callback, int timeout)
            throws QuestionerStillActiveException.CommandException
    {
        try
        {
            add(target, question, callback, timeout, null);
        }
        catch (QuestionerStillActiveException e)
        {
            throw new QuestionerStillActiveException.CommandException();
        }
    }

    public static void addChecked(ICommandSender target, String question, QuestionerCallback callback) throws QuestionerStillActiveException.CommandException
    {
        try
        {
            add(target, question, callback, DEFAULT_TIMEOUT);
        }
        catch (QuestionerStillActiveException e)
        {
            throw new QuestionerStillActiveException.CommandException();
        }
    }

    public static synchronized void answer(ICommandSender target, Boolean answer)
    {
        QuestionData question = questions.remove(target);
        if (question != null)
            question.doAnswer(answer);
        else
            ChatUtil.chatError(target, Translator.translate("There is no question to answer!"));
    }

    public static synchronized void tick()
    {
        Iterator<Entry<ICommandSender, QuestionData>> it = questions.entrySet().iterator();
        while (it.hasNext()) {
            Entry<ICommandSender, QuestionData> question = it.next();
            if (question.getValue().isTimeout()) {
				it.remove();
				question.getValue().doAnswer(null);
            }
        }
    }

    public static void cancel(ICommandSender target)
    {
        answer(target, null);
    }

    public static void confirm(ICommandSender target)
    {
        answer(target, true);
    }

    public static void deny(ICommandSender target)
    {
        answer(target, false);
    }

    @SubscribeEvent
    public void tickStart(TickEvent.ServerTickEvent event)
    {
        if (event.phase == Phase.START)
            tick();
    }

}
