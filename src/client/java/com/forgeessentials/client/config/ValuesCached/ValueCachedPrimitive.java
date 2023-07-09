package com.forgeessentials.client.config.ValuesCached;

import java.util.ArrayList;
import java.util.List;

import com.forgeessentials.client.config.IFEConfig;

import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class ValueCachedPrimitive<T>
{
    protected final ConfigValue<T> internal;
    private List<Runnable> invalidationListeners;
    protected boolean resolved;

    protected ValueCachedPrimitive(IFEConfig config, ConfigValue<T> internal)
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

    public void clearListenerCache()
    {
        resolved = false;
        if (invalidationListeners != null)
        {
            invalidationListeners.forEach(Runnable::run);
        }
    }
}
