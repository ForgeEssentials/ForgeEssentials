package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandClearInventory extends FEcmdModuleCommands
{
	@Override
	public String getCommandName()
	{
		return "clear";
	}

	@Override
	public String[] getDefaultAliases()
	{
		return new String[]
		{ "ci" };
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 0)
		{
			sender.inventory.clearInventory(-1, -1);
			sender.inventoryContainer.detectAndSendChanges();
			OutputHandler.chatConfirmation(sender, Localization.get("command.clear.doneSelf"));
		}
		else if (args.length >= 1 && PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
		{
			List<EntityPlayerMP> players = Arrays.asList(PlayerSelector.matchPlayers(sender, args[0]));
			if (PlayerSelector.hasArguments(args[0]))
			{
				players = Arrays.asList(PlayerSelector.matchPlayers(sender, args[0]));
			}
			if (players.size() != 0)
			{
				for (EntityPlayer victim : players)
				{
					int clearPar1 = -1, clearPar2 = -1;
					if (args.length >= 2)
					{
						clearPar1 = parseInt(sender, args[1]);
						clearPar2 = parseInt(sender, args[2]);
					}
					victim.inventory.clearInventory(clearPar1, clearPar2);
					victim.inventoryContainer.detectAndSendChanges();
					OutputHandler.chatWarning(sender, Localization.format("command.clear.doneBy", sender.getCommandSenderName()));
				}
				OutputHandler.chatConfirmation(sender, Localization.format("command.clear.doneOf", args[0]));
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			}
		}
		else
		{
			sender.sendChatToPlayer(Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender));
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			List<EntityPlayerMP> players = Arrays.asList(PlayerSelector.matchPlayers(sender, args[0]));
			if (PlayerSelector.hasArguments(args[0]))
			{
				players = Arrays.asList(PlayerSelector.matchPlayers(sender, args[0]));
			}
			if (players.size() != 0)
			{
				for (EntityPlayer victim : players)
				{
					int clearPar1 = -1, clearPar2 = -1;
					if (args.length >= 2)
					{
						clearPar1 = parseInt(sender, args[1]);
						clearPar2 = parseInt(sender, args[2]);
					}
					victim.inventory.clearInventory(clearPar1, clearPar2);

					victim.inventoryContainer.detectAndSendChanges();
					OutputHandler.chatWarning(sender, Localization.format("command.clear.doneBy", sender.getCommandSenderName()));
				}
				OutputHandler.chatConfirmation(sender, Localization.format("command.clear.doneOf", args[0]));
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			}
		}
		else
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
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
		return "ForgeEssentials.BasicCommands.clear.self";
	}

	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 0)
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		else
			return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.OWNERS;
	}

	@Override
	public void registerExtraPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel(getCommandPerm() + ".others", RegGroup.OWNERS);
	}
}
