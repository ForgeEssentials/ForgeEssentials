package com.forgeessentials.jscripting.wrapper.mc.event.entity.player;

import javax.annotation.Nonnull;

import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.forgeessentials.jscripting.wrapper.mc.item.JsItemStack;

public class JsAnvilRepairEvent extends JsPlayerEvent<AnvilRepairEvent>
{

    @SubscribeEvent
    public final void _handle(AnvilRepairEvent event)
    {
        _callEvent(event);
    }

    /**
     * Get the output result from the anvil
     * @return the output
     */
    @Nonnull
    public JsItemStack getItemResult() { return JsItemStack.get(_event.getItemResult()); }

    /**
     * Get the first item input into the anvil
     * @return the first input slot
     */
    @Nonnull
    public JsItemStack getItemInput() { return JsItemStack.get(_event.getItemInput()); }

    /**
     * Get the second item input into the anvil
     * @return the second input slot
     */
    @Nonnull
    public JsItemStack getIngredientInput() { return JsItemStack.get(_event.getIngredientInput()); }

    public float getBreakChance() { return _event.getBreakChance(); }
    public void setBreakChance(float breakChance) { _event.setBreakChance(breakChance); }
}
