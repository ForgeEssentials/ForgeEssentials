package com.ForgeEssentials.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandDoAs extends ForgeEssentialsCommandBase{

	@Override
	public String getCommandName() {
		return "doas";
	}

	@Override
	public String[] getDefaultAliases()
	{
		return new String[] {"sudo"};
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args) {
                if (args[1].equalsIgnoreCase("server") {
                      target.sendChatToPlayer("Use //serverdo");
                      return;
                }
		StringBuilder cmd = new StringBuilder(args.toString().length());
		for (int i = 1; i < args.length; i++)
		{
			cmd.append(args[i]);
			cmd.append(" ");
		}
		EntityPlayer target = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
		target.sendChatToPlayer("Player " + sender + "is attempting to issue a command as you.");// hook into questioner
		FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(target, cmd.toString());
		sender.sendChatToPlayer("Successfully issued command as " + args[0]); //unless you get the syntax wrong
		
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) {
		StringBuilder cmd = new StringBuilder(args.toString().length());
		for (int i = 1; i < args.length; i++)
		{
			cmd.append(args[i]);
			cmd.append(" ");
		}
		EntityPlayer target = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getPlayerForUsername(args[0]);
		target.sendChatToPlayer("The console is attempting to issue a command as you.");// hook into questioner
		FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(target, cmd.toString());// Problem is, things like motd go to the player.
		OutputHandler.SOP("Successfully issued command as " + args[0]);
	}

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public String getCommandPerm() {
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

}
