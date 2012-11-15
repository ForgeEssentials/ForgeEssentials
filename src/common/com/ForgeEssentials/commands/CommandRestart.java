package com.ForgeEssentials.commands;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.ServerConfigurationManager;

import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

public class CommandRestart extends ForgeEssentialsCommandBase {

	
	private ServerConfigurationManager serverconfmgr;
	
	@Override
	public String getCommandName() {
		return "restart";
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args) {
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) {
		OutputHandler.SOP("Not yet implemented");
		/** Not implemented
		OutputHandler.SOP("Restarting server...");
		if (MinecraftServer.getServer().getNetworkThread() != null)
        {
			MinecraftServer.getServer().getNetworkThread().stopListening();
        }

        if (serverconfmgr != null)
        {
            OutputHandler.SOP("Saving players");
            serverconfmgr.saveAllPlayerData();
            serverconfmgr.removeAllPlayers();
        }

        OutputHandler.SOP("Saving worlds");
        SaveUtil.saveGame();
        WorldServer[] var1 = MinecraftServer.getServer().worldServers;
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3)
        {
            WorldServer var4 = var1[var3];
            MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(var4));
            var4.flush();
            DimensionManager.setWorld(var4.provider.dimensionId, null);
        }
		FMLRelauncher.handleServerRelaunch(new ArgsWrapper(args));
		*/
	}

	@Override
	public String getSyntaxConsole() {
		return "/restart";
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player) {
		return null;
	}

	@Override
	public String getInfoConsole() {
		return "Restarts the server";
	}

	@Override
	public String getInfoPlayer(EntityPlayer player) {
		return null;
	}

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player) {
		return false;
	}

}
