package com.ForgeEssentials.api;

import net.minecraft.entity.player.EntityPlayer;

public interface IEconManager
{

	/**
	 * Add a set amount to a target's Wallet
	 * @param amountToAdd
	 * @param player
	 */
	public void addToWallet(int amountToAdd, String player);

	/**
	 * get the ammount of money the player has
	 * @param player
	 * @return
	 */
	public int getWallet(String player);

	/**
	 * Remove a set amount from a target's Wallet
	 * @param amountToSubtract
	 * @param player
	 */
	public void removeFromWallet(int amountToSubtract, String player);

	/**
	 * Set the target's Wallet to the specified amount
	 * @param setAmount
	 * @param player
	 */
	public void setWallet(int setAmount, EntityPlayer player);

	/**
	 * Gets the singular or plural term of the currency used
	 * @param setAmount
	 * @param player
	 */
	public String currency(int amount);

	/**
	 * Gets a combo of getWallet + currency
	 * @param player
	 * @return returns 'amount' 'currency'
	 */
	public String getMoneyString(String username);
}
