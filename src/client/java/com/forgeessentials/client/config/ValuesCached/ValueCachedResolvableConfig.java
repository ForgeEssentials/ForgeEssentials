package com.forgeessentials.client.config.ValuesCached;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.forgeessentials.client.config.IFEConfig;

import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public abstract class ValueCachedResolvableConfig<TYPE, REAL>
{
    private final ConfigValue<REAL> internal;
    private List<Runnable> invalidationListeners;
    @Nullable
    private TYPE cachedValue;

    protected ValueCachedResolvableConfig(IFEConfig config, ConfigValue<REAL> internal)
    {
        this.internal = internal;
        config.addCachedValue(this);
    }

    public void addInvalidationListener(Runnable listener)
    {
        if (invalidationListeners == null)
        {
            invalidationListeners = new ArrayList<>();
        }
        invalidationListeners.add(listener);
    }

    protected abstract TYPE resolve(REAL encoded);

    protected abstract REAL encode(TYPE value);

    public TYPE get()
    {
        if (cachedValue == null)
        {
            // If we don't have a cached value, resolve it from the actual ConfigValue
            cachedValue = resolve(internal.get());
        }
        return cachedValue;
    }

    public void set(TYPE value)
    {
        internal.set(encode(value));
        cachedValue = value;
    }

    public void clearListenerCache()
    {
        cachedValue = null;
        if (invalidationListeners != null)
        {
            invalidationListeners.forEach(Runnable::run);
        }
    }
}
