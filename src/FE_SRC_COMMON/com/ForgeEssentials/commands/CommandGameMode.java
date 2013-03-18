package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.WorldSettings;

import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
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
		if (args.length < 2)
		{
			if (args.length == 1)
			{
				if (args[0].equals("0") || args[0].equals("1") || args[0].equals("2") || args[0].equalsIgnoreCase(EnumGameType.CREATIVE.getName()) || args[0].equalsIgnoreCase(EnumGameType.SURVIVAL.getName())
						|| args[0].equalsIgnoreCase(EnumGameType.ADVENTURE.getName()) || args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("s"))
				{
					sender.setGameType(getGameTypeFromString(sender, args[0]));
				}
				else if (FunctionHelper.getPlayerFromPartialName(args[0]) != null || PlayerSelector.hasArguments(args[0]))
				{
					if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
					{
						OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOPERMISSION));
						return;
					}
					List<EntityPlayerMP> players = Arrays.asList(FunctionHelper.getPlayerFromPartialName(args[0]));
					if (PlayerSelector.hasArguments(args[0]))
					{
						players = Arrays.asList(PlayerSelector.matchPlayers(sender, args[0]));
					}
					if (players.size() != 0)
					{
						for (EntityPlayer victim : players)
						{
							EnumGameType gm;
							if (((EntityPlayerMP) victim).theItemInWorldManager.getGameType().isCreative())
							{
								gm = EnumGameType.SURVIVAL;
							}
							else
							{
								gm = EnumGameType.CREATIVE;
							}

							victim.setGameType(gm);
							OutputHandler.chatConfirmation(sender, Localization.format("command.gamemode.changed", victim.username, gm.getName()));
						}
					}
					else
					{
						OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
						return;
					}
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
				}
			}
			else
			{
				EnumGameType gm;
				if (((EntityPlayerMP) sender).theItemInWorldManager.getGameType().isCreative())
				{
					gm = EnumGameType.SURVIVAL;
				}
				else
				{
					gm = EnumGameType.CREATIVE;
				}

				sender.setGameType(gm);
			}
		}
		else if (args.length == 2)
		{
			if (!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
			{
				OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOPERMISSION));
				return;
			}
			EnumGameType gm;
			String name = "";
			if (args[0].equals("0") || args[0].equals("1") || args[0].equals("2") || args[0].equalsIgnoreCase(EnumGameType.CREATIVE.getName()) || args[0].equalsIgnoreCase(EnumGameType.SURVIVAL.getName())
					|| args[0].equalsIgnoreCase(EnumGameType.ADVENTURE.getName()) || args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("s"))
			{
				gm = getGameTypeFromString(sender, args[0]);

				List<EntityPlayerMP> players = Arrays.asList(FunctionHelper.getPlayerFromPartialName(args[1]));
				if (PlayerSelector.hasArguments(args[1]))
				{
					players = Arrays.asList(PlayerSelector.matchPlayers(sender, args[1]));
					name = args[1];
				}
				if (players.size() != 0)
				{
					for (EntityPlayer victim : players)
					{
						victim.setGameType(gm);
					}
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[1]));
					return;
				}
			}
			else
			{
				gm = getGameTypeFromString(sender, args[1]);

				List<EntityPlayerMP> players = Arrays.asList(FunctionHelper.getPlayerFromPartialName(args[0]));
				if (PlayerSelector.hasArguments(args[0]))
				{
					players = Arrays.asList(PlayerSelector.matchPlayers(sender, args[0]));
					name = args[0];
				}
				if (players.size() != 0)
				{
					for (EntityPlayer victim : players)
					{
						victim.setGameType(gm);
					}
				}
				else
				{
					OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
					return;
				}
			}
			OutputHandler.chatConfirmation(sender, Localization.format("command.gamemode.changed", name, gm.getName()));
		}
		else
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length <= 2)
		{
			List<EntityPlayerMP> players = Arrays.asList(FunctionHelper.getPlayerFromPartialName(args[0]));
			if (PlayerSelector.hasArguments(args[0]))
			{
				players = Arrays.asList(PlayerSelector.matchPlayers(sender, args[0]));
			}
			if (players.size() != 0)
			{
				for (EntityPlayer victim : players)
				{
					if (args.length == 2)
					{
						victim.setGameType(getGameTypeFromString(sender, args[1]));
					}
					else
					{
						EnumGameType gm;
						if (((EntityPlayerMP) victim).theItemInWorldManager.getGameType() == EnumGameType.SURVIVAL || ((EntityPlayerMP) victim).theItemInWorldManager.getGameType() == EnumGameType.ADVENTURE)
						{
							gm = EnumGameType.CREATIVE;
						}
						else
						{
							gm = EnumGameType.SURVIVAL;
						}

						victim.setGameType(gm);
						OutputHandler.chatConfirmation(sender, Localization.format("command.gamemode.changed", victim.username, gm.getName()));
					}
				}
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
				return;
			}
		}
		else
		{
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
		}
	}

	public EnumGameType getGameTypeFromString(ICommandSender sender, String string)
	{
		return !string.equalsIgnoreCase(EnumGameType.SURVIVAL.getName()) && !string.equalsIgnoreCase("s") ? !string.equalsIgnoreCase(EnumGameType.CREATIVE.getName()) && !string.equalsIgnoreCase("c") ? !string
				.equalsIgnoreCase(EnumGameType.ADVENTURE.getName()) && !string.equalsIgnoreCase("a") ? WorldSettings.getGameTypeById(parseIntBounded(sender, string, 0, 2)) : EnumGameType.ADVENTURE : EnumGameType.CREATIVE : EnumGameType.SURVIVAL;
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
