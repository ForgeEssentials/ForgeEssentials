package com.ForgeEssentials.serverVote.snooper;

import javax.crypto.Cipher;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.api.json.JSONException;
import com.ForgeEssentials.api.json.JSONObject;
import com.ForgeEssentials.api.snooper.Response;
import com.ForgeEssentials.api.snooper.VoteEvent;
import com.ForgeEssentials.serverVote.ModuleServerVote;
import com.ForgeEssentials.util.OutputHandler;

public class VoteResponce extends Response
{
	@Override
	public JSONObject getResponce(String input) throws JSONException
	{
		try
		{
			String decoded;

			try
			{
				Cipher cipher = Cipher.getInstance("RSA");
				cipher.init(Cipher.DECRYPT_MODE, ModuleServerVote.config.privateKey);
				byte[] decodedBytes = cipher.doFinal(input.getBytes());
				decoded = new String(decodedBytes);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return new JSONObject().put(this.getName(), "");
			}

			VoteEvent vote = new VoteEvent(decoded);

			if (!vote.isSane())
				return new JSONObject().put(this.getName(), "");

			OutputHandler.fine("Vote: " + vote);

			try
			{
				MinecraftForge.EVENT_BUS.post(vote);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return new JSONObject().put(this.getName(), "");
			}

			if (vote.isCanceled())
				return new JSONObject().put(this.getName(), "");
			else
				return new JSONObject().put(this.getName(), "");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getName()
	{
		return "VoteResponce";
	}

	@Override
	public void readConfig(String category, Configuration config)
	{

	}

	@Override
	public void writeConfig(String category, Configuration config)
	{

	}
}
