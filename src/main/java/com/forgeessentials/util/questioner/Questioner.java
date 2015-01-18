package com.forgeessentials.util.questioner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.command.ICommandSender;

import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class Questioner extends ServerEventHandler {
    
    private static Map<String, QuestionData> queue = new HashMap<String, QuestionData>();
    
    private static ArrayList<String> removeQueue = new ArrayList<String>();
    
    private static ArrayList<String> playerQueue = new ArrayList<String>();

    public static int defaultTime = 120;

    public static void addToQuestionQueue(QuestionData question)
    {
        queue.put(question.getTarget().getCommandSenderName(), question);
    }

    public static void addtoQuestionQueue(ICommandSender target, String question, IReplyHandler runnable)
    {
        addToQuestionQueue(new QuestionData(target, question, runnable, defaultTime));
    }

    public static void abort(QuestionData questionData)
    {
        removeQueue.add(questionData.getTarget().getCommandSenderName());
    }

    public static void questionDone(QuestionData questionData)
    {
        removeQueue.add(questionData.getTarget().getCommandSenderName());
    }

    public Questioner()
    {
        super();
        new CommandQuestioner(true).register();
        new CommandQuestioner(false).register();
    }

    @SubscribeEvent
    public void tickStart(TickEvent.ServerTickEvent e)
    {
        for (QuestionData data : queue.values())
        {
            data.count();
        }
        for (String name : removeQueue)
        {
            queue.remove(name);
            playerQueue.remove(name);
        }
        removeQueue.clear();

    }

    public static void processAnswer(ICommandSender player, boolean affirmative)
    {
        if (playerQueue.contains(player.getCommandSenderName()))
        {
            for (Object dataObject : queue.values())
            {
                QuestionData data = (QuestionData) dataObject;
                data.doAnswer(affirmative);
            }
        }
    }

    public static interface IReplyHandler
    {
        public void replyReceived(boolean status);
    }

}
