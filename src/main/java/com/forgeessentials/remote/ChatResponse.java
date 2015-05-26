package com.forgeessentials.remote;

import java.util.Date;

public class ChatResponse
{

    public String sender;

    public String message;

    public Date timestamp;

    public ChatResponse(String sender, String message)
    {
        this.sender = sender;
        this.message = message;
        this.timestamp = new Date();
    }

}
