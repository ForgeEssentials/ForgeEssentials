package com.ForgeEssentials.commands;

import java.util.List;

import com.ForgeEssentials.util.ChatUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandTop extends FEcmdModuleCommands
{

	@Override
	public String getCommandName()
	{
		return "top";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length == 0)
		{
			top(sender);
		}
		else if (args.length == 1 && APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
		{
			EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
			if (player != null)
			{
				top(player);
			}
			else
			{
				ChatUtils.sendMessage(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			}
		}
		else
		{
			OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender));
		}
	}

	@Override
	public void registerExtraPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel(getCommandPerm() + ".others", RegGroup.OWNERS);
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
			if (player != null)
			{
				top(player);
			}
			else
			{
				ChatUtils.sendMessage(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			}
		}
		else
		{
			ChatUtils.sendMessage(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxConsole());
		}
	}

	public void top(EntityPlayer player)
	{
		WarpPoint point = new WarpPoint(player);
		point.y = player.worldObj.getActualHeight();
		while (player.worldObj.getBlockId(point.x, point.y, point.z) == 0)
		{
			point.y--;
		}
		((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(point.x, point.y + 1, point.z, point.yaw, point.pitch);
		ChatUtils.sendMessage(player, Localization.get("message.tc.done"));
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
		else
			return null;
	}

	@Override
	public RegGroup getReggroup()
	{
		return RegGroup.MEMBERS;
	}

}
