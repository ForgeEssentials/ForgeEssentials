package com.forgeessentials.util.questioner;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Iterator;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;

public class Questioner extends ServerEventHandler
{

    public static final String MSG_STILL_ACTIVE = "Error. There is still an unanswered question left";

    private static Map<CommandSource, QuestionData> questions = new HashMap<>();

    public static int DEFAULT_TIMEOUT = 120;

    public Questioner()
    {
        super();
        FECommandManager.registerCommand(new CommandQuestionerYes("yes", 0, true));
        FECommandManager.registerCommand(new CommandQuestionerNo("no", 0, true));
    }

    public static synchronized void add(QuestionData question) throws QuestionerStillActiveException
    {
        if (questions.containsKey(question.getTarget()))
            throw new QuestionerStillActiveException();
        questions.put(question.getTarget(), question);
        question.sendQuestion();
    }

    public static void add(CommandSource target, String question, QuestionerCallback callback, int timeout, CommandSource source)
            throws QuestionerStillActiveException
    {
        add(new QuestionData(target, question, callback, timeout, source));
    }

    public static void add(CommandSource target, String question, QuestionerCallback callback, int timeout) throws QuestionerStillActiveException
    {
        add(target, question, callback, timeout, null);
    }

    public static void add(CommandSource target, String question, QuestionerCallback callback) throws QuestionerStillActiveException
    {
        add(target, question, callback, DEFAULT_TIMEOUT);
    }

    public static void addChecked(CommandSource target, String question, QuestionerCallback callback, int timeout, CommandSource source)
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

    public static void addChecked(CommandSource target, String question, QuestionerCallback callback, int timeout)
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

    public static void addChecked(CommandSource target, String question, QuestionerCallback callback) throws QuestionerStillActiveException.CommandException
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

    public static synchronized void answer(CommandSource target, Boolean answer) throws CommandException
    {
        QuestionData question = questions.remove(target);
        if (question != null)
            question.doAnswer(answer);
        else
            ChatOutputHandler.chatError(target, Translator.translate("There is no question to answer!"));
    }

    public static synchronized void tick()
    {
        Iterator<Entry<CommandSource, QuestionData>> it = questions.entrySet().iterator();
        while (it.hasNext())
        {
            Entry<CommandSource, QuestionData> question = it.next();
            if (question.getValue().isTimeout())
            {
                it.remove();
                try
                {
                    question.getValue().doAnswer(null);
                }
                catch (CommandException e)
                {
                }

            }
        }
    }

    public static void cancel(CommandSource target) throws CommandException
    {
        answer(target, null);
    }

    public static void confirm(CommandSource target) throws CommandException
    {
        answer(target, true);
    }

    public static void deny(CommandSource target) throws CommandException
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
