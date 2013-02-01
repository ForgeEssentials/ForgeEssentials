package com.ForgeEssentials.commands;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.WorldSettings;

import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandGameMode extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "gamemode";
	}

	@Override
	public String[] getDefaultAliases()
	{
		return new String[] {"gm"};
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length < 2)
		{
			if (args.length == 1)
			{
				if(FunctionHelper.getPlayerFromPartialName(args[0]) != null || PlayerSelector.matchOnePlayer(sender, args[0]) != null)
				{
					if(!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
					{
						OutputHandler.chatError(sender, "You do not have permission to do that.");
						return;
					}
					EntityPlayer victim = FunctionHelper.getPlayerFromPartialName(args[0]);
					if(PlayerSelector.hasArguments(args[0]))
					{
						victim = PlayerSelector.matchOnePlayer(sender, args[0]);
					}
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
					OutputHandler.chatConfirmation(sender, "Gamemode changed for " + victim.username);
				}
				else
				{
					sender.setGameType(getGameTypeFromString(sender, args[0]));
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
			if(!PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
			{
				OutputHandler.chatError(sender, "You do not have permission to do that.");
				return;
			}
			EntityPlayer victim = FunctionHelper.getPlayerFromPartialName(args[0]);
			if(PlayerSelector.hasArguments(args[0]))
			{
				victim = PlayerSelector.matchOnePlayer(sender, args[0]);
			}
			EnumGameType gm;
			if (((EntityPlayerMP) victim).theItemInWorldManager.getGameType().isSurvivalOrAdventure())
			{
				gm = EnumGameType.CREATIVE;
			}
			else
			{
				gm = EnumGameType.SURVIVAL;
			}

			victim.setGameType(gm);
			OutputHandler.chatConfirmation(sender, "Gamemode changed for " + victim.username);
		}
		else
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + " /gamemode [type]");
			OutputHandler.chatError(sender, " /gamemode [player] [type]");
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length <= 2)
		{
			EntityPlayer victim = FunctionHelper.getPlayerFromPartialName(args[0]);
			if(PlayerSelector.hasArguments(args[0]))
			{
				victim = PlayerSelector.matchOnePlayer(sender, args[0]);
			}
			if (args.length == 2)
			{
				victim.setGameType(getGameTypeFromString(sender, args[1]));
			}
			else
			{
				EnumGameType gm;
				if (((EntityPlayerMP) victim).theItemInWorldManager.getGameType() == EnumGameType.SURVIVAL ||
						((EntityPlayerMP) victim).theItemInWorldManager.getGameType() == EnumGameType.ADVENTURE)
				{
					gm = EnumGameType.CREATIVE;
				}
				else
				{
					gm = EnumGameType.SURVIVAL;
				}

				victim.setGameType(gm);
			}
			sender.sendChatToPlayer("Gamemode changed for " + victim.username);
		}
		else
		{
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
		}
	}

	public EnumGameType getGameTypeFromString(ICommandSender sender, String string)
	{
		return !string.equalsIgnoreCase(EnumGameType.SURVIVAL.getName()) && !string.equalsIgnoreCase("s") ? (!string.equalsIgnoreCase(EnumGameType.CREATIVE
				.getName()) && !string.equalsIgnoreCase("c") ? (!string.equalsIgnoreCase(EnumGameType.ADVENTURE.getName()) && !string.equalsIgnoreCase("a") ? WorldSettings
				.getGameTypeById(parseIntBounded(sender, string, 0, 2)) : EnumGameType.ADVENTURE)
				: EnumGameType.CREATIVE)
				: EnumGameType.SURVIVAL;
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
		if (args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, new String[] { "survival", "creative", "adventure" });
		}
		else if (args.length == 1)
		{
			List match = getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
			if (match == null)
			{
				match = getListOfStringsMatchingLastWord(args, new String[] { "survival", "creative", "adventure" });
			}
			return match;
		}
		else
		{
			return null;
		}
	}
}
