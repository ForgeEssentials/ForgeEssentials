package com.ForgeEssentials.api.data;

import com.ForgeEssentials.util.EnumDBType;
import com.google.common.base.Joiner;

public enum EnumDriverType
{
	TEXT, BINARY, SQL;

	/**
	 * @param delimeter The char sequence to be done between elements.
	 * 	trailing delimiter will be there at the end.
	 * @return
	 */
	public static String getAll(String delimeter)
	{
		return Joiner.on(", ").join(EnumDriverType.values());
	}
}
