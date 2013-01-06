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
import com.ForgeEssentials.util.TickTaskHandler;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Used to check, set and fill the border. You need to activate the module
 * before this command is usable.
 * 
 * @author Dries007
 * 
 */

public class CommandWB extends ForgeEssentialsCommandBase
{
	public static TickTaskFill taskGooing = null;

	@Override
	public String getCommandName()
	{
		return "worldborder";
	}

	@Override
	public List getCommandAliases()
	{
		return Arrays.asList(new String[] { "wb" });
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		boolean set = ModuleWorldBorder.set;
		// Info
		if (args.length == 0)
		{
			sender.sendChatToPlayer(Localization
					.get(Localization.WB_STATUS_HEADER));
			if (set)
			{
				sender.sendChatToPlayer(FEChatFormatCodes.GREEN
						+ Localization.get(Localization.WB_STATUS_BORDERSET));
				sender.sendChatToPlayer("Coordinates :");
				if (ModuleWorldBorder.shape.equals(BorderShape.square))
				{
					sender.sendChatToPlayer("centerX:" + ModuleWorldBorder.X
							+ "  centerZ:" + ModuleWorldBorder.Z);
					sender.sendChatToPlayer("rad:" + ModuleWorldBorder.rad
							+ " Shape: Square");
					sender.sendChatToPlayer("minX:" + ModuleWorldBorder.minX
							+ "  maxX:" + ModuleWorldBorder.maxX);
					sender.sendChatToPlayer("minZ:" + ModuleWorldBorder.minZ
							+ "  maxZ:" + ModuleWorldBorder.maxZ);
				}
				if (ModuleWorldBorder.shape.equals(BorderShape.round))
				{
					sender.sendChatToPlayer("centerX:" + ModuleWorldBorder.X
							+ "  centerZ:" + ModuleWorldBorder.Z);
					sender.sendChatToPlayer("rad:" + ModuleWorldBorder.rad
							+ " Shape: Round");
				}
			} else
			{
				sender.sendChatToPlayer(FEChatFormatCodes.RED
						+ Localization.get(Localization.WB_STATUS_BORDERNOTSET));
			}
			return;
		}
		// Fill
		if (args[0].equalsIgnoreCase("fill"))
		{
			if (args.length == 1)
			{
				sender.sendChatToPlayer(FEChatFormatCodes.RED
						+ Localization.get(Localization.WB_LAGWARING));
				sender.sendChatToPlayer(FEChatFormatCodes.RED
						+ Localization.get(Localization.WB_FILL_INFO));
				sender.sendChatToPlayer(Localization
						.get(Localization.WB_FILL_CONFIRM));
				return;
			}
			if (args[1].equalsIgnoreCase("ok"))
			{
				MinecraftServer server = FMLCommonHandler.instance()
						.getMinecraftServerInstance();
				WorldServer world = server.worldServers[sender.dimension];

				if (taskGooing != null)
				{
					sender.sendChatToPlayer(Localization
							.get(Localization.WB_FILL_ONLYONCE));
				} else
				{
					if (ModuleWorldBorder.shape == BorderShape.round)
					{
						taskGooing = new TickTaskFillRound(world);
					}
					if (ModuleWorldBorder.shape == BorderShape.square)
					{
						taskGooing = new TickTaskFillSquare(world);
					}
					TickTaskHandler.addTask(taskGooing);
				}
				return;
			}
			if (args[1].equalsIgnoreCase("cancel"))
			{
				taskGooing.stop();
				return;
			}
		}
		// Autopilot
		if (args[0].equalsIgnoreCase("autopilot"))
		{
			if (args.length == 1)
			{
				sender.sendChatToPlayer(FEChatFormatCodes.RED
						+ Localization.get(Localization.WB_AUTO_INFO));
				sender.sendChatToPlayer(Localization
						.get(Localization.WB_AUTO_CONFIRM));
				return;
			}
			if (args[1].equalsIgnoreCase("off"))
			{
				if (taskGooing != null)
				{
					taskGooing.disengageAutopilot();
				} else
				{
					sender.sendChatToPlayer(FEChatFormatCodes.RED
							+ Localization.get(Localization.WB_NOTHINGTODO));
				}
				return;
			}
			if (args.length == 2)
			{
				int speed = parseIntBounded(sender, args[1], 1, 20);
				taskGooing.engageAutopilot(speed);
				return;
			}
		}
		// Turbo
		if (args[0].equalsIgnoreCase("turbo"))
		{
			if (args.length == 1)
			{
				sender.sendChatToPlayer(FEChatFormatCodes.RED
						+ Localization.get(Localization.WB_LAGWARING));
				sender.sendChatToPlayer(FEChatFormatCodes.RED
						+ Localization.get(Localization.WB_TURBO_INFO));
				sender.sendChatToPlayer(Localization
						.get(Localization.WB_TURBO_CONFIRM));
				return;
			}
			if (args[1].equalsIgnoreCase("off"))
			{
				if (taskGooing != null)
				{
					taskGooing.disengageTurbo();
				} else
				{
					sender.sendChatToPlayer(FEChatFormatCodes.RED
							+ Localization.get(Localization.WB_NOTHINGTODO));
				}
				return;
			}
			try
			{
				int speed = Integer.parseInt(args[1]);
				taskGooing.engageTurbo(speed);
				return;
			} catch (Exception e)
			{
				OutputHandler.chatError(sender,
						(Localization.get(Localization.ERROR_NAN)));
			}
		}
		// Set
		if (args[0].equalsIgnoreCase("set") && args.length >= 3)
		{
			BorderShape shape = BorderShape.valueOf(args[1].toLowerCase());
			int rad = parseIntWithMin(sender, args[2], 0);

			if (args.length == 3)
			{
				ModuleWorldBorder.setCenter(rad, (int) sender.posX,
						(int) sender.posZ, shape, true);
				sender.sendChatToPlayer(Localization.get(Localization.WB_SET)
						.replaceAll("%r", "" + rad)
						.replaceAll("%x", "" + (int) sender.posX)
						.replaceAll("%z", "" + (int) sender.posZ));
				return;
			}
			if (args.length == 4)
			{
				int X = parseInt(sender, args[3]);
				int Z = parseInt(sender, args[4]);

				ModuleWorldBorder.setCenter(rad, X, Z, shape, true);
				sender.sendChatToPlayer(Localization.get(Localization.WB_SET)
						.replaceAll("%r", "" + rad).replaceAll("%x", "" + X)
						.replaceAll("%z", "" + Z));
				return;
			}
		}
		// Command unknown
		OutputHandler
				.chatError(
						sender,
						(Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender)));
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		boolean set = ModuleWorldBorder.set;
		// Info
		if (args.length == 0)
		{
			sender.sendChatToPlayer(Localization
					.get(Localization.WB_STATUS_HEADER));
			if (set)
			{
				sender.sendChatToPlayer(FEChatFormatCodes.GREEN
						+ Localization.get(Localization.WB_STATUS_BORDERSET));
				sender.sendChatToPlayer("Coordinates :");
				if (ModuleWorldBorder.shape.equals(BorderShape.square))
				{
					sender.sendChatToPlayer("minX:" + ModuleWorldBorder.minX
							+ "  maxX:" + ModuleWorldBorder.maxX);
					sender.sendChatToPlayer("minZ:" + ModuleWorldBorder.minZ
							+ "  maxZ:" + ModuleWorldBorder.maxZ);
				}
				if (ModuleWorldBorder.shape.equals(BorderShape.round))
				{
					sender.sendChatToPlayer("centerX:" + ModuleWorldBorder.X
							+ "  centerZ:" + ModuleWorldBorder.Z);
					sender.sendChatToPlayer("rad:" + ModuleWorldBorder.rad);
				}
			} else
			{
				sender.sendChatToPlayer(FEChatFormatCodes.RED
						+ Localization.get(Localization.WB_STATUS_BORDERNOTSET));
			}
			return;
		}
		// Fill
		if (args[0].equalsIgnoreCase("fill"))
		{
			if (ModuleWorldBorder.shape == BorderShape.round)
			{
				// TODO Make the filler
				sender.sendChatToPlayer("Not done yet!");
				return;
			}
			if (args.length == 1)
			{
				sender.sendChatToPlayer(FEChatFormatCodes.RED
						+ Localization.get(Localization.WB_LAGWARING));
				sender.sendChatToPlayer(FEChatFormatCodes.RED
						+ Localization.get(Localization.WB_FILL_INFO));
				sender.sendChatToPlayer(Localization
						.get(Localization.WB_FILL_CONFIRM));
				return;
			}
			if (args[1].equalsIgnoreCase("ok"))
			{
				if (args.length != 3)
				{
					sender.sendChatToPlayer(Localization
							.get(Localization.WB_FILL_CONSOLENEEDSDIM));
					return;
				}
				MinecraftServer server = FMLCommonHandler.instance()
						.getMinecraftServerInstance();
				int dim = parseInt(sender, args[2]);
				WorldServer world = server.worldServers[dim];

				if (taskGooing != null)
				{
					sender.sendChatToPlayer(Localization
							.get(Localization.WB_FILL_ONLYONCE));
				} else
				{
					world.canNotSave = true;
					if (ModuleWorldBorder.shape == BorderShape.round)
					{
						taskGooing = new TickTaskFillRound(world);
					}
					if (ModuleWorldBorder.shape == BorderShape.square)
					{
						taskGooing = new TickTaskFillSquare(world);
					}
					TickTaskHandler.addTask(taskGooing);
				}
				return;
			}
			if (args[1].equalsIgnoreCase("cancel"))
			{
				taskGooing.stop();
				return;
			}
		}
		// Autopilot
		if (args[0].equalsIgnoreCase("autopilot"))
		{
			if (args.length == 1)
			{
				sender.sendChatToPlayer(FEChatFormatCodes.RED
						+ Localization.get(Localization.WB_AUTO_INFO));
				sender.sendChatToPlayer(Localization
						.get(Localization.WB_AUTO_CONFIRM));
				return;
			}
			if (args[1].equalsIgnoreCase("off"))
			{
				if (taskGooing != null)
				{
					taskGooing.disengageAutopilot();
				} else
				{
					sender.sendChatToPlayer(FEChatFormatCodes.RED
							+ Localization.get(Localization.WB_NOTHINGTODO));
				}
				return;
			}
			if (args.length == 2)
			{
				int speed = parseIntBounded(sender, args[1], 1, 20);
				taskGooing.engageAutopilot(speed);
				return;
			}
		}
		// Turbo
		if (args[0].equalsIgnoreCase("turbo"))
		{
			if (args.length == 1)
			{
				sender.sendChatToPlayer(FEChatFormatCodes.RED
						+ Localization.get(Localization.WB_LAGWARING));
				sender.sendChatToPlayer(FEChatFormatCodes.RED
						+ Localization.get(Localization.WB_TURBO_INFO));
				sender.sendChatToPlayer(Localization
						.get(Localization.WB_TURBO_CONFIRM));
				return;
			}
			if (args[1].equalsIgnoreCase("off"))
			{
				if (taskGooing != null)
				{
					taskGooing.disengageTurbo();
				} else
				{
					sender.sendChatToPlayer(FEChatFormatCodes.RED
							+ Localization.get(Localization.WB_NOTHINGTODO));
				}
				return;
			}
			try
			{
				int speed = Integer.parseInt(args[1]);
				taskGooing.engageTurbo(speed);
				return;
			} catch (Exception e)
			{
				sender.sendChatToPlayer(Localization
						.get(Localization.ERROR_NAN));
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
				sender.sendChatToPlayer(Localization.get(Localization.WB_SET)
						.replaceAll("%r", "" + rad).replaceAll("%x", "" + X)
						.replaceAll("%z", "" + Z));
				return;
			}
		}
		// Command unknown
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
		return "ForgeEssentials.worldborder.command";
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, "set", "fill",
					"turbo", "autopilot");
		}
		if (args.length == 2 && args[0].equalsIgnoreCase("set"))
		{
			return getListOfStringsMatchingLastWord(args, "square", "round");
		}
		if (args.length == 2 && args[0].equalsIgnoreCase("fill"))
		{
			return getListOfStringsMatchingLastWord(args, "ok", "cancel");
		}
		if (args.length == 2 && args[0].equalsIgnoreCase("turbo"))
		{
			return getListOfStringsMatchingLastWord(args, "off");
		}
		if (args.length == 2 && args[0].equalsIgnoreCase("autopilot"))
		{
			return getListOfStringsMatchingLastWord(args, "off");
		}
		return null;
	}

}