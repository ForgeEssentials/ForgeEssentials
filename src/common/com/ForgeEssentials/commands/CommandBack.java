package com.ForgeEssentials.commands;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

public class CommandBack extends ForgeEssentialsCommandBase
{

	@ForgeSubscribe
	public void onPlayerDeath(LivingDeathEvent e)
	{
		if (e.entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) e.entity;
			PlayerInfo.getPlayerInfo(player).lastDeath = new Point((int) player.posX, (int) player.posY, (int) player.posZ);
		}
	}

	@Override
	public String getCommandName()
	{
		return "back";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		PlayerInfo info = PlayerInfo.getPlayerInfo(sender);
		if (info.lastDeath != null)
		{
			Point death = info.lastDeath;
			((EntityPlayerMP) sender).playerNetServerHandler.setPlayerLocation(death.x, death.y, death.z, sender.rotationYaw, sender.rotationPitch);
		} else
			OutputHandler.chatError(sender, "You have not died yet.");
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
	}

	@Override
	public String getSyntaxConsole()
	{
		return null;
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player)
	{
		return "/back";
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		return true;
	}

	@Override
	public String getInfoConsole()
	{
		return null;
	}

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return "Teleports you your last death point";
	}

}
