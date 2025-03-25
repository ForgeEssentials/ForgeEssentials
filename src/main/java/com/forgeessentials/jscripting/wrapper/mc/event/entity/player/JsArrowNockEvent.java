package com.forgeessentials.jscripting.wrapper.mc.event.entity.player;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.forgeessentials.jscripting.wrapper.mc.item.JsItemStack;
import com.forgeessentials.jscripting.wrapper.mc.world.JsWorld;

public class JsArrowNockEvent extends JsPlayerEvent<ArrowNockEvent>
{

    @SubscribeEvent
    public final void _handle(ArrowNockEvent event)
    {
        _callEvent(event);
    }

    public JsItemStack getBow() {
        return JsItemStack.get(_event.getBow());
    }
    public JsWorld getWorld() {
        return JsWorld.get(_event.getWorld());
    }
    public int getHand() { return _event.getHand().ordinal(); }
    public boolean hasAmmo() { return _event.hasAmmo(); }
    public ActionResult<JsItemStack> getAction()
    {
        ActionResult<ItemStack> action = _event.getAction();
        return ActionResult.newResult(action.getType(),JsItemStack.get(action.getResult()));
    }

    public void setAction(ActionResult<JsItemStack> action)
    {
        _event.setAction(ActionResult.newResult(action.getType(), action.getResult().getThat()));
    }
}
