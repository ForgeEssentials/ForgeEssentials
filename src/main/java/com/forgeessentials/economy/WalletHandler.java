package com.forgeessentials.economy;

import com.forgeessentials.api.IEconManager;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;

/**
 * Call these methods to modify a target's Wallet.
 */
public class WalletHandler implements IEconManager {
    private static ClassContainer con = new ClassContainer(Wallet.class);
    private static HashMap<String, Wallet> wallets = new HashMap<String, Wallet>();

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
        if (wallets.get(player).amount - amountToSubtract >= 0)
        {
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
        {
            return ConfigEconomy.currencySingular;
        }
        else
        {
            return ConfigEconomy.currencyPlural;
        }
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

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        Wallet wallet = (Wallet) DataStorageManager.getReccomendedDriver().loadObject(con, player.username);
        if (wallet == null)
        {
            wallet = new Wallet(event.player, ModuleEconomy.startbuget);
        }
        wallets.put(player.username, wallet);
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent)
    {
        if (wallets.containsKey(player.username))
        {
            DataStorageManager.getReccomendedDriver().saveObject(con, wallets.remove(player.username));
        }
    }
}
