package com.forgeessentials.commands;

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
import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandHeal extends FEcmdModuleCommands
{
	@Override
	public String getCommandName()
	{
		return "heal";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 0)
		{
			heal(sender);
		}
		else if (args.length == 1 && APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
		{
			EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
			if (player != null)
			{
				heal(player);
			}
			else
			{
				ChatUtils.sendMessage(sender, String.format("Player %s does not exist, or is not online.", args[0]));
			}
		}
		else
		{
			OutputHandler.chatError(sender, "Improper syntax. Please try this instead: <player>");
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
			if (player != null)
			{
				heal(player);
			}
			else
			{
				ChatUtils.sendMessage(sender, String.format("Player %s does not exist, or is not online.", args[0]));
			}
		}
		else
		{
			ChatUtils.sendMessage(sender, "Improper syntax. Please try this instead: <player>");
		}
	}

	public void heal(EntityPlayer target)
	{
		target.heal(20);
		target.extinguish();
		target.getFoodStats().addStats(20, 1.0F);
		ChatUtils.sendMessage(target, "You were healed");
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
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
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

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "/heal <player> Heal yourself. You can also heal other players if you have permission.";
	}

}
