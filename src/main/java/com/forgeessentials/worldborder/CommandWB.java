package com.forgeessentials.worldborder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Used to check or set the border. Filler will get a sperate command later
 *
 * @author Dries007
 */

public class CommandWB extends ForgeEssentialsCommandBase {
	@Override
	public String getCommandName()
	{
		return "worldborder";
	}

	@Override
	public List<String> getCommandAliases()
	{
		return Arrays.asList(new String[] { "wb" });
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if (FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			return;
		}

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
			{
				OutputHandler.chatError(sender, "As a player, '/wb world' works too");
			}
			return;
		}
		else if (args[0].equalsIgnoreCase("global"))
		{
			zone = APIRegistry.perms.getServerZone();
		}
		else if (FunctionHelper.isNumeric(args[0]))
		{
			World world = DimensionManager.getWorld(parseInt(sender, args[0]));
			if (world == null)
			{
				OutputHandler.chatError(sender, args[0] + " is not an ID of a loaded world.");
				return;
			}
			zone = APIRegistry.perms.getServerZone().getWorldZone(world);
		}
		else if (args[0].equalsIgnoreCase("world") && sender instanceof EntityPlayer)
		{
			zone = APIRegistry.perms.getServerZone().getWorldZone(((EntityPlayer) sender).worldObj);
		}
		else
		{
			OutputHandler.chatError(sender, "You must specify a zone you want to work in. '/wb global' or '/wb <id>'");
			return;
		}

		/*
		 * Now we have the zone...
		 */
		WorldBorder border = ModuleWorldBorder.getBorder(zone.getName(), true);

		/*
		 * Want info?
		 */
		if (args.length == 1 || args[1].equalsIgnoreCase("info"))
		{
			// Header
			String header = "--- WorldBorder for " + zone.getName() + " ---";
			OutputHandler.chatNotification(sender, header);
			// Actual info
			OutputHandler.chatNotification(sender, "Enabled: " + (border.enabled ? EnumChatFormatting.GREEN : EnumChatFormatting.RED) + border.enabled);
			OutputHandler.chatNotification(sender, "Center: " + border.center.toString());
			OutputHandler.chatNotification(sender, "Radius: " + border.rad);
			OutputHandler.chatNotification(sender, "Shape: " + border.getShape());
			// Footer
			StringBuilder footer = new StringBuilder();
			for (int i = 0; i < header.length(); i++)
			{
				footer.append("-");
			}
			OutputHandler.chatNotification(sender, footer.toString());
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
				OutputHandler.chatError(sender, "You have to specify coordinates (x z)" + (sender instanceof EntityPlayer ? " or 'here'." : "."));
			}
			else if (sender instanceof EntityPlayer && args[2].equalsIgnoreCase("here"))
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

		ModuleWorldBorder.saveAll();
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public String getPermissionNode()
	{
		return "fe.worldborder.admin";
	}

    @Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		// Zone selection
		if (args.length == 1)
		{
			ArrayList<String> list = new ArrayList<String>();
			list.add("global");
			if (sender instanceof EntityPlayer)
			{
				list.add("world");
			}
			for (int i : DimensionManager.getIDs())
			{
				list.add("" + i);
			}
			return getListOfStringsMatchingLastWord(args, list);
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

	@Override
	public String getCommandUsage(ICommandSender sender)
	{

		return "/worldborder <global|world|dimID> [info|enable|disable|center|radius|shape] Configure FE WorldBorder.";
	}

	@Override
	public RegisteredPermValue getDefaultPermission()
	{

		return RegisteredPermValue.OP;
	}

}
