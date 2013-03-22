package com.ForgeEssentials.WorldBorder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Used to check or set the border.
 * Filler will get a sperate command later
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
		
		execute(sender, args);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		
		execute(sender, args);
	}

	public void execute(ICommandSender sender, String[] args)
	{
		/*
		 * We need the zone...
		 */
		Zone zone;
		if (args.length == 0)
		{
			OutputHandler.chatError(sender, "You must specify a zone you want to work in. '/wb global' or '/wb <id>'");
			if (sender instanceof EntityPlayer)
				OutputHandler.chatError(sender, "As a player, '/wb world' works too");
			return;
		}
		else if (args[0].equalsIgnoreCase("global"))
		{
			zone = ZoneManager.getGLOBAL();
		}
		else if (FunctionHelper.isNumeric(args[0]))
		{
			World world = DimensionManager.getWorld(parseInt(sender, args[0]));
			if(world == null)
			{
				OutputHandler.chatError(sender, args[0] + " is not an ID of a loaded world.");
				return;
			}
			zone = ZoneManager.getWorldZone(world);
		}
		else if (args[0].equalsIgnoreCase("world") && (sender instanceof EntityPlayer))
		{
			zone = ZoneManager.getWorldZone(((EntityPlayer) sender).worldObj);
		}
		else
		{
			OutputHandler.chatError(sender, "You must specify a zone you want to work in. '/wb global' or '/wb <id>'");
			return;
		}
		
		/*
		 * Now we have the zone...
		 */
		WorldBorder border = ModuleWorldBorder.borderMap.get(zone.getZoneName());
		
		/*
		 * Want info? 
		 */
		if (args.length == 1 || args[1].equalsIgnoreCase("info"))
		{
			// Header
			String header = "--- WorldBorder for " + zone.getZoneName() + " ---";
			sender.sendChatToPlayer(header);
			// Actual info		
			sender.sendChatToPlayer("Enabled: " + (border.enabled ? FEChatFormatCodes.GREEN : FEChatFormatCodes.RED) + border.enabled);
			sender.sendChatToPlayer("Center: " + border.center.toString());
			sender.sendChatToPlayer("Radius: " + border.rad);
			sender.sendChatToPlayer("Shape: " + border.getShape());
			// Footer
			StringBuilder footer = new StringBuilder();
			for (int i = 0; i < header.length(); i++)
			{
				footer.append("-");
			}
			sender.sendChatToPlayer(footer.toString());
		}
		/*
		 * No. Want to en|disable?
		 */
		else if (args[1].equalsIgnoreCase("enable"))
		{
			if (border.shapeByte != 0 && border.rad != 0)
			{
				border.enabled = true;
				OutputHandler.chatConfirmation(sender, "Border has been enabled.");
			}
			else
			{
				OutputHandler.chatError(sender, "You have to set a center, radius and shape first!");
			}
		}
		else if (args[1].equalsIgnoreCase("disable"))
		{
			if (border.shapeByte != 0 && border.rad != 0)
			{
				border.enabled = false;
				OutputHandler.chatConfirmation(sender, "Border has been disabled.");
			}
			else
			{
				OutputHandler.chatError(sender, "You have to set a center, radius and shape first!");
			}
		}
		/*
		 * No. Center maybe?
		 */
		else if (args[1].equalsIgnoreCase("center"))
		{
			if (args.length == 2)
			{
				OutputHandler.chatError(sender, "You have to specify coordinates (x z)" + ((sender instanceof EntityPlayer) ? " or 'here'." : "."));
			}
			else if ((sender instanceof EntityPlayer) && args[2].equalsIgnoreCase("here"))
			{
				border.center = new Point((EntityPlayer) sender);
				OutputHandler.chatConfirmation(sender, "Center set to " + border.center);
			}
			else
			{
				if (args.length == 4)
				{
					int x = parseInt(sender, args[2]);
					int z = parseInt(sender, args[3]);
					border.center = new Point(x, 64, z);
					OutputHandler.chatConfirmation(sender, "Center set to " + border.center);	
				}
				else
				{
					OutputHandler.chatError(sender, "Expected '/wb " + args[0] + " center <x> <z>'");
				}
			}
		}
		/*
		 * No. How about radius?
		 */
		else if (args[1].equalsIgnoreCase("rad") || args[1].equalsIgnoreCase("radius"))
		{
			if (args.length == 2)
			{
				OutputHandler.chatError(sender, "You have to specify a radius...");
			}
			else
			{
				border.rad = parseIntWithMin(sender, args[2], 0);
				OutputHandler.chatConfirmation(sender, "You have set the radius to " + border.rad);
			}
		}
		/*
		 * Last option... Shape?
		 */
		else if (args[1].equalsIgnoreCase("shape"))
		{
			if (args.length == 2)
			{
				OutputHandler.chatError(sender, "You have to set the boder to 'round' or 'square'.");
			}
			else if (args[2].equalsIgnoreCase("square"))
			{
				border.shapeByte = 1;
				OutputHandler.chatConfirmation(sender, "You have set the border to " + border.getShape());
			}
			else if (args[2].equalsIgnoreCase("round"))
			{
				border.shapeByte = 2;
				OutputHandler.chatConfirmation(sender, "You have set the border to " + border.getShape());
			}
		}
		/*
		 * dafuq?
		 */
		else
		{
			OutputHandler.chatError(sender, "dafuq? I have no clue what you are trying to do. Use TAB for cmd filling!");
			OutputHandler.chatError(sender, "/wb <zone> [info|enable|disable|center|radius|shape]");
		}
	}
	
	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.WorldBorder.admin";
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		// Zone selection
		if (args.length == 1)
		{
			ArrayList<String> list = new ArrayList<String>();
			list.add("global");
			if(sender instanceof EntityPlayer)
				list.add("world");
			for (int i : DimensionManager.getIDs())
				list.add("" + i);
			return getListOfStringsFromIterableMatchingLastWord(args, list);
		}
		// Options
		if (args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, "info", "enable", "disable", "center", "radius", "shape");
		}
		// If shape...
		if (args.length == 3 && args[2].equalsIgnoreCase("shape"))
		{
			return getListOfStringsMatchingLastWord(args, "square", "round");
		}
		return null;
	}

}
