package com.forgeessentials.economy;

import com.forgeessentials.api.IEconManager;
import com.forgeessentials.core.data.DataManager;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.UUID;

/**
 * Call these methods to modify a target's Wallet.
 */
public class WalletHandler implements IEconManager {
    
    private static ClassContainer con = new ClassContainer(Wallet.class);
    private static HashMap<UUID, Wallet> wallets = new HashMap<UUID, Wallet>();

    @Override
    public void addToWallet(int amountToAdd, UUID player)
    {
        wallets.get(player).amount = wallets.get(player).amount + amountToAdd;
    }

    @Override
    public int getWallet(UUID player)
    {
        return wallets.get(player).amount;
    }

    @Override
    public void removeFromWallet(int amountToSubtract, UUID player)
    {
        if (wallets.get(player).amount - amountToSubtract >= 0)
        {
            wallets.get(player).amount = wallets.get(player).amount - amountToSubtract;
        }
    }

    @Override
    public void setWallet(int setAmount, EntityPlayer player)
    {
        wallets.get(player.getUniqueID()).amount = setAmount;
    }

    @Override
    public String currency(int amount)
    {
        if (amount == 1)
        {
            return ModuleEconomy.currencySingular;
        }
        else
        {
            return ModuleEconomy.currencyPlural;
        }
    }

    @Override
    public String getMoneyString(UUID username)
    {
        int am = getWallet(username);
        return am + " " + currency(am);
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        Wallet wallet = DataManager.getInstance().load(Wallet.class, event.player.getUniqueID().toString());
        if (wallet == null)
            wallet = (Wallet) DataStorageManager.getReccomendedDriver().loadObject(con, event.player.getUniqueID().toString());
        if (wallet == null)
        {
            wallet = new Wallet(event.player, ModuleEconomy.startbudget);
        }
        wallets.put(event.player.getUniqueID(), wallet);
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (wallets.containsKey(event.player.getUniqueID()))
        {
            Wallet wallet = wallets.remove(event.player.getUniqueID());
            DataManager.getInstance().save(wallet, wallet.getUsername());
            DataStorageManager.getReccomendedDriver().saveObject(con, wallet);
        }
    }
}
