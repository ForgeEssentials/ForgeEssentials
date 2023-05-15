package com.forgeessentials.util.questioner;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Iterator;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.util.events.FERegisterCommandsEvent;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.CommandDispatcher;

public class Questioner extends ServerEventHandler
{

    public static final String MSG_STILL_ACTIVE = "Error. There is still an unanswered question left";

    private static Map<PlayerEntity, QuestionData> questions = new HashMap<>();

    public static int DEFAULT_TIMEOUT = 120;

    public Questioner()
    {
        super();
    }
    
    @SubscribeEvent
    public void registerCommands(FERegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSource> dispatcher = event.getRegisterCommandsEvent().getDispatcher();
        FECommandManager.registerCommand(new CommandQuestionerYes(true), dispatcher);
        FECommandManager.registerCommand(new CommandQuestionerNo(true), dispatcher);
    }

    public static synchronized void add(QuestionData question) throws QuestionerException
    {
        if (questions.containsKey(question.getTarget()))
            throw new QuestionerException();
        questions.put(question.getTarget(), question);
        question.sendQuestion();
    }

    public static void add(PlayerEntity target, String question, QuestionerCallback callback, int timeout, PlayerEntity source) throws QuestionerException
    {
        add(new QuestionData(target, question, callback, timeout, source));
    }

    public static void add(PlayerEntity target, String question, QuestionerCallback callback, int timeout) throws QuestionerException
    {
        add(target, question, callback, timeout, null);
    }

    public static void add(PlayerEntity target, String question, QuestionerCallback callback) throws QuestionerException
    {
        add(target, question, callback, DEFAULT_TIMEOUT);
    }

    public static void addChecked(PlayerEntity target, String question, QuestionerCallback callback, int timeout, PlayerEntity source) throws QuestionerException.QuestionerStillActiveException
    {
        try
        {
            add(new QuestionData(target, question, callback, timeout, source));
        }
        catch (QuestionerException e)
        {
            throw new QuestionerException.QuestionerStillActiveException();
        }
    }

    public static void addChecked(PlayerEntity target, String question, QuestionerCallback callback, int timeout) throws QuestionerException.QuestionerStillActiveException
    {
        try
        {
            add(target, question, callback, timeout, null);
        }
        catch (QuestionerException e)
        {
            throw new QuestionerException.QuestionerStillActiveException();
        }
    }

    public static void addChecked(PlayerEntity target, String question, QuestionerCallback callback) throws QuestionerException.QuestionerStillActiveException
    {
        try
        {
            add(target, question, callback, DEFAULT_TIMEOUT);
        }
        catch (QuestionerException e)
        {
            throw new QuestionerException.QuestionerStillActiveException();
        }
    }

    public static synchronized void answer(PlayerEntity playerAnswering, Boolean answer) throws CommandException
    {
        QuestionData question = questions.remove(playerAnswering);
        if (question != null) {
            question.doAnswer(answer);
            ChatOutputHandler.chatConfirmation(playerAnswering, "Responded: "+ (answer ? "yes" : "no"));
        }
        else
            ChatOutputHandler.chatError(playerAnswering, "There is no question to answer!");
    }

    public static synchronized void tick()
    {
        Iterator<Entry<PlayerEntity, QuestionData>> it = questions.entrySet().iterator();
        while (it.hasNext())
        {
            Entry<PlayerEntity, QuestionData> question = it.next();
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

    public static void cancel(PlayerEntity target) throws CommandException
    {
        answer(target, null);
    }

    public static void confirm(PlayerEntity target) throws CommandException
    {
        answer(target, true);
    }

    public static void deny(PlayerEntity target) throws CommandException
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
