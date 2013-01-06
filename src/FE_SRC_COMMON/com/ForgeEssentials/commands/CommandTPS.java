package com.ForgeEssentials.commands;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.TcpConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

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
		return Arrays.asList(new String[] { "lag" });
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

		return (((double) var2 / (double) par1ArrayOfLong.length) * 1.0E-6D);
	}

	public String getTPS(long[] par1ArrayOfLong)
	{
		double tps = (func_79015_a(par1ArrayOfLong));
		if (tps < 50)
		{
			return "20";
		} else
		{
			return DF.format((1000 / tps));
		}
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		MinecraftServer server = FMLCommonHandler.instance()
				.getMinecraftServerInstance();
		if (args.length == 0)
		{
			long var1 = Runtime.getRuntime().totalMemory()
					- Runtime.getRuntime().freeMemory();

			sender.sendChatToPlayer("Memory use: " + var1 / 1024L / 1024L
					+ " mb (" + Runtime.getRuntime().freeMemory() * 100L
					/ Runtime.getRuntime().maxMemory() + "% free)");
			sender.sendChatToPlayer("Threads: "
					+ TcpConnection.field_74471_a.get() + " + "
					+ TcpConnection.field_74469_b.get());
			sender.sendChatToPlayer("Avg tick: " + getTPS(server.tickTimeArray));
			sender.sendChatToPlayer("Avg sent: "
					+ (int) func_79015_a(server.sentPacketCountArray)
					+ ", Avg size: "
					+ (int) func_79015_a(server.sentPacketSizeArray));
			sender.sendChatToPlayer("Avg rec: "
					+ (int) func_79015_a(server.receivedPacketCountArray)
					+ ", Avg size: "
					+ (int) func_79015_a(server.receivedPacketSizeArray));
			return;
		}
		if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("all"))
			{
				if (server.worldServers != null)
				{
					int x = 0;
					for (Integer id : DimensionManager.getIDs())
					{
						sender.sendChatToPlayer("Lvl " + id + " TPS: "
								+ getTPS(server.worldTickTimes.get(id)));
						x++;
					}
				}
				return;
			} else
			{
				int dim = parseIntBounded(sender, args[0],
						DimensionManager.getIDs()[0],
						(DimensionManager.getNextFreeDimId() - 1));
				sender.sendChatToPlayer("Lvl " + dim + " TPS: "
						+ getTPS(server.worldTickTimes.get(dim)));
			}
			return;
		}
		OutputHandler
				.chatError(
						sender,
						(Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender)));
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		MinecraftServer server = FMLCommonHandler.instance()
				.getMinecraftServerInstance();
		if (args.length == 0)
		{
			long var1 = Runtime.getRuntime().totalMemory()
					- Runtime.getRuntime().freeMemory();

			sender.sendChatToPlayer("Memory use: " + var1 / 1024L / 1024L
					+ " mb (" + Runtime.getRuntime().freeMemory() * 100L
					/ Runtime.getRuntime().maxMemory() + "% free)");
			sender.sendChatToPlayer("Threads: "
					+ TcpConnection.field_74471_a.get() + " + "
					+ TcpConnection.field_74469_b.get());
			sender.sendChatToPlayer("Avg tick: " + getTPS(server.tickTimeArray));
			sender.sendChatToPlayer("Avg sent: "
					+ (int) func_79015_a(server.sentPacketCountArray)
					+ ", Avg size: "
					+ (int) func_79015_a(server.sentPacketSizeArray));
			sender.sendChatToPlayer("Avg rec: "
					+ (int) func_79015_a(server.receivedPacketCountArray)
					+ ", Avg size: "
					+ (int) func_79015_a(server.receivedPacketSizeArray));
			return;
		}
		if (args.length == 1)
		{
			if (args[0].equalsIgnoreCase("all"))
			{
				if (server.worldServers != null)
				{
					int x = 0;
					for (Integer id : DimensionManager.getIDs())
					{
						sender.sendChatToPlayer("Lvl " + id + " TPS: "
								+ getTPS(server.worldTickTimes.get(id)));
						x++;
					}
				}
				return;
			} else
			{
				int dim = parseIntBounded(sender, args[0],
						DimensionManager.getIDs()[0],
						(DimensionManager.getNextFreeDimId() - 1));
				sender.sendChatToPlayer("Lvl " + dim + " TPS: "
						+ getTPS(server.worldTickTimes.get(dim)));
			}
			return;
		}
		sender.sendChatToPlayer((Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole()));
	}

	@Override
	public boolean canConsoleUseCommand()
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
		return getListOfStringsMatchingLastWord(args, "all");
	}
}
