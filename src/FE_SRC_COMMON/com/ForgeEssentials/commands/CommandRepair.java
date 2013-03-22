package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandRepair extends FEcmdModuleCommands
{
	@Override
	public String getCommandName()
	{
		return "repair";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 0)
		{
			ItemStack item = sender.getHeldItem();

			if (item == null)
			{
				OutputHandler.chatError(sender, Localization.get(Localization.ERROR_NOITEMPLAYER));
			}

			item.setItemDamage(0);

		}
		else if (args.length == 1 && PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
		{
			List<EntityPlayerMP> players = Arrays.asList(PlayerSelector.matchPlayers(sender, args[0]));
			if (PlayerSelector.hasArguments(args[0]))
			{
				players = Arrays.asList(PlayerSelector.matchPlayers(sender, args[0]));
			}
			if (players.size() != 0)
			{
				for (EntityPlayer target : players)
				{
					ItemStack item = target.getHeldItem();

					if (item != null)
					{
						item.setItemDamage(0);
					}
				}
			}
			else
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			}
		}
		else
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			//PlayerSelector.matchPlayers(sender, args[0])
			List<EntityPlayerMP> players = Arrays.asList(PlayerSelector.matchPlayers(sender, args[0]));
			if (PlayerSelector.hasArguments(args[0]))
			{
				players = Arrays.asList(PlayerSelector.matchPlayers(sender, args[0]));
			}
			if (players.size() != 0)
			{
				for (EntityPlayer target : players)
				{
					ItemStack item = target.getHeldItem();

					if (item != null)
					{
						item.setItemDamage(0);
					}
				}
			}
			else
			{
				sender.sendChatToPlayer(Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			}
		}
		else
		{
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
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
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

	@Override
	public void registerExtraPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel(getCommandPerm() + ".others", RegGroup.OWNERS);
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		else
			return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}

}
