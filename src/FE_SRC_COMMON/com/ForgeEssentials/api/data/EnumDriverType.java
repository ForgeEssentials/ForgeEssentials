package com.ForgeEssentials.api.data;

import com.ForgeEssentials.util.EnumDBType;

public enum EnumDriverType
{
	TEXT, BINARY, SQL;

	/**
	 * 
	 * @param delimeter
	 * The char sequence to be done between elements. trailing
	 * delimiter will be there at the end.
	 * @return
	 */
	public static String getAll(String delimeter)
	{
		StringBuilder s = new StringBuilder();

		for (EnumDBType type : EnumDBType.values())
			s.append(type).append(delimeter);

		return s.toString();
	}
}
