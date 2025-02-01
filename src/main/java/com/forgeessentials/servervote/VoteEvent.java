package com.forgeessentials.servervote;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;




/**
 * Event triggered when the snooper gets a vote from a service.
 * <p/>
 * Note that the player might be offline or not even play on the server.
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
    List<String> feedback = new ArrayList<String>();
    private boolean sane = false;

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
            Gson gson = new Gson();
            JsonElement element = gson.fromJson(decoded, JsonElement.class);
            JsonObject json = element.getAsJsonObject();
            player = json.get("player").getAsString();
            serviceName = json.get("serviceName").getAsString();
            ip = json.get("ip").getAsString();
            timeStamp = json.get("timeStamp").getAsJsonObject().get("date").getAsString();
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
            JsonObject json = new JsonObject();
            json.add("player", new JsonPrimitive(player));
            json.add("serviceName", new JsonPrimitive(serviceName));
            json.add("ip", new JsonPrimitive(ip));

            JsonObject time = new JsonObject();
            time.add("date", new JsonPrimitive(timeStamp));

            json.add("timeStamp", time);
            return json.toString();
        }
        catch (JsonParseException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public boolean isSane()
    {
        return sane;
    }

    public List<String> getFeedback()
    {
        return feedback;
    }

    public void setFeedback(String text)
    {
        feedback.add(text);
    }
}
