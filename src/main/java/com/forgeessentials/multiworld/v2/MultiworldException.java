package com.forgeessentials.multiworld.v2;

public class MultiworldException extends Exception
{
    private static final long serialVersionUID = 1L;

    public static enum Type
    {
        ALREADY_EXISTS("A world with that name already exists"), NO_BIOME_PROVIDER("There is no biome provider by that name"), 
        NO_WORLDTYPE("There is no dimension type by that name"), NO_WORLD_SETTINGS("There are no dimension settings by that name");

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
