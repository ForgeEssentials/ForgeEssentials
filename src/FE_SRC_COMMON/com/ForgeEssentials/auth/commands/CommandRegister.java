package com.ForgeEssentials.auth.commands;

import java.security.NoSuchAlgorithmException;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.auth.ModuleAuth;
import com.ForgeEssentials.auth.pwdData;
import com.ForgeEssentials.auth.pwdSaver;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.OutputHandler;

public class CommandRegister extends ForgeEssentialsCommandBase 
{

	@Override
	public String getCommandName() 
	{
		return "register";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args) 
	{
		if(args.length != 1)
		{
			sender.sendChatToPlayer("You must specify a password (no spaces). Don't use your MC password.");
		}
		else
		{
			try 
			{
				String pass = args[0];
				pwdData data = new pwdData();
				data.salt = ModuleAuth.pwdEnc.generateSalt();
				data.encPwd = ModuleAuth.pwdEnc.getEncryptedPassword(pass, data.salt);
				pwdSaver.setData(sender.username, data);
			}
			catch (Exception e) 
			{
				OutputHandler.chatError(sender, e.toString());
				e.printStackTrace();
			}
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) {}

	@Override
	public boolean canConsoleUseCommand() 
	{
		return false;
	}

	@Override
	public String getCommandPerm() 
	{
		return null;
		//return "ForgeEssentials.Auth." + getCommandName();
	}
	
	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		return true;
	}
}
