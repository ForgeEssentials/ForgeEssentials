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
		isComplete = false;
		this.world = world;
		X = minX = ModuleWorldBorder.minX - ModuleWorldBorder.overGenerate;
		Z = minZ = ModuleWorldBorder.minZ - ModuleWorldBorder.overGenerate;
		maxX = ModuleWorldBorder.maxX + ModuleWorldBorder.overGenerate;
		maxZ = ModuleWorldBorder.maxZ + ModuleWorldBorder.overGenerate;
		centerX = ModuleWorldBorder.X;
		centerZ = ModuleWorldBorder.Z;
		rad = ModuleWorldBorder.rad;

		eta = ((MathHelper.abs_int((maxX - minX) / 16) * MathHelper
				.abs_int((minZ - maxZ) / 16)));

		warnEveryone(Localization.get(Localization.WB_FILL_START));
		warnEveryone(Localization.get(Localization.WB_FILL_ETA).replaceAll(
				"%eta", getETA()));
	}

	@Override
	public void tick()
	{
		super.tick();

		int i = 0;
		while (i < chunksAtick)
		{
			if ((rad + ModuleWorldBorder.overGenerate) < ModuleWorldBorder
					.getDistanceRound(centerX, centerZ, X, Z))
			{
				i++;
				world.theChunkProviderServer.provideChunk((X >> 4), (Z >> 4));
				if (X <= maxX)
				{
					X += 16;
				} else
				{
					// New row!
					if (Z <= maxZ)
					{
						Z += 16;
						X = minX;
					} else
					{
						// Done!
						isComplete = true;
					}
				}
			}
		}
	}
}
