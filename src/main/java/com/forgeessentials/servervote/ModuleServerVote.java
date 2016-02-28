package com.forgeessentials.servervote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.ModuleDir;
import com.forgeessentials.servervote.Votifier.VoteReceiver;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

@FEModule(name = "ServerVote", parentMod = ForgeEssentials.class, defaultModule = false)
public class ModuleServerVote
{

    @ModuleDir
    public static File moduleDir;

    public static VoteReceiver votifier;
    public static PrintWriter log;
    public static final String scriptKey = "servervote";

    private HashMap<String, VoteEvent> offlineList = new HashMap<>();

    public ModuleServerVote()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void init(FEModuleInitEvent e)
    {
        FMLCommonHandler.instance().bus().register(this);
        ForgeEssentials.getConfigManager().registerLoader("ServerVote", new ConfigServerVote());
        APIRegistry.scripts.addScriptType(scriptKey);
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        try
        {
            votifier = new VoteReceiver(ConfigServerVote.hostname, ConfigServerVote.port);
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

        try
        {
            File file = new File(moduleDir, "offlineVoteList.txt");
            if (file.exists())
            {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null)
                {
                    if (!(line.startsWith("#") || line.isEmpty()))
                    {
                        VoteEvent vote = new VoteEvent(line.trim());
                        offlineList.put(vote.player, vote);
                    }
                }
                br.close();
            }
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent e)
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
    public void defVoteResponces(VoteEvent vote)
    {
        EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerByUsername(vote.player);
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
        if (offlineList.containsKey(e.player.getName()))
        {
            doPlayer((EntityPlayerMP) e.player, offlineList.remove(e.player.getName()));
        }
    }

    private static void doPlayer(EntityPlayerMP player, VoteEvent vote)
    {
        log.println(String.format("Player %s voted on service %s on %s", vote.player, vote.serviceName, vote.timeStamp));
        if (!ConfigServerVote.msgAll.equals(""))
        {
            MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(new S02PacketChat(new ChatComponentText(
                    ChatOutputHandler.formatColors(ConfigServerVote.msgAll.replaceAll("%service", vote.serviceName).replaceAll("%player", vote.player)))));
        }

        if (!ConfigServerVote.msgVoter.equals(""))
        {
            ChatOutputHandler.sendMessage(player,
                    ChatOutputHandler.formatColors(ConfigServerVote.msgVoter.replaceAll("%service", vote.serviceName).replaceAll("%player", vote.player)));
        }

        APIRegistry.scripts.runEventScripts(scriptKey, player);
    }
    
}
