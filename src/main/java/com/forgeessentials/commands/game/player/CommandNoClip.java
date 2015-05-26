package com.forgeessentials.commands.game.player;

import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.Packet5Noclip;
import com.forgeessentials.util.PlayerInfo;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

public class CommandNoClip extends FEcmdModuleCommands
{
	
	@Override
	public String getCommandName()
	{
		return "noclip";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_)
	{
		return "/noclip [true/false]";
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public RegisteredPermValue getDefaultPermission()
	{
		return RegisteredPermValue.OP;
	}
	
	@Override
	public void processCommandPlayer(EntityPlayerMP player, String[] args)
	{
		if (!PlayerInfo.get(player).getHasFEClient())
		{
			OutputHandler.chatError(player, "You need the FE client addon to use this command.");
			OutputHandler.chatError(player, "Please visit https://github.com/ForgeEssentials/ForgeEssentialsMain/wiki/FE-Client-mod for more information.");
			return;
		}

		if(!player.capabilities.isFlying && !player.noClip)
            throw new TranslatedCommandException("You must be flying.");
		
		if(args.length == 0)
		{
			if (!player.noClip)
			player.noClip = true;
			else player.noClip = false;
		}
		else
		{
			player.noClip = Boolean.parseBoolean(args[0]);
		}
		if(!player.noClip)
			FunctionHelper.findSafeY(player);
		NetworkUtils.netHandler.sendTo(new Packet5Noclip(player.noClip), player);
	}
	
	public static void checkClip(EntityPlayer player)
	{
		if(player.noClip)
		{
			if(!player.capabilities.isFlying)
			{
				player.noClip = false;
				FunctionHelper.findSafeY(player);
				if(!player.worldObj.isRemote)
				{
					NetworkUtils.netHandler.sendTo(new Packet5Noclip(player.noClip), (EntityPlayerMP) player);
					OutputHandler.chatNotification(player, "NoClip auto-disabled: the targeted player is not flying");
				}
			}
		}
	}

}
