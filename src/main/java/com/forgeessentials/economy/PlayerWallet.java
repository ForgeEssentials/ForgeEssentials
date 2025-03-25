package com.forgeessentials.economy;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.economy.Wallet;

public class PlayerWallet implements Wallet
{

    private long amount;

    private double fraction;

    public PlayerWallet(long amount)
    {
        this.amount = amount;
    }

    @Override
    public long get()
    {
        return amount;
    }

    @Override
    public void set(long value)
    {
        this.amount = value;
    }

    @Override
    public boolean covers(long value)
    {
        if (value < 0 || amount < value)
            return false;
        return true;
    }

    @Override
    public boolean withdraw(long value)
    {
        if (value < 0 || amount < value)
            return false;
        amount -= value;
        return true;
    }

    @Override
    public void add(long amount)
    {
        this.amount += amount;
    }

    @Override
    public void add(double amount)
    {
        this.fraction += amount;
        long rest = (long) fraction;
        this.fraction -= rest;
        this.amount += rest;
    }

    @Override
    public String toString()
    {
        return APIRegistry.economy.toString(amount);
    }

}
