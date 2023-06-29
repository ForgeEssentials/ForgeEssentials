package com.forgeessentials.jscripting.wrapper.mc.event.entity;

import com.forgeessentials.jscripting.wrapper.mc.entity.JsEntityLivingBase;

import net.minecraftforge.event.entity.living.LivingEvent;

public abstract class JsLivingEvent<T extends LivingEvent> extends JsEntityEvent<T> {

	public JsEntityLivingBase<?> getPlayer() {
		return new JsEntityLivingBase<>(_event.getEntityLiving());
	}

}
