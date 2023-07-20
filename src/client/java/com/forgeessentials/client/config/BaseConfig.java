package com.forgeessentials.client.config;

import java.util.ArrayList;
import java.util.List;

import com.forgeessentials.client.config.valuesCached.ValueCachedPrimitive;
import com.forgeessentials.client.config.valuesCached.ValueCachedResolvableConfig;

public abstract class BaseConfig implements IFEConfig
{
    private final List<ValueCachedResolvableConfig<?, ?>> valuescachedConfig = new ArrayList<>();
    private final List<ValueCachedPrimitive<?>> valuescachedPrimitive = new ArrayList<>();

    @Override
    public void clearListenerCache()
    {
        valuescachedConfig.forEach(ValueCachedResolvableConfig::clearListenerCache);
        valuescachedPrimitive.forEach(ValueCachedPrimitive::clearListenerCache);
    }

    @Override
    public <T, R> void addCachedValue(ValueCachedResolvableConfig<T, R> configValue)
    {
        valuescachedConfig.add(configValue);
    }

    @Override
    public <T> void addCachedValue(ValueCachedPrimitive<T> configValue)
    {
        valuescachedPrimitive.add(configValue);
    }
}
