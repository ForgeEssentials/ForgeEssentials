package com.ForgeEssentials.core.preloader;

/**
 * Kindly do not reference any FE classes outside the coremod package in this
 * class. This is a store room for all String[]s used by the coremod, 99% of
 * stuff is edited here and not in the actual coremod classes.
 */

public class Data
{

	protected static String[]	libraries		=
												{ "mysql-connector-java-bin.jar", "H2DB.jar", "metrics-R7-FEmod.jar" };
	protected static String[]	checksums		=
												{ "3ae0cff91d7f40d5b4c7cefbbd1eab34025bdc15", "32f12e53b4dab80b721525c01d766b95d22129bb", "bfffcaf975982b45d568f95526eba337652eecfb" };
	protected static String[]	transformers	=
												{ "com.ForgeEssentials.core.preloader.asm.FEPermissionsTransformer", "com.ForgeEssentials.core.preloader.asm.FEAccessTransformer", "com.ForgeEssentials.core.preloader.asm.FEeventAdder", "com.ForgeEssentials.core.preloader.asm.FEBrandingTransformer" };
}
