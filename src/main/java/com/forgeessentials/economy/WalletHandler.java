package com.forgeessentials.economy;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.api.IEconManager;
import com.forgeessentials.data.v2.DataManager;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

/**
 * Call these methods to modify a target's Wallet.
 */
public class WalletHandler implements IEconManager {

    private static HashMap<UUID, Wallet> wallets = new HashMap<UUID, Wallet>();

    @Override
    public void addToWallet(long amountToAdd, UUID player)
    {
        wallets.get(player).amount = wallets.get(player).amount + amountToAdd;
    }

    @Override
    public long getWallet(UUID player)
    {
        return wallets.get(player).amount;
    }

    @Override
    public boolean removeFromWallet(long amountToSubtract, UUID player)
    {
        if (wallets.get(player).amount - amountToSubtract >= 0)
        {
            wallets.get(player).amount = wallets.get(player).amount - amountToSubtract;
            return true;
        }
        return false;
    }

    @Override
    public void setWallet(long setAmount, EntityPlayer player)
    {
        wallets.get(player.getUniqueID()).amount = setAmount;
    }

    @Override
    public String currency(long amount)
    {
        return amount == 1 ? ModuleEconomy.currencySingular : ModuleEconomy.currencyPlural;
    }

    @Override
    public String getMoneyString(UUID username)
    {
        return ModuleEconomy.formatCurrency(getWallet(username));
    }

    @Override
    public void save()
    {
        for (Entry<UUID, Wallet> wallet : wallets.entrySet())
            DataManager.getInstance().save(wallet.getValue(), wallet.getKey().toString());
    }

    @Override
    public Map<String, Integer> getItemTables()
    {
        return ModuleEconomy.tables.valueMap;
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        Wallet wallet = DataManager.getInstance().load(Wallet.class, event.player.getUniqueID().toString());
        if (wallet == null)
        {
            wallet = new Wallet(ModuleEconomy.startbudget);
            // if (event.player.getEntityData().hasKey("FE-Economy"))
            // wallet.amount = event.player.getEntityData().getInteger("FE-Economy");
        }
        wallets.put(event.player.getUniqueID(), wallet);
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        Wallet wallet = wallets.remove(event.player.getUniqueID());
        if (wallet != null)
            DataManager.getInstance().save(wallet, event.player.getUniqueID().toString());
    }
}
