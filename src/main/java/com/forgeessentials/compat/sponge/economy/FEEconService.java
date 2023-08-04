package com.forgeessentials.compat.sponge.economy;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.AccountDeletionResultType;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.account.VirtualAccount;

import com.forgeessentials.api.UserIdent;

public class FEEconService implements EconomyService
{
    @Override
    public Currency defaultCurrency()
    {
        return new FECurrency();
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
    public Optional<UniqueAccount> findOrCreateAccount(UUID uuid)
    {
        return Optional.of(new AccountWrapper(UserIdent.get(uuid)));
    }

    @Override
    public Optional<Account> findOrCreateAccount(String identifier)
    {
        return Optional.of(new AccountWrapper(UserIdent.get(identifier)));
    }

    @Override
    public AccountDeletionResultType deleteAccount(UUID uuid)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountDeletionResultType deleteAccount(String identifier)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Stream<UniqueAccount> streamUniqueAccounts()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Stream<VirtualAccount> streamVirtualAccounts()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<UniqueAccount> uniqueAccounts()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<VirtualAccount> virtualAccounts()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
