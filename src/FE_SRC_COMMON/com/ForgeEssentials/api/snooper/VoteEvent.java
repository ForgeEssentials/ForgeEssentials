package com.ForgeEssentials.api.snooper;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

import com.ForgeEssentials.api.json.JSONObject;

/**
 * Event triggered when the snooper gets a vote from a service.
 * If you cancel the event, the service will receive "Failed".
 * 
 * @author Dries007
 */

@Cancelable
public class VoteEvent extends Event
{
	public String player;
	public String serviceName;
	public String ip;
	public String timeStamp;
	private boolean sane = false;
	List<String> feedback = new ArrayList();
	
	public VoteEvent(String player, String serviceName, String ip, String timeStamp)
	{
		this.player = player;
		this.serviceName = serviceName;
		this.ip = ip;
		this.timeStamp = timeStamp;
		this.sane = true;
	}
	
	public VoteEvent(String decoded)
	{
		try
		{
			JSONObject json = new JSONObject(decoded);
			this.player = json.getString("player");
			this.serviceName = json.getString("serviceName");
			this.ip = json.getString("ip");
			this.timeStamp = json.getJSONObject("timeStamp").getString("date");
			this.sane = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public String toString()
	{
		return player + "@" + ip + " by " + serviceName + "@" + timeStamp;
	}
	
	public boolean isSane()
	{
		return sane;
	}

	public void setFeedback(String text)
	{
		feedback.add(text);
	}
	
	public List<String> getFeedback()
	{
		return feedback;
	}
}
