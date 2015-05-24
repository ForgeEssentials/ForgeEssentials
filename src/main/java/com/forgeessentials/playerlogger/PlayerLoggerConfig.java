package com.forgeessentials.playerlogger;

import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.core.moduleLauncher.config.ConfigLoader.ConfigLoaderBase;

public class PlayerLoggerConfig extends ConfigLoaderBase {

    private static final String CAT = "PlayerLogger";

    // Main
    public static String databaseType;
    public static String databaseUrl;
    public static String databaseUsername;
    public static String databasePassword;
    
    @Override
    public void load(Configuration config, boolean isReload)
    {
        config.addCustomCategoryComment(CAT, "PlayerLogger config");
        databaseType = config.get(CAT, "DB_type", "h2", "Database type. Available types are h2 and mysql.").getString();
        databaseUrl = config.get(CAT, "DB_url", "ForgeEssentials/playerlogger", "Database url. Filename for H2 or server address for MySql (e.g., \"localhost:3306/forgeessentials\").").getString();
        databaseUsername = config.get(CAT, "DB_user", "forgeessentials", "Database type. Available types are h2 and mysql.").getString();
        databasePassword = config.get(CAT, "DB_password", "forgeessentials", "Database type. Available types are h2 and mysql.").getString();
    }

    @Override
    public void save(Configuration config)
    {
        config.addCustomCategoryComment(CAT, "Configure the backup system.");
        config.get(CAT, "DB_type", "h2", "Database type. Available types are h2 and mysql.").set(databaseType);
        config.get(CAT, "DB_url", "ForgeEssentials/playerlogger", "Database url. Filename for H2 or server address for MySql.").set(databaseUrl);
        config.get(CAT, "DB_user", "forgeessentials", "Database type. Available types are h2 and mysql.").set(databaseUsername);
        config.get(CAT, "DB_password", "forgeessentials", "Database type. Available types are h2 and mysql.").set(databasePassword);
    }

}
