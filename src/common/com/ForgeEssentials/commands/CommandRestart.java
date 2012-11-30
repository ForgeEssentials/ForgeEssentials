package com.ForgeEssentials.commands;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.OutputHandler;

public class CommandRestart extends ForgeEssentialsCommandBase
{

	@Override
	public String getCommandName()
	{
		return "restart";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		OutputHandler.SOP("Not yet implemented, check back soon!");
		/**
		try {
            
            File file = new File(startupScript);
            if (file.isFile()) {
                System.out.println("Attempting to restart with " + startupScript);

                // Kick all players
                
                //Close the socket

                // Give the socket a chance to send the packets
                try {
                    MinecraftServer.getServer().stopServer();
                } catch (Throwable t) {
                }

                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    Runtime.getRuntime().exec("cmd /c start " + file.getPath());
                } else {
                    Runtime.getRuntime().exec(file.getPath());
                }
                System.exit(0);
            } else {
                System.out.println("Startup script '" + startupScript + "' does not exist! Ensure a shell script named restart.bat or restart.sh is placed in the ForgeEssentials folder!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        */
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}

}
