package com.ForgeEssentials.api.snooper;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

import com.ForgeEssentials.api.json.JSONException;
import com.ForgeEssentials.api.json.JSONObject;

/**
 * Event triggered when the snooper gets a vote from a service.
 *
 * Note that the player might be offline or not even play on the server.
 * @author Dries007
 */

@Cancelable
public class VoteEvent extends Event
{  
	public String	player;
	public String	serviceName;
	public String	ip;
	public String	timeStamp;
	private boolean	sane		= false;
	List<String>	feedback	= new ArrayList<String>();

	public VoteEvent(String player, String serviceName, String ip, String timeStamp)
	{
		this.player = player;
		this.serviceName = serviceName;
		this.ip = ip;
		this.timeStamp = timeStamp;
		sane = true;
	}

	public VoteEvent(String decoded)
	{
		try
		{
			JSONObject json = new JSONObject(decoded);
			player = json.getString("player");
			serviceName = json.getString("serviceName");
			ip = json.getString("ip");
			timeStamp = json.getJSONObject("timeStamp").getString("date");
			sane = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public String toString()
	{
	    try
        {
            JSONObject json = new JSONObject();
            json.put("player", player);
            json.put("serviceName", serviceName);
            json.put("ip", ip);
            json.put("ip", ip);
            json.put("timeStamp", new JSONObject().put("date", timeStamp));
            return json.toString();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
	    return "";
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
