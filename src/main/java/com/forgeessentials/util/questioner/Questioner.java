package com.forgeessentials.util.questioner;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.command.ICommandSender;

import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class Questioner extends ServerEventHandler
{

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

    public static synchronized void answer(ICommandSender target, Boolean answer)
    {
        QuestionData question = questions.remove(target);
        if (question != null)
            question.doAnswer(answer);
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
        synchronized (Questioner.class)
        {
            for (Entry<ICommandSender, QuestionData> question : questions.entrySet())
                if (question.getValue().isTimeout())
                    cancel(question.getKey());
        }
    }

}
