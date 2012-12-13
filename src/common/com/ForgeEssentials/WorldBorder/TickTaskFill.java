package com.ForgeEssentials.WorldBorder;

import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.IProgressUpdate;
import net.minecraft.src.MathHelper;
import net.minecraft.src.MinecraftException;
import net.minecraft.src.WorldServer;

import com.ForgeEssentials.WorldControl.tickTasks.ITickTask;
import com.ForgeEssentials.util.FEChatFormatCodes;
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
	protected boolean canNotSaveBefore;
	
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
	
	protected int eta; //in ticks
	protected Long ticks = 0L;
	protected int chunksAtick = 1;
	
	public TickTaskFill(boolean canNotSaveBefore, WorldServer world)
	{
		
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
            ((EntityPlayerMP)FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.get(var2)).sendChatToPlayer(FEChatFormatCodes.AQUA + msg);
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
