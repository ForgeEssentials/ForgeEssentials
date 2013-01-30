package com.ForgeEssentials.WorldBorder;

import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

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
		super(world);
	}

	@Override
	public void tick()
	{
		super.tick();
	}

	@Override
	public void genList() 
	{
		for(int X = this.minX; X <= this.maxX; X = X + 16)
		{
			for(int Z = this.minZ; Z <= this.maxZ; Z = Z + 16)
			{
				toDo.add(new ChunkCoordIntPair(X, Z));
			}
		}
		OutputHandler.SOP(toDo.size() + " chunks to generate.");
	}
}
