package com.forgeessentials.jscripting.fewrapper.fe;


import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.jscripting.wrapper.JsWrapper;

public class JsWallet extends JsWrapper<Wallet>
{
    public JsWallet(Wallet that)
    {
        super(that);
    }

    public long get()
    {
        return that.get();
    }

    public void set(long value)
    {
        that.set(value);
    }

    public void add(long amount)
    {
        that.add(amount);
    }

    public void add(double amount)
    {
        that.add(amount);
    }

    public boolean covers(long value)
    {
        return that.covers(value);
    }

    public boolean withdraw(long value)
    {
        return that.withdraw(value);
    }

    @Override
    public String toString()
    {
        return that.toString();
    }
}
