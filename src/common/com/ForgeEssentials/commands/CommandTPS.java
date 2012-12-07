package com.ForgeEssentials.commands;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.permission.query.PermQueryPlayer;
import com.ForgeEssentials.util.DataStorage;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;

import cpw.mods.fml.common.FMLCommonHandler;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TcpConnection;
import net.minecraftforge.common.DimensionManager;

public class CommandTPS extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "tps";
	}
	
	@Override
	public List getCommandAliases()
    {
		return Arrays.asList(new String[] {"TPS", "lag"});
    }
	
	private static final DecimalFormat DF = new DecimalFormat("########0.000");
	/**
	 * 
	 * @param par1ArrayOfLong
	 * @return amount of time for 1 tick in ms
	 */
	private double func_79015_a(long[] par1ArrayOfLong)
    {
        long var2 = 0L;
        long[] var4 = par1ArrayOfLong;
        int var5 = par1ArrayOfLong.length;

        for (int var6 = 0; var6 < var5; ++var6)
        {
            long var7 = var4[var6];
            var2 += var7;
        }

        return (((double)var2 / (double)par1ArrayOfLong.length) * 1.0E-6D);
    }
	
	public double getTPS()
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		return (this.func_79015_a(server.tickTimeArray));
		//return ;
	}
	
	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		double tps = getTPS();
		if(tps < 50)
		{
			
			sender.sendChatToPlayer("TPS: 20");
		}
		else
		{
			sender.sendChatToPlayer("TPS: " + DF.format((1000/tps)));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		double tps = getTPS();
		if(tps < 50)
		{	
			sender.sendChatToPlayer("TPS: 20");
		}
		else
		{
			sender.sendChatToPlayer("TPS: " + DF.format((1000/tps)));
		}
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
		return null;
    }
}
