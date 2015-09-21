package com.forgeessentials.util.questioner;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.util.events.ServerEventHandler;

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

    public static synchronized void answer(ICommandSender target, Boolean answer) throws CommandException
    {
        QuestionData question = questions.remove(target);
        if (question != null)
            question.doAnswer(answer);
    }

    public static synchronized void tick()
    {
        for (Entry<ICommandSender, QuestionData> question : questions.entrySet())
            if (question.getValue().isTimeout())
                try
                {
                    cancel(question.getKey());
                }
                catch (CommandException e)
                {
                    e.printStackTrace();
                }
    }

    public static void cancel(ICommandSender target) throws CommandException
    {
        answer(target, null);
    }

    public static void confirm(ICommandSender target) throws CommandException
    {
        answer(target, true);
    }

    public static void deny(ICommandSender target) throws CommandException
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
