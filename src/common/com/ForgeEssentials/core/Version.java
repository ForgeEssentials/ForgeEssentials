package com.ForgeEssentials.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import cpw.mods.fml.common.Loader;

public class Version
{
	public static String		version	= "0.0.1";
	private static final String	latest	= "https://raw.github.com/ForgeEssentials/ForgeEssentialsMain/master/VERSION.TXT";

	/**
	 * Based on Pahimar's.
	 */
	public static void checkVersion()
	{
		try
		{
			URL url = new URL(latest);

			InputStreamReader read = new InputStreamReader(url.openStream());
			BufferedReader reader = new BufferedReader(read);

			String line = null;

			while ((line = reader.readLine()) != null)
			{
				if (line.startsWith(Loader.instance().getMCVersionString()))
				{
					if (line.endsWith(version))
						OutputHandler.SOP("You are running the latest version of ForgeEssentials.");
					reader.close();
					read.close();
					return;
				}
			}
			OutputHandler.SOP("Please update ForgeEssentials.");
			reader.close();
			read.close();
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
			OutputHandler.SOP("Unable to connect to version checker");
		}
	}
}
