package com.ForgeEssentials.coremod;

/**
 * Kindly do not reference any FE classes outside the coremod package in this class.
 * This is a store room for all String[]s used by the coremod, 99% of stuff is edited here and not in the actual coremod classes.
 */

public class Data
{

	public static String[]	libraries		= { "mysql-connector-java-bin.jar", "sqlite-jdbc.jar" };
	public static String[]	checksums		= { "3AE0CFF91D7F40D5B4C7CEFBBD1EAB34025BDC15", "CEA9F7F8E6BCB580D953A8651FB8391640DE0F85" };
	protected static String[]	transformers	= { "com.ForgeEssentials.coremod.FEPermissionsTransformer" };
    protected static String[]   downloaders     = {"com.ForgeEssentials.coremod.libget.SQLDownloader", "com.ForgeEssentials.coremod.libget.WEDownloader"};
}
