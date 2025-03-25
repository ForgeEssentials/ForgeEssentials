package com.forgeessentials.jscripting.wrapper.mc.event.entity.player;

import javax.annotation.Nonnull;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.forgeessentials.jscripting.fewrapper.fe.JsPoint;
import com.forgeessentials.jscripting.wrapper.mc.item.JsItemStack;
import com.forgeessentials.jscripting.wrapper.mc.world.JsWorld;

public class JsPlayerInteractEvent extends JsPlayerEvent<PlayerInteractEvent>
{

    @SubscribeEvent
    public final void _handle(PlayerInteractEvent event)
    {
        _callEvent(event);
    }

    /**
     * @return The hand involved in this interaction. Will never be null.
     */
    @Nonnull
    public int getHand()
    {
        return _event.getHand().ordinal();
    }

    /**
     * @return The itemstack involved in this interaction, {@code ItemStack.EMPTY} if the hand was empty.
     */
    @Nonnull
    public JsItemStack getItemStack()
    {
        return JsItemStack.get(_event.getItemStack());
    }

    /**
     * If the interaction was on an entity, will be a BlockPos centered on the entity.
     * If the interaction was on a block, will be the position of that block.
     * Otherwise, will be a BlockPos centered on the player.
     * Will never be null.
     * @return The position involved in this interaction.
     */
    @Nonnull
    public JsPoint getPos()
    {
        BlockPos pos = _event.getPos();
        return new JsPoint(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * @return The face involved in this interaction. For all non-block interactions, this will return -1.
     */
    public int getFace()
    {
        EnumFacing face = _event.getFace();
        return face != null ? face.ordinal() : -1;
    }

    /**
     * @return Convenience method to get the world of this interaction.
     */
    public JsWorld getWorld()
    {
        return JsWorld.get(_event.getWorld());
    }
}
