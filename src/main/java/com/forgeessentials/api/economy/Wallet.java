package com.forgeessentials.api.economy;

public interface Wallet {

    public long get();

    public void set(long value);

    public void add(long amount);

    public void add(double amount);

    public boolean withdraw(long value);

    /**
     * Get the amount in this wallet described as string together with the currency
     * 
     * @return Returns the amount in this wallet described as string together with the currency
     */
    @Override
    public String toString();

}
