package com.forgeessentialsclient.config.ValuesCached;

import java.util.function.BooleanSupplier;
import com.forgeessentialsclient.config.IFEConfig;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ValueCachedBoolean extends ValueCachedPrimitive<Boolean> implements BooleanSupplier
{
	private boolean cachedValue;

	private ValueCachedBoolean(IFEConfig config, ConfigValue<Boolean> internal) {
		super(config, internal);
	}

	public static ValueCachedBoolean wrap(IFEConfig config, ConfigValue<Boolean> internal) {
		return new ValueCachedBoolean(config, internal);
	}

	public boolean get() {
		if (!resolved) {
			//If we don't have a cached value or need to resolve it again, get it from the actual ConfigValue
			cachedValue = internal.get();
			resolved = true;
		}
		return cachedValue;
	}

	@Override
	public boolean getAsBoolean() {
		return get();
	}

	public void set(boolean value) {
		internal.set(value);
		cachedValue = value;
	}
}
