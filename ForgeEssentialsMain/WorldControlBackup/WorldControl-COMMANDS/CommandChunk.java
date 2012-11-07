package com.ForgeEssentials.commands;

import java.util.Arrays;
import java.util.List;

import com.ForgeEssentials.PlayerInfo;
import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.WorldControl.FunctionHandler;

import net.minecraft.src.CommandBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

public class CommandChunk extends CommandBase {

	@Override
	public String getCommandName() {
		return "chunk";
	}
	
	public List getCommandAliases()
    {
        return Arrays.asList(new String[] {"ch"});
    }
	
	public int toChunk(double num) {
		int nume = (int)num;
		int temp = (int)(nume/16);
		return temp*16;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2)
	{
		try
		{
			EntityPlayer ep = this.getCommandSenderAsPlayer(var1);
			PlayerInfo.getPlayerInfo(ep.username).setPoint1(new Point(toChunk(ep.posX), 0, toChunk(ep.posZ)));
			PlayerInfo.getPlayerInfo(ep.username).setPoint2(new Point(toChunk(ep.posX) + 16, 256, toChunk(ep.posZ) + 16));
			this.getCommandSenderAsPlayer(var1).addChatMessage("Selected Chunk");
		}
		catch (Exception e)
		{
			this.getCommandSenderAsPlayer(var1).addChatMessage("Chunk Command Failed!(Unknown Reason)");
		}
	}

}
