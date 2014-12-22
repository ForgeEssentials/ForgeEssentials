package com.forgeessentials.economy;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Represents an offer to transact an object.
 * @param <T> type of item to transact
 */
public class Offer<T>
{
    public EntityPlayer buyer;
    public EntityPlayer seller;
    public T item;
    public int price;

    public Offer(EntityPlayer buyer, EntityPlayer seller, T item, int price)
    {
        this.buyer = buyer;
        this.seller = seller;
        this.item = item;
        this.price = price;
    }
}
