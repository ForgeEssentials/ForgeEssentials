package com.ForgeEssentials.WorldBorder;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.ITickTask;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Does the actual filling, with limited chuncks per tick.
 * 
 * @author Dries007
 * 
 */

public class TickTaskFill implements ITickTask
{
	protected boolean isComplete;

	protected WorldServer world;
	protected int dim;

	protected int minX;
	protected int minZ;

	protected int maxX;
	protected int maxZ;

	protected int centerX;
	protected int centerZ;
	protected int rad;

	protected int X;
	protected int Z;

	protected int tps = 20;

	protected int eta; // in ticks
	protected Long ticks = 0L;
	protected int chunksAtick = 1;

	public boolean autoPilot = false;
	public int targetTPS = 20;

	protected MinecraftServer server = FMLCommonHandler.instance()
			.getMinecraftServerInstance();

	public double getTPS()
	{
		double tps = FunctionHelper.getTPS(dim);
		if ((int) tps == 0)
		{
			tps = 1;
		}
		this.tps = (int) tps;
		return tps;
	}

	public String getETA()
	{
		return (int) (((eta - ticks) / getTPS()))
				+ Localization.get(Localization.UNIT_SECONDS);
	}

	public void warnEveryone(String msg)
	{
		OutputHandler.SOP("#### " + msg);
		for (int var2 = 0; var2 < FMLCommonHandler.instance()
				.getMinecraftServerInstance().getConfigurationManager().playerEntityList
				.size(); ++var2)
		{
			((EntityPlayerMP) FMLCommonHandler.instance()
					.getMinecraftServerInstance().getConfigurationManager().playerEntityList
					.get(var2)).sendChatToPlayer(FEChatFormatCodes.AQUA + msg);
		}
	}

	public void engageTurbo(int speed)
	{
		warnEveryone(Localization.get(Localization.WB_TURBO_ON));
		chunksAtick = speed;
	}

	public void disengageTurbo()
	{
		warnEveryone(Localization.get(Localization.WB_TURBO_OFF));
		chunksAtick = 1;
	}

	public void engageAutopilot(int speed)
	{
		targetTPS = speed;
		autoPilot = true;
	}

	public void disengageAutopilot()
	{
		autoPilot = false;
		chunksAtick = 1;
	}

	@Override
	public void tick()
	{
		ticks++;
		if (ticks % (tps * 10) == 0)
		{
			warnEveryone(Localization.get(Localization.WB_FILL_STILLGOING)
					.replaceAll("%eta", getETA())
					.replaceAll("%ctp", chunksAtick + ""));
		}

		getTPS();

		if (autoPilot && ticks % (tps) == 0)
		{
			if (tps < targetTPS)
			{
				if (chunksAtick > 0)
				{
					--chunksAtick;
				}
				OutputHandler.debug("WB Autopilot: Less CPT:" + chunksAtick
						+ " TPS:" + tps + " < " + targetTPS);
			} else if (tps - 2 > targetTPS)
			{
				// We can handle more!
				++chunksAtick;
				OutputHandler.debug("WB Autopilot: More CPT:" + chunksAtick
						+ " TPS:" + tps + " > " + targetTPS);
			} else
			{
				OutputHandler.debug("WB Autopilot: Good CPT:" + chunksAtick
						+ " TPS:" + tps + " ~~ " + targetTPS);
			}
		}
	}

	@Override
	public void onComplete()
	{
		warnEveryone(Localization.get(Localization.WB_FILL_DONE));
		warnEveryone(Localization.get(Localization.WB_FILL_FINISHED)
				.replaceAll("%ticks", "" + ticks)
				.replaceAll("%sec", "" + (int) (ticks / tps)));
		CommandWB.taskGooing = null;
	}

	@Override
	public boolean isComplete()
	{
		return isComplete;
	}

	@Override
	public boolean editsBlocks()
	{
		return true;
	}

	public void stop()
	{
		warnEveryone(Localization.get(Localization.WB_FILL_ABORTED));
		isComplete = true;
	}

}
