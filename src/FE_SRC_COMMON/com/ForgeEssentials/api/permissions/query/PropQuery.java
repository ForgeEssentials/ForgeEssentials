package com.ForgeEssentials.api.permissions.query;

public abstract class PropQuery
{
	public enum PermPropType
	{
		/**
		 * Integers...
		 */
		NUMBER,
		
		/**
		 * floats.. NO doubles.. cast it if ya want...
		 */
		DECIMAL,
		
		/**
		 * duh, strings. You may break objects down into strings if you wish.. 
		 * but its not necessarily a good idea.
		 */
		TEXT;
	}
	
	public final String perm;
	public final PermPropType type;
	protected String value;

	public PropQuery(String permKey, PermPropType type)
	{
		perm = permKey;
		this.type = type;
	}
	
	public void setValue(String value)
	{
		this.value = value;
	}
	
	/**
	 * @return an empty string if this is unset.
	 */
	public String getStringValue()
	{
		if (value == null)
			return "";
		return value;
	}
	
	/**
	 * @return -1 if unset or not a valid integer.
	 */
	public int getNumberValue()
	{
		if (value == null)
			return -1;
		
		try
		{
			return Integer.parseInt(value);
		}
		catch(NumberFormatException e)
		{
			return -1;
		}
	}
	
	/**
	 * @return -1 if unset or not a valid decimal.
	 */
	public float getDecimalValue()
	{
		if (value == null)
			return -1;
		
		try
		{
			return Integer.parseInt(value);
		}
		catch(NumberFormatException e)
		{
			return -1;
		}
	}
}
