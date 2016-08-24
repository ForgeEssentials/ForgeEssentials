package com.forgeessentials.playerlogger;

import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;

public class PlayerLoggerConfig extends ConfigLoaderBase
{

    private static final String CAT = "PlayerLogger";

    // Main
    public static String databaseType;
    public static String databaseUrl;
    public static String databaseUsername;
    public static String databasePassword;

    public static int logDuration;

    public static double playerPositionInterval;

    @Override
    public void load(Configuration config, boolean isReload)
    {
        config.addCustomCategoryComment(CAT, "PlayerLogger config");
        databaseType = config.get(CAT, "DB_type", "h2", "Database type (h2 / mysql). DO NOT USE MYSQL UNLESS YOU REALLY NEED TO!").getString();
        databaseUrl = config.get(CAT, "DB_url", "ForgeEssentials/playerlogger",
                "Database url. Filename for H2 or server address for MySql (e.g., \"localhost:3306/forgeessentials\").").getString();
        databaseUsername = config.get(CAT, "DB_user", "forgeessentials", "Database user.").getString();
        databasePassword = config.get(CAT, "DB_password", "forgeessentials", "Database password.").getString();
        logDuration = config.get(CAT, "log_duration", 0, "Days to keep data saved in the database. Set to 0 to keep all data indefinitely.").getInt();
        playerPositionInterval = config.get(CAT, "player_pos_interval", 5, "Log player positions every X seconds. Set to 0 to disable.").getDouble();
        if (playerPositionInterval > 0 && playerPositionInterval < 0.5)
            playerPositionInterval = 0.5;
        if (ModulePlayerLogger.getLogger().getEntityManager() != null)
            ModulePlayerLogger.getLogger().loadDatabase();
    }

    @Override
    public void save(Configuration config)
    {
        config.addCustomCategoryComment(CAT, "Configure the backup system.");
        config.get(CAT, "DB_type", "h2", "Database type. Available types are h2 and mysql.").set(databaseType);
        config.get(CAT, "DB_url", "ForgeEssentials/playerlogger", "Database url. Filename for H2 or server address for MySql.").set(databaseUrl);
        config.get(CAT, "DB_user", "forgeessentials", "Database user.").set(databaseUsername);
        config.get(CAT, "DB_password", "forgeessentials", "Database password.").set(databasePassword);
        config.get(CAT, "daystokeepdata", 0, "Days to keep data saved in the database. Set to 0 to keep all data indefinitely.").set(logDuration);
    }

}
