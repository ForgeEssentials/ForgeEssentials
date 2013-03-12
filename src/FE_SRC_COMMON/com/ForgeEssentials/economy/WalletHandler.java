package com.ForgeEssentials.economy;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.data.ClassContainer;
import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.tickets.Ticket;
import com.ForgeEssentials.util.Localization;

import cpw.mods.fml.common.IPlayerTracker;

/**
 * Call these methods to modify a target's Wallet.
 */
public class WalletHandler implements IPlayerTracker
{
	private static ClassContainer			con	= new ClassContainer(Wallet.class);
	private static HashMap<String, Wallet> 	wallets = new HashMap<String, Wallet>();
	
	/**
	 * Add a set amount to a target's Wallet
	 * @param amountToAdd
	 * the amount to add to the Wallet
	 * @param player
	 * target player
	 */
	public static void addToWallet(int amountToAdd, EntityPlayer player)
	{
		wallets.get(player.username).amount = wallets.get(player.username).amount + amountToAdd;
	}

	/**
	 * Returns the size of the target's Wallet
	 * @param player
	 * target player
	 * @return the size of the target's Wallet
	 */
	public static int getWallet(EntityPlayer player)
	{
		return wallets.get(player.username).amount;
	}

	/**
	 * Remove a set amount from a target's Wallet
	 * @param amountToSubtract
	 * the amount to remove from the Wallet
	 * @param player
	 * target player
	 */
	public static void removeFromWallet(int amountToSubtract, EntityPlayer player)
	{
		wallets.get(player.username).amount = wallets.get(player.username).amount - amountToSubtract;
	}

	/**
	 * Set the target's Wallet to the specified amount
	 * @param setAmount
	 * amount to set the Wallet to
	 * @param player
	 * target player
	 */
	public static void setWallet(int setAmount, EntityPlayer player)
	{ 
		wallets.get(player.username).amount = setAmount;
	}

	/**
	 * Gets the singular or plural term of the currency used
	 * @param setAmount
	 * amount to set the Wallet to
	 * @param player
	 * target player
	 * @return singular or plural term of the currency used
	 */
	public static String currency(int amount)
	{
		if (amount == 1)
			return ConfigEconomy.currencySingular;
		else
			return ConfigEconomy.currencyPlural;
	}
	
	/**
	 * Gets a combo of getWallet + currency
	 * @param player
	 * @return returns 'amount' 'currency'
	 */
	public static String getMoneyString(EntityPlayer player)
	{
		int am = getWallet(player);
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
		wallets.put(wallet.getUsername(), wallet);
	}

	@Override
	public void onPlayerLogout(EntityPlayer player)
	{
		if(wallets.containsKey(player.username))
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
