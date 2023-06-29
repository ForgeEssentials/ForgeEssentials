package com.forgeessentials.data.v2;

public @interface SerializationGroup {

	public String name() default DataManager.DEFAULT_GROUP;

}
