package com.forgeessentials.chat;

import java.rmi.server.UID;
import java.util.UUID;

import com.forgeessentials.commons.IReconstructData;
import com.forgeessentials.commons.SaveableObject;
import com.forgeessentials.commons.SaveableObject.Reconstructor;
import com.forgeessentials.commons.SaveableObject.SaveableField;
import com.forgeessentials.commons.SaveableObject.UniqueLoadingKey;

@SaveableObject
public class Mail {
    @UniqueLoadingKey
    @SaveableField
    private String key;

    @SaveableField
    private String sender;

    @SaveableField
    private String receiver;

    @SaveableField
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

    @Reconstructor
    private static Mail reconstruct(IReconstructData tag)
    {
        return new Mail((String) tag.getFieldValue("key"), (String) tag.getFieldValue("sender"), (String) tag.getFieldValue("receiver"),
                (String) tag.getFieldValue("message"));
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
