package com.ForgeEssentials.auth.commands;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.auth.LoginHandler;
import com.ForgeEssentials.auth.ModuleAuth;
import com.ForgeEssentials.auth.pwdData;
import com.ForgeEssentials.auth.pwdSaver;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.OutputHandler;

public class CommandLogin extends ForgeEssentialsCommandBase 
{

	@Override
	public String getCommandName() 
	{
		return "login";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args) 
	{
		if(args.length != 1)
		{
			sender.sendChatToPlayer("You have to give your password.");
		}
		else
		{
			try 
			{
				pwdData data = pwdSaver.getData(sender.username);
				if(ModuleAuth.pwdEnc.authenticate(args[0], data.encPwd, data.salt))
				{
					ModuleAuth.handler.login(sender);
				}
				else
				{
					OutputHandler.chatError(sender, "Wrong pass!");
				}
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
