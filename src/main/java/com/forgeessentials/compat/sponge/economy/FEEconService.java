package com.forgeessentials.compat.sponge.economy;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.account.VirtualAccount;

import com.forgeessentials.api.UserIdent;

public class FEEconService implements EconomyService
{
    @Override
    public Currency getDefaultCurrency()
    {
        return new FECurrency();
    }

    @Override
    public Set<Currency> getCurrencies()
    {
        Set<Currency> list = new HashSet();
        list.add(new FECurrency());
        return list;
    }

    @Override
    public boolean hasAccount(UUID uuid)
    {
        return false;
    }

    @Override
    public boolean hasAccount(String identifier)
    {
        return false;
    }

    @Override
    public Optional<UniqueAccount> getOrCreateAccount(UUID uuid)
    {
        return Optional.of(new AccountWrapper(UserIdent.get(uuid)));
    }

    @Override
    public Optional<Account> getOrCreateAccount(String identifier)
    {
        return Optional.of(new AccountWrapper(UserIdent.get(identifier)));
    }

    @Override
    public void registerContextCalculator(ContextCalculator<Account> calculator)
    {

    }
}
