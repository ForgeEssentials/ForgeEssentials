package com.ForgeEssentials.WorldControl;

import com.ForgeEssentials.commands.CommandInfo;
import com.ForgeEssentials.commands.CommandProcesser;

/**
 * @author UnknownCoder : Max Bruce
 * Provides an interface for getting region selections, parsing commands, block placement, and storing backups(?)
 */

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MathHelper;

public class APIHelper
{
	public static APIHelper instance = new APIHelper();
	
	public CommandInfo parseCommand(String idmeta)
	{
		return CommandProcesser.processIDMetaCombo(idmeta);
	}
	
	public int getPlayerPos(EntityPlayer ep, CoordinateType type)
	{
		if(type==CoordinateType.X) {
			return MathHelper.floor_double(ep.posX);
		}else if(type==CoordinateType.Y) {
			return MathHelper.floor_double(ep.posY);
		}if(type==CoordinateType.Z) {
			return MathHelper.floor_double(ep.posZ);
		}
		return 0;
	}
	
	public void addBackup(CopyArea area, int id, String username)
	{
		FunctionHandler.cpy.put(new FunctionHandler.PlayerInfo(id, username), area);
	}
	
	public void placeBlock(int x, int y, int z, CommandInfo inf, EntityPlayer sender, BackupArea back)
	{
		FunctionHandler.instance.placeBlock(x, y, z, inf, sender, back);
	}

}
