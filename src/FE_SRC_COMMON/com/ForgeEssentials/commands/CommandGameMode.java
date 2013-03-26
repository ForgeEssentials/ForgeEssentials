package com.ForgeEssentials.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.PlayerSelector;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.StatCollector;
import net.minecraft.world.EnumGameType;

import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandGameMode extends FEcmdModuleCommands
{
	@Override
	public String getCommandName()
	{
		return "gamemode";
	}

	@Override
	public String[] getDefaultAliases()
	{
		return new String[]
		{ "gm" };
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		EnumGameType gm;
		EntityPlayer target = sender;

		// no arguments? toggle gamemode.
		if (args.length == 0)
		{
			gm = getToggledType(target);
			target.setGameType(gm);
			target.fallDistance = 0.0F;
			String modeName = StatCollector.translateToLocal("gameMode." + gm.getName());
			OutputHandler.chatConfirmation(sender, Localization.format("command.gamemode.changed", target.username, modeName));
			return;
		}

		gm = getGameTypeFromString(sender, args[0]);

		// 1 argument? try gamemode.
		if (args.length == 1)
		{
			// not a gamemode? try a player,
			if (gm == null)
			{
				// throws exception if there is no player
				target = func_82359_c(sender, args[0]);

				gm = getToggledType(target);
			}

			target.setGameType(gm);
			target.fallDistance = 0.0F;
			String modeName = StatCollector.translateToLocal("gameMode." + gm.getName());
			OutputHandler.chatConfirmation(sender, Localization.format("command.gamemode.changed", target.username, modeName));
			return;
		}

		// default to survival if cannot be parsed
		if (gm == null)
		{
			gm = EnumGameType.SURVIVAL;
		}

		// 2 arguments? do ./GameMode <mode> <player>
		if (args.length == 2)
		{
			// throws exception if there is no player
			target = FunctionHelper.getPlayerForName(sender, args[1]);
			if (target == null)
				throw new PlayerNotFoundException();

			target.setGameType(gm);
			target.fallDistance = 0.0F;
			String modeName = StatCollector.translateToLocal("gameMode." + gm.getName());
			OutputHandler.chatConfirmation(sender, Localization.format("command.gamemode.changed", target.username, modeName));
			return;
		}

		// > 2 arguments? do ./GameMode <mode> <players>
		if (args.length > 2)
		{
			ArrayList<EntityPlayerMP> players = new ArrayList();
			EntityPlayerMP player;
			for (int i = 1; i < args.length; i++)
			{
				player = FunctionHelper.getPlayerForName(sender, args[i]);
				if (player != null)
					players.add(player);
			}

			if (players.isEmpty())
				throw new PlayerNotFoundException();

			String modeName = StatCollector.translateToLocal("gameMode." + gm.getName());

			for (EntityPlayerMP victim : players)
			{
				victim.setGameType(gm);
				victim.fallDistance = 0.0F;
			}

			OutputHandler.chatConfirmation(sender, Localization.format("command.gamemode.changed", "all specified players", modeName));
			return;
		}

		throw new WrongUsageException("commands.gamemode.usage");

	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		EnumGameType gm;
		EntityPlayer target;

		// no arguments? toggle gamemode.
		if (args.length == 0)
			throw new WrongUsageException("commands.gamemode.usage");

		gm = getGameTypeFromString(sender, args[0]);

		// 1 argument? try player
		if (args.length == 1)
		{
			// throws exception if there is no player
			target = func_82359_c(sender, args[0]);

			gm = getToggledType(target);

			target.setGameType(gm);
			target.fallDistance = 0.0F;
			String modeName = StatCollector.translateToLocal("gameMode." + gm.getName());
			OutputHandler.chatConfirmation(sender, Localization.format("command.gamemode.changed", target.username, modeName));
			return;
		}

		// default to survival if cannot be parsed
		if (gm == null)
		{
			gm = EnumGameType.SURVIVAL;
		}

		// 2 arguments? do ./GameMode <mode> <player>
		if (args.length == 2)
		{
			// throws exception if there is no player
			target = func_82359_c(sender, args[0]);

			target.setGameType(gm);
			target.fallDistance = 0.0F;
			String modeName = StatCollector.translateToLocal("gameMode." + gm.getName());
			OutputHandler.chatConfirmation(sender, Localization.format("command.gamemode.changed", target.username, modeName));
			return;
		}

		// > 2 arguments? do ./GameMode <mode> <players>
		if (args.length > 2)
		{
			EntityPlayer[] players = PlayerSelector.matchPlayers(sender, args[1]);

			if (players == null || players.length == 0)
				throw new PlayerNotFoundException();

			String modeName = StatCollector.translateToLocal("gameMode." + gm.getName());

			for (EntityPlayer player : players)
			{
				player.setGameType(gm);
				player.fallDistance = 0.0F;
			}

			OutputHandler.chatConfirmation(sender, Localization.format("command.gamemode.changed", "all specified players", modeName));
			return;
		}

		throw new WrongUsageException("commands.gamemode.usage");
	}

	private EnumGameType getGameTypeFromString(ICommandSender sender, String string)
	{
		if (string.equalsIgnoreCase(EnumGameType.SURVIVAL.getName()) || string.equalsIgnoreCase("s") || string.equals("0"))
			return EnumGameType.SURVIVAL;
		else if (string.equalsIgnoreCase(EnumGameType.CREATIVE.getName()) || string.equalsIgnoreCase("c") || string.equals("1"))
			return EnumGameType.CREATIVE;
		else if (string.equalsIgnoreCase(EnumGameType.ADVENTURE.getName()) || string.equalsIgnoreCase("a") || string.equals("2"))
			return EnumGameType.ADVENTURE;
		else
			return null;
	}

	private EnumGameType getToggledType(EntityPlayer player)
	{
		if (player.capabilities.isCreativeMode)
			return EnumGameType.SURVIVAL;
		else
			return EnumGameType.CREATIVE;
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
		if (args.length == 2)
			return getListOfStringsMatchingLastWord(args, new String[]
			{ "survival", "creative", "adventure" });
		else if (args.length == 1)
		{
			List<?> match = getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
			if (match == null)
			{
				match = getListOfStringsMatchingLastWord(args, new String[]
				{ "survival", "creative", "adventure" });
			}
			return match;
		}
		else
			return null;
	}

	@Override
	public void registerExtraPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel(getCommandPerm() + ".others", RegGroup.OWNERS);
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}
}
