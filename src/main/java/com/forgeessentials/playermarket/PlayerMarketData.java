package com.forgeessentials.playermarket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.item.ItemStack;

public class PlayerMarketData
{

    public List<AuctionStack> itemsListed = new ArrayList<>();

    public static class AuctionStack
    {
        public ItemStack stack;
        public long price;
        public String sellerName;
        public UUID sellerId;
        public int timeout;
        public boolean hasTimeout;

        public AuctionStack copy()
        {
            AuctionStack newStack = new AuctionStack();
            newStack.stack = stack.copy();
            newStack.price = price;
            newStack.sellerName = sellerName;
            newStack.sellerId = sellerId;
            newStack.timeout = timeout;
            return newStack;
        }
    }

    /**
     * maxSize of the market.  Set to -1 to disable, or 54 to limit market to a single page!
     */
    public int marketSize = -1;
}
