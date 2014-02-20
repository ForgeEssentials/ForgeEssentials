package com.forgeessentials.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.Localization;
import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Allows you to modify a bunch of interesting stuff...
 * @author Dries007
 */

public class CommandCapabilities extends FEcmdModuleCommands
{
	public static ArrayList<String>	names;
	static
	{
		names = new ArrayList<String>();
		names.add("disabledamage");
		names.add("isflying");
		names.add("allowflying");
		names.add("iscreativemode");
		names.add("allowedit");
	}

	@Override
	public String getCommandName()
	{
		return "capabilities";
	}

	/*
	 * Expected syntax /capabilities [player] [capability] [value]
	 */
	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length > 3)
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender));
			return;
		}
		execute(sender, args);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length > 3)
		{
			ChatUtils.sendMessage(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
			return;
		}
		execute(sender, args);
	}

	public void execute(ICommandSender sender, String[] args)
	{
		if (args.length == 0)
		{
			OutputHandler.chatConfirmation(sender, Localization.get("command.capabilities.list"));
			OutputHandler.chatConfirmation(sender, FunctionHelper.niceJoin(names.toArray()));
		}
		else if (args.length == 1)
		{
			EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
			if (player != null)
			{
				OutputHandler.chatConfirmation(sender, Localization.format("command.capabilities.listForX", player.username));
				ChatUtils.sendMessage(sender, names.get(0) + " = " + player.capabilities.disableDamage);
				ChatUtils.sendMessage(sender, names.get(1) + " = " + player.capabilities.isFlying);
				ChatUtils.sendMessage(sender, names.get(2) + " = " + player.capabilities.allowFlying);
				ChatUtils.sendMessage(sender, names.get(3) + " = " + player.capabilities.isCreativeMode);
				ChatUtils.sendMessage(sender, names.get(4) + " = " + player.capabilities.allowEdit);
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			}
		}
		else if (args.length == 2)
		{
			if (sender instanceof EntityPlayer)
			{
				if (!APIRegistry.perms.checkPermAllowed(new PermQueryPlayer((EntityPlayer) sender, getCommandPerm() + ".others")))
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOPERMISSION));
					return;
				}
			}
			EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
			if (player != null)
			{
				if (args[1].equalsIgnoreCase(names.get(0)))
				{
					ChatUtils.sendMessage(sender, player.username + " => " + names.get(0) + " = " + player.capabilities.disableDamage);
				}
				else if (args[1].equalsIgnoreCase(names.get(1)))
				{
					ChatUtils.sendMessage(sender, player.username + " => " + names.get(1) + " = " + player.capabilities.isFlying);
				}
				else if (args[1].equalsIgnoreCase(names.get(2)))
				{
					ChatUtils.sendMessage(sender, player.username + " => " + names.get(2) + " = " + player.capabilities.allowFlying);
				}
				else if (args[1].equalsIgnoreCase(names.get(3)))
				{
					ChatUtils.sendMessage(sender, player.username + " => " + names.get(3) + " = " + player.capabilities.isCreativeMode);
				}
				else if (args[1].equalsIgnoreCase(names.get(4)))
				{
					ChatUtils.sendMessage(sender, player.username + " => " + names.get(4) + " = " + player.capabilities.allowEdit);
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format("command.capabilities.capabilityUnknown", args[1]));
					return;
				}
			}
		}
		else if (args.length == 3)
		{
			if (sender instanceof EntityPlayer)
			{
				if (!APIRegistry.perms.checkPermAllowed(new PermQueryPlayer((EntityPlayer) sender, getCommandPerm() + ".others")))
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOPERMISSION));
					return;
				}
			}
			EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
			if (player != null)
			{
				if (args[1].equalsIgnoreCase(names.get(0)))
				{
					boolean bln = Boolean.parseBoolean(args[2]);
					player.capabilities.disableDamage = bln;
					ChatUtils.sendMessage(sender, names.get(0) + " = " + player.capabilities.disableDamage);
				}
				else if (args[1].equalsIgnoreCase(names.get(1)))
				{
					boolean bln = Boolean.parseBoolean(args[2]);
					player.capabilities.isFlying = bln;
					ChatUtils.sendMessage(sender, names.get(1) + " = " + player.capabilities.isFlying);
				}
				else if (args[1].equalsIgnoreCase(names.get(2)))
				{
					boolean bln = Boolean.parseBoolean(args[2]);
					player.capabilities.allowFlying = bln;
					ChatUtils.sendMessage(sender, names.get(2) + " = " + player.capabilities.allowFlying);
				}
				else if (args[1].equalsIgnoreCase(names.get(3)))
				{
					boolean bln = Boolean.parseBoolean(args[2]);
					player.capabilities.isCreativeMode = bln;
					ChatUtils.sendMessage(sender, names.get(3) + " = " + player.capabilities.isCreativeMode);
				}
				else if (args[1].equalsIgnoreCase(names.get(4)))
				{
					boolean bln = Boolean.parseBoolean(args[2]);
					player.capabilities.allowEdit = bln;
					ChatUtils.sendMessage(sender, names.get(4) + " = " + player.capabilities.allowEdit);
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format("command.capabilities.capabilityUnknown", args[1]));
					return;
				}
				player.sendPlayerAbilities();
			}
		}
	}

	@Override
	public void registerExtraPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel(getCommandPerm() + ".others", RegGroup.OWNERS);
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
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		else if (args.length == 2)
			return getListOfStringsFromIterableMatchingLastWord(args, names);
		else if (args.length == 3)
			return getListOfStringsMatchingLastWord(args, "true", "false");
		else
			return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
