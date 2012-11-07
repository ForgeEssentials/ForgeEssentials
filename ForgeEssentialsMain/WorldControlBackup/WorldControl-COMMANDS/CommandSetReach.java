package com.ForgeEssentials.commands;

import java.nio.ByteBuffer;

import com.ForgeEssentials.WorldControl.WorldControlMain;
import com.ForgeEssentials.network.PacketWCSetReach;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.Packet250CustomPayload;

public class CommandSetReach extends CommandBase
{

	@Override
	public String getCommandName()
	{
		return "setreach";
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2)
	{
		try
		{
			if (var2.length != 1)
			{
				this.getCommandSenderAsPlayer(var1).addChatMessage("SetReach Command Failed!(Try /setreach <distance>)");
				return;
			}
			EntityPlayer ep = this.getCommandSenderAsPlayer(var1);
			if (ep instanceof EntityPlayerMP)
			{
				float reach = Float.parseFloat(var2[0]);
				EntityPlayerMP epmp = (EntityPlayerMP) ep;
				epmp.theItemInWorldManager.setBlockReachDistance(reach);
				PacketDispatcher.sendPacketToPlayer(new PacketWCSetReach(reach), (Player) epmp);
				epmp.addChatMessage("Set Reach Distance to: " + reach);
			}
		} catch (Exception e)
		{
			this.getCommandSenderAsPlayer(var1).addChatMessage("SetReach Command Failed!(Unknown Reason)");
		}
	}

}
