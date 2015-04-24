package com.forgeessentials.servervote;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.ModuleDir;
import com.forgeessentials.servervote.Votifier.VoteReceiver;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;

@FEModule(name = "ServerVote", parentMod = ForgeEssentials.class)
public class ModuleServerVote {

    @ModuleDir
    public static File moduleDir;

    public static VoteReceiver votifier;
    public static PrintWriter log;

    private HashMap<String, VoteEvent> offlineList = new HashMap<String, VoteEvent>();

    public ModuleServerVote()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void log(VoteEvent vote)
    {
        OutputHandler.felog.finer("Got Vote from player " + vote.player + " by service " + vote.serviceName + " time " + vote.timeStamp);
    }

    @SubscribeEvent
    public void init(FEModuleInitEvent e)
    {
        FMLCommonHandler.instance().bus().register(this);
        ForgeEssentials.getConfigManager().registerLoader("ServerVote", new ConfigServerVote());
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
            FMLLog.severe("Error initializing Votifier compat.");
            FMLLog.severe(e.toString());
            e1.printStackTrace();
        }

        try
        {
            File logFile = new File(moduleDir, "vote.log");
            if (!logFile.exists())
            {
                logFile.createNewFile();
            }
            log = new PrintWriter(logFile);
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
            FMLLog.severe("Error closing Votifier compat thread.");
            FMLLog.severe(e.toString());
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
        EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().func_152612_a(vote.player);
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
        if (offlineList.containsKey(e.player.getCommandSenderName()))
        {
            doPlayer((EntityPlayerMP) e.player, offlineList.remove(e.player.getCommandSenderName()));
        }
    }

    private static void doPlayer(EntityPlayerMP player, VoteEvent vote)
    {
        if (!ConfigServerVote.msgAll.equals(""))
        {
            player.playerNetServerHandler.sendPacket(new S02PacketChat(new ChatComponentText(FunctionHelper.formatColors(ConfigServerVote.msgAll
                    .replaceAll("%service", vote.serviceName).replaceAll("%player", vote.player)))));
        }

        if (!ConfigServerVote.msgVoter.equals(""))
        {
            OutputHandler.sendMessage(player,
                    FunctionHelper.formatColors(ConfigServerVote.msgAll.replaceAll("%service", vote.serviceName).replaceAll("%player", vote.player)));
        }

        if (!ConfigServerVote.freeStuff.isEmpty())
        {
            for (ItemStack stack : ConfigServerVote.freeStuff)
            {
                OutputHandler.felog.finer(stack.toString());
                player.inventory.addItemStackToInventory(stack.copy());
            }
        }
    }
}
