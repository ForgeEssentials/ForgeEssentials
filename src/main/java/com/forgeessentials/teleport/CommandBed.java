package com.forgeessentials.teleport;

import java.util.List;

import com.forgeessentials.util.TeleportCenter;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.core.PlayerInfo;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.AreaSelector.Point;
import com.forgeessentials.util.AreaSelector.WarpPoint;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandBed extends ForgeEssentialsCommandBase
{
	private WarpPoint sleepPoint;

    public CommandBed()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }
	
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
				OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
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
				    TeleportCenter.addToTpQue(sleepPoint, player);
                    OutputHandler.chatConfirmation(player, "Teleported to bed last used.");
				}else{
					OutputHandler.chatError(player, "No bed found.");
				}

				OutputHandler.chatConfirmation(player, "Teleported to bed last used.");
			} else {
				OutputHandler.chatError(player, "Your bed is obstructed.");
			}
		} else {
			OutputHandler.chatError(player, "No bed found.");			
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
				OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
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
		return "fe.teleport." + getCommandName();
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
		return RegGroup.MEMBERS;
	}
	
	@ForgeSubscribe
	public void getCoords(PlayerSleepInBedEvent e){
		if (sleepPoint == null)
        {
            this.sleepPoint = new WarpPoint(e.entityPlayer.dimension, e.x, e.y, e.z, 0, 0);
        }
        else
        this.sleepPoint.x = e.x;
		this.sleepPoint.y = e.y;
		this.sleepPoint.z = e.z;
	}

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "/bed [player] Teleport you or another player to the bed last used.";
	}
}
