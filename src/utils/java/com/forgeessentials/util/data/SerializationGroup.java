package com.forgeessentials.util.data;

public @interface SerializationGroup
{

    public String name() default DataUtils.DEFAULT_GROUP;

}
