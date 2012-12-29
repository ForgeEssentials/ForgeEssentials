package com.ForgeEssentials.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import cpw.mods.fml.common.Loader;

public class Version
{
	private static final String	latest		= "https://raw.github.com/ForgeEssentials/ForgeEssentialsMain/master/VERSION.TXT";
	// Only change me when updating versions
	public static int			major		= 0;
	public static int			minor		= 1;
	public static int			revision	= 0;
	// Should always be 0
	public static int			jenkins		= 0;

	public static String getVersion()
	{
		return String.format("%d.%d.%d.%d", major, minor, revision, jenkins);
	}

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
				if (line.startsWith(Loader.instance().getMCVersionString()))
				{
					if (line.endsWith(getVersion()))
						OutputHandler.SOP("You are running the latest version of ForgeEssentials.");
					reader.close();
					read.close();
					return;
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
