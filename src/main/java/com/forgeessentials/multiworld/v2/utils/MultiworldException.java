package com.forgeessentials.multiworld.v2.utils;

public class MultiworldException extends Exception
{
    private static final long serialVersionUID = 1L;

    public static enum Type
    {
		WORLD_ALREADY_EXISTS("A world with that name already exists"),
		NO_BIOME_PROVIDER("There is no biome provider by that name"),
		NO_DIMENSION_TYPE("There is no dimension type by that name"),
		NO_DIMENSION_SETTINGS("There are no dimension settings by that name"),
		NULL_BIOME_PROVIDER("Null biome provider"),
		NULL_DIMENSION_TYPE("Null dimension type"),
		NULL_DIMENSION_SETTINGS("Null dimension settings");

        public String error;

        private Type(String error)
        {
            this.error = error;
        }
    }

    public Type type;

    public MultiworldException(Type type)
    {
        this.type = type;

    }
}
