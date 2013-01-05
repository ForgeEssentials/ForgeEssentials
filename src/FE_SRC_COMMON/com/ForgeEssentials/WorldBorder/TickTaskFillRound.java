package com.ForgeEssentials.WorldBorder;

import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;

import com.ForgeEssentials.util.Localization;

/**
 * Does the actual filling, with limited chuncks per tick.
 * 
 * @author Dries007
 *
 */

public class TickTaskFillRound extends TickTaskFill
{
	public TickTaskFillRound(WorldServer world)
	{
		this.isComplete = false;
		this.world = world;
		this.X = this.minX = ModuleWorldBorder.minX - ModuleWorldBorder.overGenerate;
		this.Z = this.minZ = ModuleWorldBorder.minZ - ModuleWorldBorder.overGenerate;
		this.maxX = ModuleWorldBorder.maxX + ModuleWorldBorder.overGenerate;
		this.maxZ = ModuleWorldBorder.maxZ + ModuleWorldBorder.overGenerate;
		this.centerX = ModuleWorldBorder.X;
		this.centerZ = ModuleWorldBorder.Z;
		this.rad = ModuleWorldBorder.rad;
		
		this.eta = (int) ((MathHelper.abs_int((this.maxX - this.minX)/16) * MathHelper.abs_int((this.minZ - this.maxZ)/16)));
		
		warnEveryone(Localization.get(Localization.WB_FILL_START));
		warnEveryone(Localization.get(Localization.WB_FILL_ETA).replaceAll("%eta", getETA()));
	}

	@Override
	public void tick()
	{
		super.tick();
		
		int i = 0;
		while (i < chunksAtick)
		{
			if((rad + ModuleWorldBorder.overGenerate) < ModuleWorldBorder.getDistanceRound(centerX, centerZ, X, Z))
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
