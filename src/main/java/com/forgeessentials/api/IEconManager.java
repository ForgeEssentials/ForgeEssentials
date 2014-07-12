package com.forgeessentials.api;

import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public interface IEconManager {

    /**
     * Add a set amount to a target's Wallet
     *
     * @param amountToAdd
     * @param player
     */
    public void addToWallet(int amountToAdd, UUID player);

    /**
     * get the ammount of money the player has
     *
     * @param player
     * @return
     */
    public int getWallet(UUID player);

    /**
     * Remove a set amount from a target's Wallet
     *
     * @param amountToSubtract
     * @param player
     */
    public void removeFromWallet(int amountToSubtract, UUID player);

    /**
     * Set the target's Wallet to the specified amount
     *
     * @param setAmount
     * @param player
     */
    public void setWallet(int setAmount, EntityPlayer player);

    /**
     * Gets the singular or plural term of the currency used
     *
     * @param amount
     */
    public String currency(int amount);

    /**
     * Gets a combo of getWallet + currency
     *
     * @param username
     * @return returns 'amount' 'currency'
     */
    public String getMoneyString(UUID username);
}
