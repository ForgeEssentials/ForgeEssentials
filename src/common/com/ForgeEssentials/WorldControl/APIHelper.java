package com.ForgeEssentials.WorldControl;

import com.ForgeEssentials.WorldControl.FunctionHandler.PlayerInfo;
import com.ForgeEssentials.commands.CommandInfo;
import com.ForgeEssentials.commands.CommandProcesser;

/**
 * @author UnknownCoder : Max Bruce
 * Provides an interface for getting region selections, parsing commands, block placement, and storing backups(?)
 */

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MathHelper;

public class APIHelper
{
	public static APIHelper instance = new APIHelper();
	
	public int getSel(String username, SelectionType sel)
	{
		if (sel==SelectionType.X1) {
			return FunctionHandler.instance.point1X.get(username);
		}else if (sel==SelectionType.X2) {
			return FunctionHandler.instance.point2X.get(username);
		}else if (sel==SelectionType.Y1) {
			return FunctionHandler.instance.point1Y.get(username);
		}else if (sel==SelectionType.Y2) {
			return FunctionHandler.instance.point2Y.get(username);
		}else if (sel==SelectionType.Z1) {
			return FunctionHandler.instance.point1Z.get(username);
		}else if (sel==SelectionType.Z2) {
			return FunctionHandler.instance.point2Z.get(username);
		}
		
		return 0;
	}
	
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
