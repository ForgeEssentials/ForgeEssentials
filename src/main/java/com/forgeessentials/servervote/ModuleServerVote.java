package com.forgeessentials.servervote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.config.ConfigData;
import com.forgeessentials.core.config.ConfigLoaderBase;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.ModuleDir;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartedEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppingEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

@FEModule(name = "ServerVote", parentMod = ForgeEssentials.class, defaultModule = false, version = ForgeEssentials.CURRENT_MODULE_VERSION)
public class ModuleServerVote extends ConfigLoaderBase
{
    private static ForgeConfigSpec SERVERVOTE_CONFIG;
    private static final ConfigData data = new ConfigData("ServerVote", SERVERVOTE_CONFIG,
            new ForgeConfigSpec.Builder());

    @ModuleDir
    public static File moduleDir;

    public static VoteReceiver votifier;
    public static PrintWriter log;
    public static final String scriptKey = "servervote";

    private HashMap<String, VoteEvent> offlineList = new HashMap<>();

    @SubscribeEvent
    public void serverStarting(FEModuleServerStartingEvent event)
    {
        if (APIRegistry.scripts != null)
        {
            APIRegistry.scripts.addScriptType(scriptKey);
        }
        try
        {
            votifier = new VoteReceiver(ConfigServerVote.hostname, ConfigServerVote.port, ConfigServerVote.token);
            votifier.start();
        }
        catch (Exception e1)
        {
            LoggingHandler.felog.error("Error initializing Votifier compat.");
            e1.printStackTrace();
        }

        try
        {
            File logFile = new File(moduleDir, "vote.log");
            if (!logFile.exists())
            {
                logFile.createNewFile();
            }
            log = new PrintWriter(new FileWriter(logFile, true), true);
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    @SubscribeEvent
    public void serverStarted(FEModuleServerStartedEvent event)
    {
        File file = new File(moduleDir, "offlineVoteList.txt");
        if (file.exists())
        {
            try (BufferedReader br = new BufferedReader(new FileReader(file)))
            {
                String line;
                while ((line = br.readLine()) != null)
                {
                    if (!(line.startsWith("#") || line.isEmpty()))
                    {
                        VoteEvent vote = new VoteEvent(line.trim());
                        offlineList.put(vote.player, vote);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStoppingEvent e)
    {
        try
        {
            votifier.shutdown();
        }
        catch (Exception e1)
        {
            LoggingHandler.felog.error("Error closing Votifier compat thread.");
            e1.printStackTrace();
        }

        try
        {
            log.close();
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }

        try
        {
            File file = new File(moduleDir, "offlineVoteList.txt");
            if (!file.exists())
            {
                file.createNewFile();
            }

            PrintWriter pw = new PrintWriter(file);
            for (VoteEvent vote : offlineList.values())
            {
                pw.println(vote.toString());
            }
            pw.close();
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void serverVoteEvent(VoteEvent vote)
    {
        ServerPlayer player = ServerLifecycleHooks.getCurrentServer().getPlayerList()
                .getPlayerByName(vote.player);
        if (player != null)
        {
            doPlayer(player, vote);
        }
        else
        {
            offlineList.put(vote.player, vote);
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e)
    {
        if (offlineList.containsKey(e.getPlayer().getDisplayName().getString()))
        {
            doPlayer((ServerPlayer) e.getPlayer(),
                    offlineList.remove(e.getPlayer().getDisplayName().getString()));
        }
    }

    private static void doPlayer(ServerPlayer player, VoteEvent vote)
    {
        log.println(
                String.format("Player %s voted on service %s on %s", vote.player, vote.serviceName, vote.timeStamp));
        if (!ConfigServerVote.msgAll.equals(""))
        {
            ServerLifecycleHooks.getCurrentServer().getPlayerList()
                    .broadcastAll(new ClientboundChatPacket(
                            new TextComponent(ChatOutputHandler.formatColors(ConfigServerVote.msgAll
                                    .replaceAll("%service", vote.serviceName).replaceAll("%player", vote.player))),
                            ChatType.CHAT, player.getGameProfile().getId()));
        }

        if (!ConfigServerVote.msgVoter.equals(""))
        {
            ChatOutputHandler.sendMessage(player.createCommandSourceStack(),
                    ChatOutputHandler.formatColors(ConfigServerVote.msgVoter.replaceAll("%service", vote.serviceName)
                            .replaceAll("%player", vote.player)));
        }
        if (APIRegistry.scripts != null)
        {
            APIRegistry.scripts.runEventScripts(scriptKey, player.createCommandSourceStack());
        }
    }

    @Override
    public void load(Builder BUILDER, boolean isReload)
    {
        ConfigServerVote.load(BUILDER, isReload);
    }

    @Override
    public void bakeConfig(boolean reload)
    {
        ConfigServerVote.bakeConfig(reload);
    }

    @Override
    public ConfigData returnData()
    {
        return data;
    }
}
