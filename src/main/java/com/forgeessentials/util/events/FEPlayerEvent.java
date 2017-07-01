package com.forgeessentials.util.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * All events on this class are fired on the FE internal EventBus and are not cancellable.
 */
public class FEPlayerEvent extends PlayerEvent
{

    public FEPlayerEvent(EntityPlayer player)
    {
        super(player);
    }

    public EntityPlayerMP getPlayer()
    {
        return (EntityPlayerMP) getEntityPlayer();
    }

    /**
     * Fired when a player does not have PlayerInfo data, should modules need to do additional setup.
     */
    public static class NoPlayerInfoEvent extends FEPlayerEvent
    {

        public NoPlayerInfoEvent(EntityPlayer player)
        {
            super(player);
        }

    }

    /**
     * Fired when the AFK system declares a player has gone AFK. Thrown by commands module.
     */
    public static class PlayerAFKEvent extends FEPlayerEvent
    {
        public final boolean afk;

        public PlayerAFKEvent(EntityPlayer player, boolean afk)
        {
            super(player);
            this.afk = afk;
        }
    }

    public static class ClientHandshakeEstablished extends FEPlayerEvent
    {
        public ClientHandshakeEstablished(EntityPlayer player)
        {
            super(player);
        }
    }

    /**
     * Fired when an inventory group is changed. For custom inventory support.
     */
    public static class InventoryGroupChange extends FEPlayerEvent
    {
        String newInvGroupName;
        Map<String, List<ItemStack>> newInvGroup;

        public InventoryGroupChange(EntityPlayer player, String newInvGroupName, Map newInvGroup)
        {
            super(player);
            this.newInvGroup = newInvGroup;
            this.newInvGroupName = newInvGroupName;
        }

        public IItemHandlerModifiable swapInventory(String modname, IItemHandlerModifiable toSwap)
        {
            List<ItemStack> oldItems = new ArrayList<>();
            List<ItemStack> newItems = newInvGroup.getOrDefault(modname, new ArrayList<>());
            for (int slotIdx = 0; slotIdx < toSwap.getSlots(); slotIdx++)
            {
                oldItems.add(toSwap.getStackInSlot(slotIdx));
                if (newItems != null && slotIdx < newItems.size())
                {
                    toSwap.setStackInSlot(slotIdx, newItems.get(slotIdx));
                }
                else
                {
                    toSwap.setStackInSlot(slotIdx, null);
                }
            }
            newInvGroup.put(modname, oldItems);
            return toSwap;
        }
    }
}
