package com.ForgeEssentials;

import java.io.File;

import net.minecraftforge.common.Configuration;

public class FEConfig
{

	public static final File FECONFIG = new File(ForgeEssentials.FEDIR, "config.txt");

	// Default values - MUST BE CATEGORY THEN NAME, ALL LOWER CASE
	public String motd = "ForgeEssentials is awesome. https://github.com/ForgeEssentials/ForgeEssentialsMain";

	public void loadConfig()
	{
		Configuration config = new Configuration(FECONFIG);
		OutputHandler.SOP("Loading config");
		config.load();
		ForgeEssentials.motd = config.get("Basic", "motd", motd).value;
		config.save();
	}

	public void changeConfig(String category, String name, Object newValue)
	{
		try
		{
			getClass().getField(category.toLowerCase() + name.toLowerCase()).set(this, newValue);
		} catch (Exception e)
		{
			OutputHandler.SOP("Could not change config setting, a dev probably messed something up.");
		}
		FECONFIG.delete();
		loadConfig();
	}
}
