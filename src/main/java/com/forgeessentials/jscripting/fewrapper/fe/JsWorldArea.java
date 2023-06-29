package com.forgeessentials.jscripting.fewrapper.fe;

import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.jscripting.wrapper.mc.JsAreaBase;

public class JsWorldArea<T extends WorldArea> extends JsAreaBase<T> {

	public JsWorldArea(T that) {
		super(that);
	}

	@SuppressWarnings("unchecked")
	public JsWorldArea(String dim, JsPoint<?> p1, JsPoint<?> p2) {
		this((T) new WorldArea(dim, p1.getThat(), p2.getThat()));
	}

	public String getDimension() {
		return that.getDimension();
	}

}
