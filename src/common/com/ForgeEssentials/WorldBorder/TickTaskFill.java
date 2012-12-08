package com.ForgeEssentials.WorldBorder;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.ForgeEssentials.WorldControl.ModuleWorldControl;
import com.ForgeEssentials.WorldControl.tickTasks.ITickTask;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.IProgressUpdate;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.MinecraftException;
import net.minecraft.src.WorldServer;

public class TickTaskFill implements ITickTask
{
	private boolean isComplete;
	private boolean canNotSaveBefore;
	
	private WorldServer world;
	private int dim;
	
	private int minX;
	private int minZ;
	
	private int maxX;
	private int maxZ;
	
	private int X;
	private int Z;
	
	private int eta; //in ticks
	private Long ticks = 0L;
	private int chunksAtick = 1;
	
	public TickTaskFill(boolean canNotSaveBefore, WorldServer world)
	{
		this.isComplete = false;
		this.canNotSaveBefore = canNotSaveBefore;
		this.world = world;
		this.X = this.minX = ModuleWorldBorder.borderData.getInteger("minX") - 320;
		this.Z = this.minZ = ModuleWorldBorder.borderData.getInteger("minZ") - 320;
		this.maxX = ModuleWorldBorder.borderData.getInteger("maxX") + 320;
		this.maxZ = ModuleWorldBorder.borderData.getInteger("maxZ") + 320;
		
		this.eta = (int) (((MathHelper.abs_int((this.maxX - this.minX)/16) * MathHelper.abs_int((this.minZ - this.maxZ)/16))));
		
		warnEveryone(Localization.get(Localization.WB_FILL_START));
		warnEveryone(OutputHandler.AQUA + "minX:" + this.minX + "  maxX:" + this.maxX);
		warnEveryone(OutputHandler.AQUA + "minZ:" + this.minZ + "  maxZ:" + this.maxZ);
		
		warnEveryone(Localization.get(Localization.WB_FILL_ETA).replaceAll("%eta", getETA()));
	}
	
	public double getTPS()
	{
		long var2 = 0L;
        long[] var4 = FMLCommonHandler.instance().getMinecraftServerInstance().tickTimeArray;
        int var5 = var4.length;

        for (int var6 = 0; var6 < var5; ++var6)
        {
            long var7 = var4[var6];
            var2 += var7;
        }
        
        double tps = 1000/(((double)var2 / (double)var5)* 1.0E-6D);
        
        if(tps > 20) tps = 20;
        
        OutputHandler.SOP("TPS: " + tps);
        
        return tps;
	}
	
	public String getETA()
	{
		return (int) (((eta - ticks)/getTPS())) + Localization.get(Localization.UNIT_SECONDS);
	}
	
	public void warnEveryone(String msg)
	{
		OutputHandler.SOP("#### " + msg);
		for (int var2 = 0; var2 < FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.size(); ++var2)
        {
            ((EntityPlayerMP)FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.get(var2)).sendChatToPlayer(OutputHandler.AQUA + msg);
        }
	}
	
	public void engageTurbo()
	{
		warnEveryone(Localization.get(Localization.WB_TURBO_ON));
		chunksAtick = 10;
	}
	
	public void disEngageTurbo()
	{
		warnEveryone(Localization.get(Localization.WB_TURBO_OFF));
		chunksAtick = 1;
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

	@Override
	public void onComplete()
	{
		warnEveryone(Localization.get(Localization.WB_FILL_DONE));
		this.world.canNotSave = false;
		try {world.saveAllChunks(true, (IProgressUpdate)null);}
		catch (MinecraftException e) {warnEveryone(Localization.get(Localization.WB_SAVING_FAILED));}
		world.canNotSave = canNotSaveBefore;
		warnEveryone(Localization.get(Localization.WB_FILL_FINISHED).replaceAll("%ticks", "" + ticks).replaceAll("%sec", "" + (int)(ticks / 20)));
		CommandWB.taskGooing = null;
	}

	@Override
	public boolean isComplete()
	{
		return this.isComplete;
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
