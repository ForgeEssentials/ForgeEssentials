package com.ForgeEssentials.commands;

import java.util.HashMap;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandPotion extends ForgeEssentialsCommandBase
{
	public static HashMap<String, Integer> names;
	static
	{
		names = new HashMap<String, Integer>();
		names.put("speed", 1);
		names.put("slowness", 2);
		names.put("haste", 3);
		names.put("miningfatigue", 4);
		names.put("strength", 5);
		names.put("heal", 6);
		names.put("damage", 7);
		names.put("jumpboost", 8);
		names.put("nausea", 9);
		names.put("regeneration", 10);
		names.put("resistance", 11);
		names.put("fireresistance", 12);
		names.put("waterbreathing", 13);
		names.put("invisibility", 14);
		names.put("blindness", 15);
		names.put("nightvision", 16);
		names.put("hunger", 17);
		names.put("weakness", 18);
		names.put("poison", 19);
		names.put("wither", 20);
	}

	@Override
	public String getCommandName()
	{
		return "potion";
	}

	/*
	 * Expected syntax: /potion player effect duration [ampl]
	 */

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		EntityPlayerMP target = null;
		int ID = 0;
		int dur = 0;
		int ampl = 0;

		if (args.length == 4)
		{
			ampl = parseIntWithMin(sender, args[3], 0);
		}
		else if (args.length != 3)
		{
			OutputHandler.chatError(sender, (Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender)));
			return;
		}

		if (args[0].equalsIgnoreCase("me"))
		{
			target = (EntityPlayerMP) sender;
		}
		else if(PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
		{
			target = FunctionHelper.getPlayerFromPartialName(args[0]);
			if(PlayerSelector.hasArguments(args[0]))
			{
				target = PlayerSelector.matchOnePlayer(sender, args[0]);
			}
		}

		if (names.containsKey(args[1]))
		{
			ID = names.get(args[1]);
		}
		else
		{
			OutputHandler.chatError(sender, Localization.get(Localization.POTIONEFFECTNOTFOUND));
			return;
		}

		dur = parseIntWithMin(sender, args[2], 0) * 20;

		PotionEffect eff = new PotionEffect(ID, dur, ampl);
		if(target != null)
		{
			target.addPotionEffect(eff);
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		EntityPlayerMP target;
		int ID = 0;
		int dur = 0;
		int ampl = 0;

		if (args.length == 4)
		{
			ampl = parseIntWithMin(sender, args[3], 0);
		}
		else if (args.length != 3)
		{
			sender.sendChatToPlayer((Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole()));
			return;
		}

		target = FunctionHelper.getPlayerFromPartialName(args[0]);
		if(PlayerSelector.hasArguments(args[0]))
		{
			target = PlayerSelector.matchOnePlayer(sender, args[0]);
		}
		
		if (names.containsKey(args[1]))
		{
			ID = names.get(args[1]);
		}
		else
		{
			sender.sendChatToPlayer(Localization.get(Localization.POTIONEFFECTNOTFOUND));
			return;
		}

		dur = parseIntWithMin(sender, args[2], 0) * 20;

		PotionEffect eff = new PotionEffect(ID, dur, ampl);
		target.addPotionEffect(eff);
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
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		}
		else if (args.length == 2)
		{
			return getListOfStringsFromIterableMatchingLastWord(args, names.keySet());
		}
		else
		{
			return null;
		}
	}

}
