package com.forgeessentials.questioner;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

public class QuestionCenter implements IScheduledTickHandler
{
	private static Map<String, QuestionData>	queue				= new HashMap<String, QuestionData>();
	private static ArrayList<String>			removeQueue		= new ArrayList<String>();
	private static ArrayList<String> 			playerQueue		= new ArrayList<String>();
	
	public static int defaultTime = 120;
	public static int defaultInterval = 30;

	public static void addToQuestionQue(QuestionData question)
	{
		queue.put(question.getTarget().username, question);
	}

	public static void abort(QuestionData questionData)
	{
		removeQueue.add(questionData.getTarget().username);
	}

	public static void questionDone(QuestionData questionData)
	{
		removeQueue.add(questionData.getTarget().username);
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		for (QuestionData data : queue.values())
		{
			data.count();
		}
		for(String name : removeQueue)
		{
			queue.remove(name);
			playerQueue.remove(name);
		}
		removeQueue.clear();
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		// Not needed here
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel()
	{
		return "QuestionCenter";
	}

	@Override
	public int nextTickSpacing()
	{
		return 20;
	}
	
	public static void processAnswer(EntityPlayer player, boolean affirmative)
	{
		if(playerQueue.contains(player.username))
		{
			for(Object dataObject : queue.values())
			{
				QuestionData data = (QuestionData)dataObject;
				data.doAnswer(affirmative);
			}
		}
	}

}
