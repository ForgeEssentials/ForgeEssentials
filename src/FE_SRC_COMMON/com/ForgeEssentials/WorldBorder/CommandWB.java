package com.ForgeEssentials.WorldBorder;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import com.ForgeEssentials.WorldBorder.ModuleWorldBorder.BorderShape;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Used to check, set and fill the border. You need to activate the module
 * before this command is usable.
 * @author Dries007
 */

public class CommandWB extends ForgeEssentialsCommandBase
{
	public static TickTaskFill	taskGooing	= null;

	@Override
	public String getCommandName()
	{
		return "worldborder";
	}

	@Override
	public List<String> getCommandAliases()
	{
		return Arrays.asList(new String[]
		{ "wb" });
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;

		boolean set = ModuleWorldBorder.set;
		// Info
		if (args.length == 0)
		{
			sender.sendChatToPlayer(Localization.get(Localization.WB_STATUS_HEADER));
			if (set)
			{
				sender.sendChatToPlayer(FEChatFormatCodes.GREEN + Localization.get(Localization.WB_STATUS_BORDERSET));
				sender.sendChatToPlayer("Coordinates :");
				if (ModuleWorldBorder.shape.equals(BorderShape.square))
				{
					sender.sendChatToPlayer("centerX:" + ModuleWorldBorder.X + "  centerZ:" + ModuleWorldBorder.Z);
					sender.sendChatToPlayer("rad:" + ModuleWorldBorder.rad + " Shape: Square");
					sender.sendChatToPlayer("minX:" + ModuleWorldBorder.minX + "  maxX:" + ModuleWorldBorder.maxX);
					sender.sendChatToPlayer("minZ:" + ModuleWorldBorder.minZ + "  maxZ:" + ModuleWorldBorder.maxZ);
				}
				if (ModuleWorldBorder.shape.equals(BorderShape.round))
				{
					sender.sendChatToPlayer("centerX:" + ModuleWorldBorder.X + "  centerZ:" + ModuleWorldBorder.Z);
					sender.sendChatToPlayer("rad:" + ModuleWorldBorder.rad + " Shape: Round");
				}
			}
			else
			{
				sender.sendChatToPlayer(FEChatFormatCodes.RED + Localization.get(Localization.WB_STATUS_BORDERNOTSET));
			}
			return;
		}
		// Fill
		if (args[0].equalsIgnoreCase("fill"))
		{
			if (args.length == 1)
			{
				sender.sendChatToPlayer(FEChatFormatCodes.RED + Localization.get(Localization.WB_LAGWARING));
				sender.sendChatToPlayer(FEChatFormatCodes.RED + Localization.get(Localization.WB_FILL_INFO));
				sender.sendChatToPlayer(Localization.get(Localization.WB_FILL_CONFIRM));
				return;
			}
			if (args[1].equalsIgnoreCase("start"))
			{
				MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
				WorldServer world = server.worldServers[sender.dimension];

				if (taskGooing != null)
				{
					sender.sendChatToPlayer(Localization.get(Localization.WB_FILL_ONLYONCE));
				}
				else
				{
					taskGooing = new TickTaskFill(world);
				}
				return;
			}
			if (args[1].equalsIgnoreCase("continue"))
			{
				MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
				WorldServer world = server.worldServerForDimension(sender.dimension);

				if (taskGooing != null)
				{
					sender.sendChatToPlayer(Localization.get(Localization.WB_FILL_ONLYONCE));
				}
				else
				{
					taskGooing = new TickTaskFill(world);
				}
				return;
			}
			if (args[1].equalsIgnoreCase("cancel"))
			{
				taskGooing.stop();
				return;
			}
			if (args[1].equalsIgnoreCase("message"))
			{
				if (args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("false"))
				{
					TickTaskFill.enablemsg = false;
				}
				else
				{
					TickTaskFill.enablemsg = true;
				}
				return;
			}
		}
		// Set
		if (args[0].equalsIgnoreCase("set") && args.length >= 3)
		{
			BorderShape shape = BorderShape.valueOf(args[1].toLowerCase());
			int rad = parseIntWithMin(sender, args[2], 0);

			if (args.length == 3)
			{
				ModuleWorldBorder.setCenter(rad, (int) sender.posX, (int) sender.posZ, shape, true);
				sender.sendChatToPlayer(Localization.get(Localization.WB_SET).replaceAll("%r", "" + rad).replaceAll("%x", "" + (int) sender.posX).replaceAll("%z", "" + (int) sender.posZ));
				return;
			}
			if (args.length == 5)
			{
				int X = parseInt(sender, args[3]);
				int Z = parseInt(sender, args[4]);

				ModuleWorldBorder.setCenter(rad, X, Z, shape, true);
				sender.sendChatToPlayer(Localization.get(Localization.WB_SET).replaceAll("%r", "" + rad).replaceAll("%x", "" + X).replaceAll("%z", "" + Z));
				return;
			}
		}
		// Command unknown
		OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender));
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		boolean set = ModuleWorldBorder.set;
		// Info
		if (args.length == 0)
		{
			sender.sendChatToPlayer(Localization.get(Localization.WB_STATUS_HEADER));
			if (set)
			{
				sender.sendChatToPlayer(FEChatFormatCodes.GREEN + Localization.get(Localization.WB_STATUS_BORDERSET));
				sender.sendChatToPlayer("Coordinates :");
				if (ModuleWorldBorder.shape.equals(BorderShape.square))
				{
					sender.sendChatToPlayer("minX:" + ModuleWorldBorder.minX + "  maxX:" + ModuleWorldBorder.maxX);
					sender.sendChatToPlayer("minZ:" + ModuleWorldBorder.minZ + "  maxZ:" + ModuleWorldBorder.maxZ);
				}
				if (ModuleWorldBorder.shape.equals(BorderShape.round))
				{
					sender.sendChatToPlayer("centerX:" + ModuleWorldBorder.X + "  centerZ:" + ModuleWorldBorder.Z);
					sender.sendChatToPlayer("rad:" + ModuleWorldBorder.rad);
				}
			}
			else
			{
				sender.sendChatToPlayer(FEChatFormatCodes.RED + Localization.get(Localization.WB_STATUS_BORDERNOTSET));
			}
			return;
		}
		// Fill
		if (args[0].equalsIgnoreCase("fill"))
		{
			if (args.length == 1)
			{
				sender.sendChatToPlayer(FEChatFormatCodes.RED + Localization.get(Localization.WB_LAGWARING));
				sender.sendChatToPlayer(FEChatFormatCodes.RED + Localization.get(Localization.WB_FILL_INFO));
				sender.sendChatToPlayer(Localization.get(Localization.WB_FILL_CONFIRM));
				return;
			}
			if (args[1].equalsIgnoreCase("start"))
			{
				if (args.length != 3)
				{
					sender.sendChatToPlayer(Localization.get(Localization.WB_FILL_CONSOLENEEDSDIM));
					return;
				}
				MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
				int dim = parseInt(sender, args[2]);
				WorldServer world = server.worldServerForDimension(dim);

				if (world != null)
				{
					if (taskGooing != null)
					{
						sender.sendChatToPlayer(Localization.get(Localization.WB_FILL_ONLYONCE));
					}
					else
					{
						taskGooing = new TickTaskFill(world);
					}
				}
				else
				{
					sender.sendChatToPlayer(Localization.get(Localization.WB_FILL_UNLOADEDWOLD));
				}

				return;
			}
			if (args[1].equalsIgnoreCase("continue"))
			{
				if (args.length != 3)
				{
					sender.sendChatToPlayer(Localization.get(Localization.WB_FILL_CONSOLENEEDSDIM));
					return;
				}
				MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
				int dim = parseInt(sender, args[2]);
				WorldServer world = server.worldServers[dim];

				if (taskGooing != null)
				{
					sender.sendChatToPlayer(Localization.get(Localization.WB_FILL_ONLYONCE));
				}
				else
				{
					taskGooing = new TickTaskFill(world);
				}
				return;
			}
			if (args[1].equalsIgnoreCase("cancel"))
			{
				taskGooing.stop();
				return;
			}
			if (args[1].equalsIgnoreCase("message"))
			{
				if (args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("false"))
				{
					TickTaskFill.enablemsg = false;
				}
				else
				{
					TickTaskFill.enablemsg = true;
				}
				return;
			}
		}
		// Set
		if (args[0].equalsIgnoreCase("set") && args.length >= 5)
		{
			BorderShape shape = BorderShape.valueOf(args[1].toLowerCase());
			int rad = parseIntWithMin(sender, args[2], 0);

			if (args.length == 5)
			{
				int X = parseInt(sender, args[3]);
				int Z = parseInt(sender, args[4]);

				ModuleWorldBorder.setCenter(rad, X, Z, shape, true);
				sender.sendChatToPlayer(Localization.get(Localization.WB_SET).replaceAll("%r", "" + rad).replaceAll("%x", "" + X).replaceAll("%z", "" + Z));
				return;
			}
		}
		// Command unknown
		sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.WorldBorder.command";
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, "set", "fill");
		if (args.length == 2 && args[0].equalsIgnoreCase("set"))
			return getListOfStringsMatchingLastWord(args, "square", "round");
		if (args.length == 2 && args[0].equalsIgnoreCase("fill"))
			return getListOfStringsMatchingLastWord(args, "start", "continue", "cancel", "message");
		if (args.length == 3 && args[0].equalsIgnoreCase("fill") && args[1].equalsIgnoreCase("message"))
			return getListOfStringsMatchingLastWord(args, "off", "on");
		return null;
	}

}
