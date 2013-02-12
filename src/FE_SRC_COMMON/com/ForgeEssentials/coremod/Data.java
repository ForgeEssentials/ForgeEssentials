package com.ForgeEssentials.coremod;

/**
 * Kindly do not reference any FE classes outside the coremod package in this
 * class. This is a store room for all String[]s used by the coremod, 99% of
 * stuff is edited here and not in the actual coremod classes.
 */

public class Data
{

	protected static String[]	libraries		=
												{ "mysql-connector-java-bin.jar", "H2DB.jar", "worldedit-5.4.6-SNAPSHOT.jar", "Metrics-FE-r2.jar" };
	protected static String[]	checksums		=
												{ "3ae0cff91d7f40d5b4c7cefbbd1eab34025bdc15", "32f12e53b4dab80b721525c01d766b95d22129bb", "de96549f9c31aa9268f649fc6757e7b68b180549", "fd25f4d2ec961ac61d2019a2b7de8a3b22b75165" };
	protected static String[]	transformers	=
												{ "com.ForgeEssentials.coremod.transformers.FEPermissionsTransformer", "com.ForgeEssentials.coremod.transformers.FEAccessTransformer", "com.ForgeEssentials.coremod.transformers.FEeventAdder" };
}
