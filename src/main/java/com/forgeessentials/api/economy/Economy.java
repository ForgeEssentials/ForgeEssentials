package com.forgeessentials.api.economy;

import com.forgeessentials.api.UserIdent;

public interface Economy
{

    /**
     * Get the wallet for a player
     * 
     * @param player
     * @return
     */
    public Wallet getWallet(UserIdent player);

    /**
     * Gets the singular or plural term of the currency used
     *
     * @param amount
     * @return
     */
    public String currency(long amount);

    /**
     * Turns the amount into a string with the currency attached
     *
     * @param amount
     * @return
     */
    public String toString(long amount);

}
