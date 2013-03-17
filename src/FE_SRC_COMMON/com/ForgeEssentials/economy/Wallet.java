package com.ForgeEssentials.economy;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.data.IReconstructData;
import com.ForgeEssentials.api.data.SaveableObject;
import com.ForgeEssentials.api.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.api.data.SaveableObject.SaveableField;
import com.ForgeEssentials.api.data.SaveableObject.UniqueLoadingKey;

@SaveableObject
public class Wallet
{
	@UniqueLoadingKey
	private String	username;

	@SaveableField
	public int		amount;

	public Wallet(EntityPlayer player, int startAmount)
	{
		if (player.getEntityData().hasKey("Economy-" + player.username))
		{
			startAmount = player.getEntityData().getInteger("Economy-" + player.username);
		}
		username = player.username;
		amount = startAmount;
	}

	private Wallet(Object username, Object amount)
	{
		this.username = (String) username;
		this.amount = (Integer) amount;
	}

	@Reconstructor
	private static Wallet reconstruct(IReconstructData tag)
	{
		return new Wallet(tag.getFieldValue("username"), tag.getFieldValue("amount"));
	}

	public String getUsername()
	{
		return username;
	}
}
