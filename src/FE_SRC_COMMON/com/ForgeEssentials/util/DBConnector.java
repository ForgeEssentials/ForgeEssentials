package com.ForgeEssentials.util;

public class DBConnector
{
	EnumDBType loadedType;
	private final DBConnector fallback;
	public final String name;
	
	/**
	 * @param name a name for the DB connector. to be used in Logging.
	 * @param fallback The DBConnector from which to take information for a given type if loading that type from this config fails.
	 */
	public DBConnector(String name, DBConnector fallback)
	{
		this.name = name;
		this.fallback = fallback;
	}
	
	public void generate()
	{
		// TODOmakeconfig stuff
	}
}
