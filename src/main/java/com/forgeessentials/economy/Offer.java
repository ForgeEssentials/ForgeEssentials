package com.forgeessentials.economy;

import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Represents an offer to transact an object.
 * 
 * @param <T>
 *            type of item to transact
 */
public class Offer<T>
{

    public EntityPlayerMP seller;

    public EntityPlayerMP buyer;

    public T item;

    public long price;

    public Offer(EntityPlayerMP buyer, EntityPlayerMP seller, T item, long price)
    {
        this.seller = seller;
        this.buyer = buyer;
        this.item = item;
        this.price = price;
    }

}
