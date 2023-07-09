package com.forgeessentials.remote.network;

import com.forgeessentials.remote.RemoteMessageID;

public class ChatResponse
{

    public static final String ID = RemoteMessageID.CHAT;

    public String sender;

    public Object message;

    public long timestamp;

    public ChatResponse(String sender, Object message)
    {
        this.sender = sender;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

}
