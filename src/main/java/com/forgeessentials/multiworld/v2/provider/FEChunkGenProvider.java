package com.forgeessentials.multiworld.v2.provider;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FEChunkGenProvider
{
    String providerName();
}
