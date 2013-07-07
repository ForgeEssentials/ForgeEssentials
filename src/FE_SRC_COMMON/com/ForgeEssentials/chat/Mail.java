package com.ForgeEssentials.chat;

import java.rmi.server.UID;

import com.ForgeEssentials.api.data.IReconstructData;
import com.ForgeEssentials.api.data.SaveableObject;
import com.ForgeEssentials.api.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.api.data.SaveableObject.SaveableField;
import com.ForgeEssentials.api.data.SaveableObject.UniqueLoadingKey;

@SaveableObject
public class Mail
{
	@UniqueLoadingKey
	@SaveableField
	private String	key;

	@SaveableField
	private String	sender;

	@SaveableField
	private String	receiver;

	@SaveableField
	private String	message;

	public Mail(String key, String sender, String receiver, String message)
	{
		if (key.equals(""))
		{
			this.key = new UID().toString().replaceAll(":", "_");
		}
		else
		{
			this.key = key;
		}
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
	}

	public String getKey()
	{
		return key;
	}

	public String getSender()
	{
		return sender;
	}

	public String getReceiver()
	{
		return receiver;
	}

	public String getMessage()
	{
		return message;
	}

	@Reconstructor
	private static Mail reconstruct(IReconstructData tag)
	{
		return new Mail((String) tag.getFieldValue("key"), (String) tag.getFieldValue("sender"), (String) tag.getFieldValue("receiver"), (String) tag.getFieldValue("message"));
	}
}
