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
	public TickTaskFillSquare(WorldServer world)
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

		eta = (((MathHelper.abs_int((maxX - minX) / 16) * MathHelper.abs_int((minZ - maxZ) / 16))));

		warnEveryone(Localization.get(Localization.WB_FILL_START));
		warnEveryone(FEChatFormatCodes.AQUA + "minX:" + minX + "  maxX:" + maxX);
		warnEveryone(FEChatFormatCodes.AQUA + "minZ:" + minZ + "  maxZ:" + maxZ);

		warnEveryone(Localization.get(Localization.WB_FILL_ETA).replaceAll("%eta", getETA()));
	}

	@Override
	public void tick()
	{
		super.tick();

		int i = 0;
		while (i < chunksAtick)
		{
			if(!world.theChunkProviderServer.chunkExists((X >> 4), (Z >> 4)))
			{
				i++;
				world.theChunkProviderServer.provideChunk((X >> 4), (Z >> 4));
			}
			world.theChunkProviderServer.unloadChunksIfNotNearSpawn((X >> 4), (Z >> 4));
			world.theChunkProviderServer.unload100OldestChunks();
			
			if (X <= maxX)
			{
				X += 16;
			}
			else
			{
				// New row!
				if (Z <= maxZ)
				{
					Z += 16;
					X = minX;
				}
				else
				{
					// Done!
					isComplete = true;
				}
			}
		}
	}
}
