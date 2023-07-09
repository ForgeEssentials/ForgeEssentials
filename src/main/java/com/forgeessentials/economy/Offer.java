package com.forgeessentials.economy;

import net.minecraft.entity.player.ServerPlayerEntity;

/**
 * Represents an offer to transact an object.
 * 
 * @param <T>
 *            type of item to transact
 */
public class Offer<T>
{

    public ServerPlayerEntity seller;

    public ServerPlayerEntity buyer;

    public T item;

    public long price;

    public Offer(ServerPlayerEntity buyer, ServerPlayerEntity seller, T item, long price)
    {
        this.seller = seller;
        this.buyer = buyer;
        this.item = item;
        this.price = price;
    }

}
