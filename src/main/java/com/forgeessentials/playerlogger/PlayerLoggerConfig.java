package com.forgeessentials.playerlogger;


import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;

import net.minecraftforge.common.ForgeConfigSpec;

public class PlayerLoggerConfig
{

    private static final String CAT = "PlayerLogger";

    // Main
    public static String databaseType;
    public static String databaseUrl;
    public static String databaseUsername;
    public static String databasePassword;

    public static int logDuration;

    public static double playerPositionInterval;

    static ForgeConfigSpec.ConfigValue<String> FEdatabaseType;
    static ForgeConfigSpec.ConfigValue<String> FEdatabaseUrl;
    static ForgeConfigSpec.ConfigValue<String> FEdatabaseUsername;
    static ForgeConfigSpec.ConfigValue<String> FEdatabasePassword;
    static ForgeConfigSpec.IntValue FElogDuration;
    static ForgeConfigSpec.IntValue FEplayerPositionInterval;
    
    public static void load(ForgeConfigSpec.Builder BUILDER)
    {
    	BUILDER.comment("PlayerLogger config").push(CAT);
    	
    	FEdatabaseType = BUILDER.comment("Database type (h2 / mysql). DO NOT USE MYSQL UNLESS YOU REALLY NEED TO!").define("DB_type", "h2");
    	FEdatabaseUrl = BUILDER.comment("Database url. Filename for H2 or server address for MySql (e.g., \"localhost:3306/forgeessentials\").").define("DB_url", "ForgeEssentials/playerlogger");
    	FEdatabaseUsername = BUILDER.comment("Database user.").define("DB_user", "forgeessentials");
    	FEdatabasePassword = BUILDER.comment("Database password.").define("DB_password", "forgeessentials");
    	FElogDuration = BUILDER.comment("Days to keep data saved in the database. Set to 0 to keep all data indefinitely.").defineInRange("log_duration", 0, 0, Integer.MAX_VALUE);
    	FEplayerPositionInterval = BUILDER.comment("Log player positions every X seconds. Set to 0 to disable.").defineInRange("player_pos_interval", 5, 0, Integer.MAX_VALUE);
    	BUILDER.pop();
    }

	public static void bakeConfig(boolean reload) {
		databaseType = FEdatabaseType.get();
        databaseUrl = FEdatabaseUrl.get();
        databaseUsername = FEdatabaseUsername.get();
        databasePassword = FEdatabasePassword.get();
        logDuration = FElogDuration.get();
        playerPositionInterval = FEplayerPositionInterval.get();
        if (playerPositionInterval > 0 && playerPositionInterval < 0.5)
            playerPositionInterval = 0.5;
        if (ModulePlayerLogger.getLogger().getEntityManager() != null)
            ModulePlayerLogger.getLogger().loadDatabase();
	}
	/*
    public void save()
    {
        config.addCustomCategoryComment(CAT, "Configure the backup system.");
        config.get(CAT, "DB_type", "h2", "Database type. Available types are h2 and mysql.").set(databaseType);
        config.get(CAT, "DB_url", "ForgeEssentials/playerlogger", "Database url. Filename for H2 or server address for MySql.").set(databaseUrl);
        config.get(CAT, "DB_user", "forgeessentials", "Database user.").set(databaseUsername);
        config.get(CAT, "DB_password", "forgeessentials", "Database password.").set(databasePassword);
        config.get(CAT, "daystokeepdata", 0, "Days to keep data saved in the database. Set to 0 to keep all data indefinitely.").set(logDuration);
    }*/

}
