package com.ForgeEssentials.coremod;

/**
 * Kindly do not reference any FE classes outside the coremod package in this class. This is a store room for all String[]s used by the coremod, 99% of stuff is
 * edited here and not in the actual coremod classes.
 */

public class Data
{

	public static String[] libraries = { "mysql-connector-java-bin.jar", "H2DB.jar" };
	public static String[] checksums = { "3ae0cff91d7f40d5b4c7cefbbd1eab34025bdc15", "32f12e53b4dab80b721525c01d766b95d22129bb" };
	protected static String[] transformers = { "com.ForgeEssentials.coremod.transformers.FEPermissionsTransformer",
			"com.ForgeEssentials.coremod.transformers.FEAccessTransformer", "com.ForgeEssentials.coremod.transformers.FEeventAdder" };
	protected static String[] downloaders = { "com.ForgeEssentials.coremod.libget.SQLDownloader", "com.ForgeEssentials.coremod.libget.WEDownloader" };
}
