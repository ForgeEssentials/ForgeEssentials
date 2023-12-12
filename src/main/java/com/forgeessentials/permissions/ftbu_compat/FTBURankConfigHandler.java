package com.forgeessentials.permissions.ftbu_compat;

import java.lang.reflect.Field;
import java.util.Collection;

import javax.annotation.Nullable;

import net.minecraft.server.MinecraftServer;
import serverutils.events.RegisterRankConfigHandlerEvent;
import serverutils.lib.config.ConfigNull;
import serverutils.lib.config.ConfigValue;
import serverutils.lib.config.DefaultRankConfigHandler;
import serverutils.lib.config.IRankConfigHandler;
import serverutils.lib.config.RankConfigValueInfo;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.output.LoggingHandler;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public enum FTBURankConfigHandler implements IRankConfigHandler
{
    INSTANCE;

    @SubscribeEvent
    public static void registerRankConfigHandler(RegisterRankConfigHandlerEvent event)
    {
        LoggingHandler.felog.debug("registerRankConfigHandler()");
        if (INSTANCE.isFTBURanksActive())
        {
            LoggingHandler.felog.info("Ranks are active...  Not registering configs!");
        } else {
            event.setHandler(INSTANCE);
        }
    }

    public boolean isFTBURanksActive()
    {
        try {
           Class<?> clazz = Class.forName("serverutils.ServerUtilitiesConfig");
           Field ranksF = clazz.getField("ranks");
           Object ranks = ranksF.get(null);
           Class<?> ranksConfig = Class.forName("serverutils.ServerUtilitiesConfig$RanksConfig");
           Field enabledF = ranksConfig.getField("enabled");
           return enabledF.getBoolean(ranks);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignored) {
            LoggingHandler.felog.warn("GTNHFTBU is not detected but GTNHFTBLib is.  Enabling Configs.");
            LoggingHandler.felog.debug("Associated Stack Trace:", ignored);
        }
        return false;
    }

    @Override public void registerRankConfig(RankConfigValueInfo info)
    {
        LoggingHandler.felog.debug("registerRankConfig({})", info);
        DefaultRankConfigHandler.INSTANCE.registerRankConfig(info);

        APIRegistry.perms.registerPermissionProperty(info.node.toString(), info.defaultValue.getString());
        APIRegistry.perms.registerPermissionPropertyOp(info.node.toString(), info.defaultOPValue.getString());
    }

    @Override public Collection<RankConfigValueInfo> getRegisteredConfigs()
    {
        return DefaultRankConfigHandler.INSTANCE.getRegisteredConfigs();
    }

    @Override public ConfigValue getConfigValue(MinecraftServer server, GameProfile profile, String node)
    {
        LoggingHandler.felog.info("Getting Config value " + node + " for player " + profile.getName());
        ConfigValue value = ConfigNull.INSTANCE;

        RankConfigValueInfo info = getInfo(node);

        if (info != null) {
            LoggingHandler.felog.info("Config Value is not null");
            UserIdent ident = UserIdent.get(profile);
            value = info.defaultValue.copy();
            WorldPoint point = null;
            if (ident.hasPlayer()) {
                point = WorldPoint.create(ident.getPlayer());
            }

            if (!value.setValueFromString(null, APIRegistry.perms.getUserPermissionProperty(ident, node), false)) {
                LoggingHandler.felog.info("Failed to set value");
                return ConfigNull.INSTANCE;
            }

            LoggingHandler.felog.info(node+ " set to " + value.getString());
        }

        return value;
    }

    @Nullable @Override public RankConfigValueInfo getInfo(String node)
    {
        return DefaultRankConfigHandler.INSTANCE.getInfo(node);
    }
}
