package com.ForgeEssentials.economy;

import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.util.Localization;

/**
 * Call these methods to modify a target's wallet.
 */
public class Wallet
{
	/**
	 * Add a set amount to a target's wallet
	 * @param amountToAdd
	 * the amount to add to the wallet
	 * @param player
	 * target player
	 */
	public static void addToWallet(int amountToAdd, EntityPlayer player)
	{
		doesWalletExist(player);
		ModuleEconomy.getPlayerInfo(player).wallet = ModuleEconomy.getPlayerInfo(player).wallet + amountToAdd;
		ModuleEconomy.saveData(player);
	}

	/**
	 * Returns the size of the target's wallet
	 * @param player
	 * target player
	 * @return the size of the target's wallet
	 */
	public static int getWallet(EntityPlayer player)
	{
		doesWalletExist(player);
		return ModuleEconomy.getPlayerInfo(player).wallet;
	}

	/**
	 * Remove a set amount from a target's wallet
	 * @param amountToSubtract
	 * the amount to remove from the wallet
	 * @param player
	 * target player
	 */
	public static void removeFromWallet(int amountToSubtract, EntityPlayer player)
	{
		doesWalletExist(player);
		ModuleEconomy.getPlayerInfo(player).wallet = ModuleEconomy.getPlayerInfo(player).wallet - amountToSubtract;
		ModuleEconomy.saveData(player);
	}

	/**
	 * Checks if the player's wallet exists, if not set it to 0. Should only be
	 * called under special circumstances, FEE should do everything needed.
	 * @param player
	 * target player
	 */
	public static void doesWalletExist(EntityPlayer player)
	{
		if (!(ModuleEconomy.getPlayerInfo(player).wallet <= 0 || ModuleEconomy.getPlayerInfo(player).wallet >= 0))
		{
			ModuleEconomy.getPlayerInfo(player).wallet = 0;
			ModuleEconomy.saveData(player);
		}
	}

	/**
	 * Set the target's wallet to the specified amount
	 * @param setAmount
	 * amount to set the wallet to
	 * @param player
	 * target player
	 */
	public static void setWallet(int setAmount, EntityPlayer player)
	{
		doesWalletExist(player);
		ModuleEconomy.getPlayerInfo(player).wallet = setAmount;
		ModuleEconomy.saveData(player);
	}

	/**
	 * Gets the singular or plural term of the currency used
	 * @param setAmount
	 * amount to set the wallet to
	 * @param player
	 * target player
	 * @return singular or plural term of the currency used
	 */
	public static String currency(int amount)
	{
		if (amount == 1)
			return Localization.get(Localization.WALLET_CURRENCY_SINGULAR);
		else
			return Localization.get(Localization.WALLET_CURRENCY_PLURAL);
	}
}
