package com.forgeessentials.economy;

import net.minecraft.server.level.ServerPlayer;

/**
 * Represents an offer to transact an object.
 * 
 * @param <T>
 *            type of item to transact
 */
public class Offer<T>
{

    public ServerPlayer seller;

    public ServerPlayer buyer;

    public T item;

    public long price;

    public Offer(ServerPlayer buyer, ServerPlayer seller, T item, long price)
    {
        this.seller = seller;
        this.buyer = buyer;
        this.item = item;
        this.price = price;
    }

}
