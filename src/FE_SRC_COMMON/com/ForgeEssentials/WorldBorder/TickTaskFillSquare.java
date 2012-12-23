package com.ForgeEssentials.WorldBorder;

import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;

import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.Localization;

/**
 * Does the actual filling, with limited chuncks per tick.
 * 
 * @author Dries007
 *
 */

public class TickTaskFillSquare extends TickTaskFill
{
	public TickTaskFillSquare(boolean canNotSaveBefore, WorldServer world)
	{
		this.isComplete = false;
		this.canNotSaveBefore = canNotSaveBefore;
		this.world = world;
		this.X = this.minX = ModuleWorldBorder.borderData.getInteger("minX") - ModuleWorldBorder.overGenerate;
		this.Z = this.minZ = ModuleWorldBorder.borderData.getInteger("minZ") - ModuleWorldBorder.overGenerate;
		this.maxX = ModuleWorldBorder.borderData.getInteger("maxX") + ModuleWorldBorder.overGenerate;
		this.maxZ = ModuleWorldBorder.borderData.getInteger("maxZ") + ModuleWorldBorder.overGenerate;
		this.centerX = ModuleWorldBorder.borderData.getInteger("centerX");
		this.centerZ = ModuleWorldBorder.borderData.getInteger("centerZ");
		this.rad = ModuleWorldBorder.borderData.getInteger("rad");
		
		this.eta = (int) (((MathHelper.abs_int((this.maxX - this.minX)/16) * MathHelper.abs_int((this.minZ - this.maxZ)/16))));
		
		warnEveryone(Localization.get(Localization.WB_FILL_START));
		warnEveryone(FEChatFormatCodes.AQUA + "minX:" + this.minX + "  maxX:" + this.maxX);
		warnEveryone(FEChatFormatCodes.AQUA + "minZ:" + this.minZ + "  maxZ:" + this.maxZ);
		
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
