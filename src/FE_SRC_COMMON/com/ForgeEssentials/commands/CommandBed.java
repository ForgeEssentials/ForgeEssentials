package com.ForgeEssentials.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.permissions.IPermRegisterEvent;
import com.ForgeEssentials.api.permissions.RegGroup;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.commands.util.FEcmdModuleCommands;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandBed extends FEcmdModuleCommands
{
	private Point sleepPoint;
	
	@Override
	public String getCommandName()
	{
		return "bed";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if (args.length >= 1 && APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".others")))
		{
			EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
			if (player != null)
			{
				tp(player);
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			}
		}
		else
		{
			tp((EntityPlayerMP) sender);
		}
	}

	private void tp(EntityPlayerMP player)
	{
		ChunkCoordinates spawn = player.getBedLocation();
		if (spawn != null)
		{
			spawn = EntityPlayer.verifyRespawnCoordinates(player.worldObj, spawn, true);
			if (spawn != null)
			{
				World world = player.worldObj;
				if (!world.provider.canRespawnHere())
				{
					world = DimensionManager.getWorld(0);
				}
				PlayerInfo.getPlayerInfo(player.username).back = new WarpPoint(player);
				// Doesnt work
				// FunctionHelper.setPlayer(player, new Point(spawn), world);
				//player.playerNetServerHandler.setPlayerLocation(spawn.posX, spawn.posY, spawn.posZ, player.rotationYaw, player.rotationPitch);
				if (sleepPoint != null){
				FunctionHelper.setPlayer(player, sleepPoint, world);
				}else{
					OutputHandler.chatError(player, Localization.get("command.bed.noExist"));
				}
				OutputHandler.chatConfirmation(player, Localization.get("command.bed.done"));
			} else {
				OutputHandler.chatError(player, Localization.get("command.bed.obstructed"));
			}
		} else {
			OutputHandler.chatError(player, Localization.get("command.bed.noExist"));			
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
			if (player != null)
			{
				tp(player);
			}
			else
			{
				OutputHandler.chatError(sender, Localization.format(Localization.ERROR_NOPLAYER, args[0]));
			}
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

	@Override
	public void registerExtraPermissions(IPermRegisterEvent event)
	{
		event.registerPermissionLevel(getCommandPerm() + ".others", RegGroup.OWNERS);
	}
	
	@ForgeSubscribe
	private void getCoords(PlayerSleepInBedEvent e){
		this.sleepPoint.x = e.x;
		this.sleepPoint.y = e.y;
		this.sleepPoint.z = e.z;
	}
}
