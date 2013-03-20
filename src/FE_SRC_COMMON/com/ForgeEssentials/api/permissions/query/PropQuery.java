package com.ForgeEssentials.api.permissions.query;

import com.google.common.base.Strings;

public abstract class PropQuery
{
	public final String	perm;
	protected String	value;

	public PropQuery(String permKey)
	{
		perm = permKey;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public boolean hasValue()
	{
		return !Strings.isNullOrEmpty(value);
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
		catch (NumberFormatException e)
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
		catch (NumberFormatException e)
		{
			return -1;
		}
	}
}
