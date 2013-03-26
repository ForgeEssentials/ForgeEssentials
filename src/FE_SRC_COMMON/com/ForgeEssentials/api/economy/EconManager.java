package com.ForgeEssentials.api.economy;

import net.minecraft.entity.player.EntityPlayer;

public abstract class EconManager
{
	public static IEconManager	manager;

	private EconManager()
	{
		// never used
	}

	public static void addToWallet(int amountToAdd, String player)
	{
		manager.addToWallet(amountToAdd, player);
	}

	public static int getWallet(String player)
	{
		return manager.getWallet(player);
	}

	public static void removeFromWallet(int amountToSubtract, String player)
	{
		manager.removeFromWallet(amountToSubtract, player);
	}

	public static void setWallet(int setAmount, EntityPlayer player)
	{
		manager.setWallet(setAmount, player);
	}

	public static String currency(int amount)
	{
		return manager.currency(amount);
	}

	public static String getMoneyString(String username)
	{
		return manager.getMoneyString(username);
	}

}
