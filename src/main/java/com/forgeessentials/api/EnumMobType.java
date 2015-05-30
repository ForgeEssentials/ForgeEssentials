package com.forgeessentials.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public enum EnumMobType
{
    BOSS, GOLEM, HOSTILE, PASSIVE, VILLAGER, TAMEABLE;

    public static boolean isMobType(String type)
    {
        try
        {
            EnumMobType.valueOf(type.toUpperCase());
            return true;
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface FEMob
    {
        EnumMobType type() default EnumMobType.HOSTILE;

        @Retention(RetentionPolicy.RUNTIME)
        @Target({ ElementType.METHOD, ElementType.FIELD })
        public @interface IsTamed
        {
        }
    }
}
