package com.forgeessentials.economy;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.api.IEconManager;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;

import cpw.mods.fml.common.IPlayerTracker;

/**
 * Call these methods to modify a target's Wallet.
 */
public class WalletHandler implements IPlayerTracker, IEconManager
{
	private static ClassContainer			con		= new ClassContainer(Wallet.class);
	private static HashMap<String, Wallet>	wallets	= new HashMap<String, Wallet>();

	@Override
	public void addToWallet(int amountToAdd, String player)
	{
		wallets.get(player).amount = wallets.get(player).amount + amountToAdd;
	}

	@Override
	public int getWallet(String player)
	{
		return wallets.get(player).amount;
	}

	@Override
	public void removeFromWallet(int amountToSubtract, String player)
	{
		if (wallets.get(player).amount - amountToSubtract >= 0){
		wallets.get(player).amount = wallets.get(player).amount - amountToSubtract;
		}
	}

	@Override
	public void setWallet(int setAmount, EntityPlayer player)
	{
		wallets.get(player.username).amount = setAmount;
	}

	@Override
	public String currency(int amount)
	{
		if (amount == 1)
			return ConfigEconomy.currencySingular;
		else
			return ConfigEconomy.currencyPlural;
	}

	@Override
	public String getMoneyString(String username)
	{
		int am = getWallet(username);
		return am + " " + currency(am);
	}

	/*
	 * Player tracker stuff
	 */

	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		Wallet wallet = (Wallet) DataStorageManager.getReccomendedDriver().loadObject(con, player.username);
		if (wallet == null)
		{
			wallet = new Wallet(player, ModuleEconomy.startbuget);
		}
		wallets.put(player.username, wallet);
	}

	@Override
	public void onPlayerLogout(EntityPlayer player)
	{
		if (wallets.containsKey(player.username))
		{
			DataStorageManager.getReccomendedDriver().saveObject(con, wallets.remove(player.username));
		}
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player)
	{
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player)
	{
	}
}
