package com.forgeessentials.questioner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class QuestionCenter {
    private static Map<UUID, QuestionData> queue = new HashMap<UUID, QuestionData>();
    private static ArrayList<UUID> removeQueue = new ArrayList<UUID>();
    private static ArrayList<UUID> playerQueue = new ArrayList<UUID>();

    public static int defaultTime = 120;
    public static int defaultInterval = 30;

    public static void addToQuestionQue(QuestionData question)
    {
        queue.put(question.getTarget().getPersistentID(), question);
    }

    public static void abort(QuestionData questionData)
    {
        removeQueue.add(questionData.getTarget().getPersistentID());
    }

    public static void questionDone(QuestionData questionData)
    {
        removeQueue.add(questionData.getTarget().getPersistentID());
    }
    @SubscribeEvent
    public void tickStart(TickEvent.ServerTickEvent e)
    {
        for (QuestionData data : queue.values())
        {
            data.count();
        }
        for (UUID name : removeQueue)
        {
            queue.remove(name);
            playerQueue.remove(name);
        }
        removeQueue.clear();

    }

    public static void processAnswer(EntityPlayer player, boolean affirmative)
    {
        if (playerQueue.contains(player.getPersistentID()))
        {
            for (Object dataObject : queue.values())
            {
                QuestionData data = (QuestionData) dataObject;
                data.doAnswer(affirmative);
            }
        }
    }

}
