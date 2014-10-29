package com.forgeessentials.commands;

import com.forgeessentials.commands.network.S5PacketNoclip;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.OutputHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

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
		return "/noclip [distance]";
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
	public void processCommandPlayer(EntityPlayer player, String[] args)
	{
        if(!player.capabilities.isFlying && !player.noClip)
		{
			OutputHandler.chatError(player, "Must be flying.");
			return;
		}
		
		if(args.length == 0)
		{
			player.noClip ^= true;
		}
		else
		{
			player.noClip = Boolean.parseBoolean(args[0]);
		}
		if(!player.noClip)
			findSafeY(player);
		S5PacketNoclip.setPlayerNoclipStatus(player, player.noClip);
		OutputHandler.chatConfirmation(player, "NoClip " + (player.noClip ? "enabled." : "disabled."));
	}
	
	public static void checkClip(EntityPlayer player)
	{
		if(player.noClip)
		{
			if(!player.capabilities.isFlying)
			{
				player.noClip = false;
				findSafeY(player);
                S5PacketNoclip.setPlayerNoclipStatus(player, player.noClip);
				OutputHandler.chatNotification(player, "NoClip auto-disabled: not flying");
			}
		}
	}

	private static void findSafeY(EntityPlayer player)
	{
		int x = (int)player.posX;
		int y = (int)player.posY;
		int z = (int)player.posZ;
		World w = player.worldObj;
		if(w.getBlock(x, y, z) == Blocks.air)
		{
			if(w.getBlock(x, y + 1, z) == Blocks.air || w.getBlock(x, y - 1, z) == Blocks.air)
				return;
		}
		else if(w.getBlock(x, y - 1, z) == Blocks.air && w.getBlock(x, y-2, z) == Blocks.air)
		{
			return;
		}
		else
		{
			while(y < 256)
			{
				if(w.getBlock(x, y, z) == Blocks.air && w.getBlock(x, y + 1, z) == Blocks.air)
				{
					player.setPositionAndUpdate(player.posX, y, player.posZ);
					return;
				}
				y++;
			}
		}
	}

}
