/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package com.ForgeEssentials.core.config;

public class Property
{
    public enum Type
    {
        STRING,
        INTEGER,
        BOOLEAN,
        LIST        
    }

    private String name;
    public String value;
    public String[] valueList; // used for List configs
    public String comment;
    private Type type; //Currently does nothing, need to design a way to save/load from the file.
    
    public Property(){}
    
    public Property(String name, String value, Type type)
    {
        setName(name);
        this.value = value;
        this.valueList = new String[] {value};
        this.type = type;
    }
    
    public Property(String name, String[] value)
    {
        setName(name);
        this.valueList = value;
        
        StringBuilder builder = new StringBuilder();
        for (String string : value)
        	builder.append(":").append(string);
        
        this.value = builder.toString();
        
        this.type = Type.LIST;
    }
    
    /**
     * Returns the value in this property as a integer,
     * if the value is not a valid integer, it will return -1.
     * 
     * @return The value
     */
    public int getInt()
    {
        return getInt(-1);
    }
    
    /**
     * Returns the value in this property as a integer,
     * if the value is not a valid integer, it will return the
     * provided default.
     * 
     * @param _default The default to provide if the current value is not a valid integer
     * @return The value
     */
    public int getInt(int _default)
    {
        try
        {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
            return _default;
        }
    }
    
    /**
     * Checks if the current value stored in this property can be converted to an integer.
     * @return True if the vslue can be converted to an integer
     */
    public boolean isIntValue()
    {
    	return type.equals(Type.BOOLEAN);
    }
    
    /**
     * Returns the value in this property as a boolean,
     * if the value is not a valid boolean, it will return the
     * provided default.
     * 
     * @param _default The default to provide
     * @return The value as a boolean, or the default
     */
    public boolean getBoolean(boolean _default)
    {
        if (isBooleanValue())
        {
            return Boolean.parseBoolean(value);
        }
        else
        {
            return _default;
        }
    }
    
    /**
     * Checks if the current value held by this property is a valid boolean value.
     * @return True if it is a boolean value
     */
    public boolean isBooleanValue()
    {
    	return type.equals(Type.BOOLEAN);
    }
    
    public boolean isList()
    {
    	return type.equals(Type.LIST);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
