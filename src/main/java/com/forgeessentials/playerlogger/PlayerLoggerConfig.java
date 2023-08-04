package com.forgeessentials.playerlogger;

import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;

public class PlayerLoggerConfig
{

    private static final String CAT = "PlayerLogger";

    // Main
    private static String databaseType;
    private static String databaseUrl;
    private static String databaseUsername;
    private static String databasePassword;

    private static int logDuration;

    private static double playerPositionInterval;

    static ForgeConfigSpec.ConfigValue<String> FEdatabaseType;
    static ForgeConfigSpec.ConfigValue<String> FEdatabaseUrl;
    static ForgeConfigSpec.ConfigValue<String> FEdatabaseUsername;
    static ForgeConfigSpec.ConfigValue<String> FEdatabasePassword;
    static ForgeConfigSpec.IntValue FElogDuration;
    static ForgeConfigSpec.IntValue FEplayerPositionInterval;

    public static void load(Builder BUILDER, boolean isReload)
    {
        BUILDER.comment("PlayerLogger config").push(CAT);
        FEdatabaseType = BUILDER.comment("Database type (h2 / mysql). DO NOT USE MYSQL UNLESS YOU REALLY NEED TO!")
                .define("DB_type", "h2");
        FEdatabaseUrl = BUILDER.comment(
                "Database url. Filename for H2 or server address for MySql (e.g., \"localhost:3306/forgeessentials\").")
                .define("DB_url", "ForgeEssentials/playerlogger");
        FEdatabaseUsername = BUILDER.comment("Database user.").define("DB_user", "forgeessentials");
        FEdatabasePassword = BUILDER.comment("Database password.").define("DB_password", "forgeessentials");
        FElogDuration = BUILDER
                .comment("Days to keep data saved in the database. Set to 0 to keep all data indefinitely.")
                .defineInRange("log_duration", 0, 0, Integer.MAX_VALUE);
        FEplayerPositionInterval = BUILDER.comment("Log player positions every X seconds. Set to 0 to disable.")
                .defineInRange("player_pos_interval", 5, 0, Integer.MAX_VALUE);
        BUILDER.pop();
    }

    public static void bakeConfig(boolean reload)
    {
        databaseType = FEdatabaseType.get();
        setDatabaseUrl(FEdatabaseUrl.get());
        databaseUsername = FEdatabaseUsername.get();
        databasePassword = FEdatabasePassword.get();
        logDuration = FElogDuration.get();
        playerPositionInterval = FEplayerPositionInterval.get();
        if (playerPositionInterval > 0 && playerPositionInterval < 0.5)
            playerPositionInterval = 0.5;
        if (ModulePlayerLogger.getLogger() != null)
            if (ModulePlayerLogger.getLogger().getEntityManager() != null)
                ModulePlayerLogger.getLogger().loadDatabase();
            else
                LoggingHandler.felog.error("Failed to start PlayerLogger Datatabase, EntityManager is null");
        else
            LoggingHandler.felog.error("Failed to start PlayerLogger Datatabase, Logger is null");
    }

    public static void save(boolean reload)
    {
        FEdatabaseType.set(databaseType);
        FEdatabaseUrl.set(databaseUrl);
        FEdatabaseUsername.set(databaseUsername);
        FEdatabasePassword.set(databasePassword);
        FElogDuration.set(logDuration);
    }

    public static String getDatabaseType()
    {
        return databaseType;
    }

    public static String getDatabaseUrl()
    {
        return databaseUrl;
    }

    public static void setDatabaseUrl(String databaseUrl)
    {
        PlayerLoggerConfig.databaseUrl = databaseUrl;
    }

    public static String getDatabaseUsername()
    {
        return databaseUsername;
    }

    public static String getDatabasePassword()
    {
        return databasePassword;
    }

    public static int getLogDuration()
    {
        return logDuration;
    }

    public static double getPlayerPositionInterval()
    {
        return playerPositionInterval;
    }
}
