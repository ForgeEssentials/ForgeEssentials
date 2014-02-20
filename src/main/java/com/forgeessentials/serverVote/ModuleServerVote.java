package com.forgeessentials.serverVote;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;

import com.forgeessentials.api.snooper.VoteEvent;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.Config;
import com.forgeessentials.core.moduleLauncher.FEModule.Init;
import com.forgeessentials.core.moduleLauncher.FEModule.ModuleDir;
import com.forgeessentials.core.moduleLauncher.FEModule.ServerInit;
import com.forgeessentials.core.moduleLauncher.FEModule.ServerStop;
import com.forgeessentials.serverVote.Votifier.VoteReceiver;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.modules.FEModuleInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerInitEvent;
import com.forgeessentials.util.events.modules.FEModuleServerStopEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.registry.GameRegistry;

@FEModule(name = "ServerVoteModule", parentMod = ForgeEssentials.class, configClass = ConfigServerVote.class)
public class ModuleServerVote implements IPlayerTracker
{
	@Config
	public static ConfigServerVote     config;

	@ModuleDir
	public static File                 moduleDir;
	
	public static VoteReceiver         votifier;
	public static PrintWriter          log;

    private HashMap<String, VoteEvent>  offlineList = new HashMap<String, VoteEvent>();

	public ModuleServerVote()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Init
	public void init(FEModuleInitEvent e)
	{
	    GameRegistry.registerPlayerTracker(this);
	}

	@ServerInit
	public void serverStarting(FEModuleServerInitEvent e)
	{
		try
		{
			votifier = new VoteReceiver(config.hostname, config.port);
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
		    File logFile = new File(e.getModuleDir(), "vote.log");
		    if (!logFile.exists()) logFile.createNewFile();
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

	@ServerStop
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
		    if (!file.exists()) file.createNewFile();
		    
		    PrintWriter pw = new PrintWriter(file);
		    for (VoteEvent vote : offlineList.values())
		        pw.println(vote.toString());
		    pw.close();
		}
		catch (Exception e1)
		{
		    e1.printStackTrace();
		}
	}

	@ForgeSubscribe(priority = EventPriority.HIGHEST)
	public void defVoteResponces(VoteEvent vote)
	{
	    EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(vote.player);
		if (player != null) doPlayer(player, vote);
		else offlineList.put(vote.player, vote);
	}

    public static void log(VoteEvent vote)
    {
        OutputHandler.felog.finer("Got Vote from player " + vote.player + " by service " + vote.serviceName + " time " + vote.timeStamp);
    }

    @Override
    public void onPlayerLogin(EntityPlayer player)
    {
        if (offlineList.containsKey(player.username))
            doPlayer(player, offlineList.remove(player.username));
    }

    @Override
    public void onPlayerLogout(EntityPlayer player)
    {
        
    }

    @Override
    public void onPlayerChangedDimension(EntityPlayer player)
    {
        
    }

    @Override
    public void onPlayerRespawn(EntityPlayer player)
    {
        
    }
    
    private void doPlayer(EntityPlayer player, VoteEvent vote)
    {
        if (!config.msgAll.equals(""))
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager()
                    .sendPacketToAllPlayers(new Packet3Chat(FunctionHelper.formatColors(config.msgAll.replaceAll("%service", vote.serviceName).replaceAll("%player", vote.player))));
        }

        if (!config.msgVoter.equals(""))
        {
            ChatUtils.sendMessage(player, FunctionHelper.formatColors(config.msgAll.replaceAll("%service", vote.serviceName).replaceAll("%player", vote.player)));
        }

        if (!config.freeStuff.isEmpty())
        {
            for (ItemStack stack : config.freeStuff)
            {
                OutputHandler.felog.finer(stack.toString());
                player.inventory.addItemStackToInventory(stack.copy());
            }
        }
    }
}
