package com.forgeessentials.chat;

import java.rmi.server.UID;
import java.util.UUID;

public class Mail {
    
    private String key;

    private String sender;

    private String receiver;

    private String message;

    public Mail(String key, UUID sender, UUID receiver, String message)
    {
        if (key == null || key.isEmpty())
            key = new UID().toString().replaceAll(":", "_");
        this.key = key;
        this.sender = sender.toString();
        this.receiver = receiver.toString();
        this.message = message;
    }

    public Mail(String key, String sender, String receiver, String message)
    {
        if (key == null || key.isEmpty())
            key = new UID().toString().replaceAll(":", "_");
        this.key = key;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public String getKey()
    {
        return key;
    }

    public UUID getSender()
    {
        return UUID.fromString(sender);
    }

    public UUID getReceiver()
    {
        return UUID.fromString(receiver);
    }

    public String getMessage()
    {
        return message;
    }
}
