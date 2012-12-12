package com.ForgeEssentials.WorldBorder;

import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.IProgressUpdate;
import net.minecraft.src.MathHelper;
import net.minecraft.src.MinecraftException;
import net.minecraft.src.WorldServer;

import com.ForgeEssentials.WorldControl.tickTasks.ITickTask;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Does the actual filling, with limited chuncks per tick.
 * 
 * @author Dries007
 *
 */

public class TickTaskFillRound extends TickTaskFill
{
	public TickTaskFillRound(boolean canNotSaveBefore, WorldServer world)
	{
		super(canNotSaveBefore, world);
		this.isComplete = false;
		this.canNotSaveBefore = canNotSaveBefore;
		this.world = world;
		this.X = this.minX = ModuleWorldBorder.borderData.getInteger("minX") - 320;
		this.Z = this.minZ = ModuleWorldBorder.borderData.getInteger("minZ") - 320;
		this.maxX = ModuleWorldBorder.borderData.getInteger("maxX") + 320;
		this.maxZ = ModuleWorldBorder.borderData.getInteger("maxZ") + 320;
		this.centerX = ModuleWorldBorder.borderData.getInteger("centerX");
		this.centerZ = ModuleWorldBorder.borderData.getInteger("centerZ");
		this.rad = ModuleWorldBorder.borderData.getInteger("rad");
		
		this.eta = (int) ((MathHelper.abs_int((this.maxX - this.minX)/16) * MathHelper.abs_int((this.minZ - this.maxZ)/16)));
		
		warnEveryone(Localization.get(Localization.WB_FILL_START));
		warnEveryone(Localization.get(Localization.WB_FILL_ETA).replaceAll("%eta", getETA()));
	}

	@Override
	public void tick()
	{
		ticks ++;
		if(ticks % (20 * 10) == 0)
		{
			warnEveryone(Localization.get(Localization.WB_FILL_STILLGOING).replaceAll("%eta", getETA()));
		}
		
		int i = 0;
		while (i < chunksAtick)
		{
			if(!ModuleWorldBorder.outDistance(centerX, centerZ, rad + 320, X, Z))
			{
				i++;
				world.theChunkProviderServer.provideChunk((X >> 4), (Z >> 4));	
				if(X <= maxX)
				{
					X += 16;
				}
				else
				{
					//New row!
					if(Z <= maxZ)
					{
						Z += 16;
						X = minX;
					}	
					else
					{
						//Done!
						isComplete = true;
					}
				}
			}
		}
	}
}
