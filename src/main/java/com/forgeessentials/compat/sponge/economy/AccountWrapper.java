package com.forgeessentials.compat.sponge.economy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.spongepowered.api.event.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.account.VirtualAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransferResult;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.economy.ModuleEconomy;

import net.kyori.adventure.text.Component;

public class AccountWrapper implements UniqueAccount, VirtualAccount
{
    private UserIdent ident;

    public AccountWrapper(UserIdent ident)
    {
        this.ident = ident;
    }

    @Override
    public Component displayName()
    {
        return Component.text(ident.getUsername());
    }

    @Override
    public BigDecimal defaultBalance(Currency currency)
    {
        return new BigDecimal(APIRegistry.perms.getGlobalPermissionProperty(ModuleEconomy.PERM_STARTBUDGET));
    }

    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts)
    {
        return APIRegistry.economy.getWallet(ident).get() >= 0;
    }

    @Override
    public BigDecimal balance(Currency currency, Set<Context> contexts)
    {
        if (currency instanceof FECurrency)
            return new BigDecimal(APIRegistry.economy.getWallet(ident).get());
        else
            return new BigDecimal(0);
    }

    @Override
    public Map<Currency, BigDecimal> balances(Set<Context> contexts)
    {
        Map<Currency, BigDecimal> returned = new HashMap<>();
        returned.put(new FECurrency(), balance(new FECurrency(), contexts));
        return returned;
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Set<Context> contexts)
    {
        if (currency instanceof FECurrency)
            APIRegistry.economy.getWallet(ident).set(amount.longValue());
        return new FETransaction(this, currency, amount, contexts, ResultType.SUCCESS, "resetacc");
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Set<Context> contexts)
    {
        Map<Currency, TransactionResult> returned = new HashMap<>();
        Currency currency = new FECurrency();
        APIRegistry.economy.getWallet(ident).set(defaultBalance(currency).longValue());
        returned.put(currency, new FETransaction(this, currency,
                new BigDecimal(APIRegistry.economy.getWallet(ident).get()), contexts, ResultType.SUCCESS, "resetacc"));
        return returned;
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Set<Context> contexts)
    {
        if (currency instanceof FECurrency)
        {
            APIRegistry.economy.getWallet(ident).set(defaultBalance(currency).longValue());
            return new FETransaction(this, currency, new BigDecimal(APIRegistry.economy.getWallet(ident).get()),
                    contexts, ResultType.SUCCESS, "resetacc");
        }
        return new FETransaction(this, currency, new BigDecimal(APIRegistry.economy.getWallet(ident).get()), contexts,
                ResultType.FAILED, "resetacc");
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Set<Context> contexts)
    {
        if (currency instanceof FECurrency)
        {
            APIRegistry.economy.getWallet(ident).add(amount.longValue());
            return new FETransaction(this, currency, amount, contexts, ResultType.SUCCESS, "depositacc");
        }
        return new FETransaction(this, currency, amount, contexts, ResultType.FAILED, "depositacc");
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Set<Context> contexts)
    {

        if (currency instanceof FECurrency)
        {
            if (APIRegistry.economy.getWallet(ident).withdraw(amount.longValue()))
            {
                return new FETransaction(this, currency, amount, contexts, ResultType.SUCCESS, "withdrawacc");
            }
            else
            {
                return new FETransaction(this, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, "withdrawacc");
            }

        }
        return new FETransaction(this, currency, amount, contexts, ResultType.FAILED, "withdrawacc");
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Set<Context> contexts)
    {
        if (currency instanceof FECurrency)
        {
            if (APIRegistry.economy.getWallet(ident).withdraw(amount.longValue()))
            {
                to.deposit(currency, amount, contexts);
                return new FETransaction(this, currency, amount, contexts, ResultType.SUCCESS, "transferacc");
            }
            else
            {
                return new FETransaction(this, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, "transferacc");
            }

        }
        return new FETransaction(this, currency, amount, contexts, ResultType.FAILED, "transferacc");
    }

    @Override
    public String identifier()
    {
        return ident.getUuid().toString();
    }

    @Override
    public UUID uniqueId()
    {
        return ident.getUuid();
    }

    public class FETransaction implements TransferResult
    {

        private final Account account;
        private final Currency currency;
        private final BigDecimal amount;
        private final Set<Context> contexts;
        private final ResultType result;
        private final TransactionType type;

        // will be null for non-transfer transactions
        private final Account recipient;

        /**
         * non-transfer
         * 
         * @param account
         * @param currency
         * @param amount
         * @param contexts
         * @param result
         * @param type
         */
        public FETransaction(Account account, Currency currency, BigDecimal amount, Set<Context> contexts,
                ResultType result, String type)
        {
            this.account = account;
            this.currency = currency;
            this.amount = amount;
            this.contexts = contexts;
            this.result = result;
            this.type = new FETransType(type);
            this.recipient = null;
        }

        /**
         * transfer
         * 
         * @param account
         * @param currency
         * @param amount
         * @param contexts
         * @param result
         * @param type
         */
        public FETransaction(Account account, Currency currency, BigDecimal amount, Set<Context> contexts,
                ResultType result, String type, Account recipient)
        {
            this.account = account;
            this.currency = currency;
            this.amount = amount;
            this.contexts = contexts;
            this.result = result;
            this.type = new FETransType(type);
            this.recipient = recipient;

        }

        @Override
        public Account account()
        {
            return account;
        }

        @Override
        public Currency currency()
        {
            return currency;
        }

        @Override
        public BigDecimal amount()
        {
            return amount;
        }

        @Override
        public Set<Context> contexts()
        {
            return contexts;
        }

        @Override
        public ResultType result()
        {
            return result;
        }

        @Override
        public TransactionType type()
        {
            return type;
        }

        @Override
        public Account accountTo()
        {
            return recipient;
        }
    }

    public class FETransType implements TransactionType
    {
        private final String name;

        public FETransType(String name)
        {
            this.name = name;

        }

        public String getId()
        {
            return "fe:" + name;
        }

        public String getName()
        {
            return name;
        }
    }

    @Override
    public boolean hasBalance(Currency currency, Cause cause)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public BigDecimal balance(Currency currency, Cause cause)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<Currency, BigDecimal> balances(Cause cause)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
