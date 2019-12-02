package com.forgeessentials.permissions.ftbu_compat;

import java.util.Collection;

import javax.annotation.Nullable;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.server.permission.context.IContext;

import com.feed_the_beast.ftblib.events.RegisterRankConfigHandlerEvent;
import com.feed_the_beast.ftblib.lib.config.ConfigNull;
import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.config.DefaultRankConfigHandler;
import com.feed_the_beast.ftblib.lib.config.IRankConfigHandler;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.output.LoggingHandler;
import com.mojang.authlib.GameProfile;

public enum FTBURankConfigHandler implements IRankConfigHandler
{
    INSTANCE;

    @SubscribeEvent
    public static void registerRankConfigHandler(RegisterRankConfigHandlerEvent event)
    {
        if (!FTBUtilitiesConfig.ranks.enabled)
        {
            event.setHandler(INSTANCE);
        } else {
            LoggingHandler.felog.info("Ranks are active...  Not registering configs!");
        }
    }

    @Override public void registerRankConfig(RankConfigValueInfo info)
    {
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
            UserIdent ident = UserIdent.get(profile.getId());
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
