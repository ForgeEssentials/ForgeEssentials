package com.ForgeEssentials.commands;

import java.text.DecimalFormat;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandServerStatus extends CommandBase {

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "status";
	}
	public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return par1ICommandSender.translateString("/status [player]", new Object[0]);
    }
	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		if (var2.length <=1){
			EntityPlayerMP var3 = null;
			if (var2.length ==1){
			var3 = func_82359_c(var1, var2[0]);
			}else if (var1 instanceof EntityPlayerMP){
			 var3 = getCommandSenderAsPlayer(var1);
			}else
			{
				throw new WrongUsageException("/status [player]");
			}
			DecimalFormat df = new DecimalFormat("#.##");
			notifyAdmins(var1, 0, var3.username + " is at X=" + df.format(var3.posX) + " Y=" + df.format(var3.posY) + " Z=" + df.format(var3.posZ) + " with gamemode " + var3.theItemInWorldManager.getGameType().getName() , new Object[] {0});
		}else{
			throw new WrongUsageException("/status [player]");
		}

	}
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length != 1 ? null : getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames());
    }
	public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2)
    {
        return par2 == 0;
    }
}
