/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package com.ForgeEssentials.mcfconfig;

import java.util.ArrayList;

public class Property
{
	public enum Type
	{
		STRING, INTEGER, BOOLEAN
	}

	private String name;
	public ArrayList<String> value;
	public String comment;
	private Type type; // Currently does nothing, need to design a way to save/load from the file.
	public boolean expandable;

	public Property()
	{
		value = new ArrayList<String>();
	}

	public Property(String name, String value, Type type)
	{
		setName(name);
		this.value = new ArrayList<String>();
		this.value.add(value);
		this.type = type;
	}

	public Property(String name, ArrayList value, Type type)
	{
		setName(name);
		this.value = value;
		this.type = type;
		expandable = true;
	}

	/**
	 * Returns the value in this property as a integer, if the value is not a valid integer, it will return -1.
	 * 
	 * @return The value
	 */
	public int getInt()
	{
		return getInt(-1);
	}

	/**
	 * Returns the value in this property as a integer, if the value is not a valid integer, it will return the provided default.
	 * 
	 * @param _default
	 *            The default to provide if the current value is not a valid integer
	 * @return The value
	 */
	public int getInt(int _default)
	{
		try
		{
			return Integer.parseInt(value.get(0));
		} catch (NumberFormatException e)
		{
			return _default;
		}
	}

	/**
	 * Checks if the current value stored in this property can be converted to an integer.
	 * 
	 * @return True if the vslue can be converted to an integer
	 */
	public boolean isIntValue()
	{
		try
		{
			Integer.parseInt(value.get(0));
			return true;
		} catch (NumberFormatException e)
		{
			return false;
		}
	}

	/**
	 * Returns the value in this property as a boolean, if the value is not a valid boolean, it will return the provided default.
	 * 
	 * @param _default
	 *            The default to provide
	 * @return The value as a boolean, or the default
	 */
	public boolean getBoolean(boolean _default)
	{
		if (isBooleanValue())
		{
			return Boolean.parseBoolean(value.get(0));
		} else
		{
			return _default;
		}
	}

	/**
	 * Checks if the current value held by this property is a valid boolean value.
	 * 
	 * @return True if it is a boolean value
	 */
	public boolean isBooleanValue()
	{
		return ("true".equals(value.get(0).toLowerCase()) || "false".equals(value.get(0).toLowerCase()));
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
