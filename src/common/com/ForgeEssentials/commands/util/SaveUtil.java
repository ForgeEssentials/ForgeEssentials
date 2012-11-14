package com.ForgeEssentials.commands.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.IProgressUpdate;
import net.minecraft.src.MinecraftException;
import net.minecraft.src.WorldServer;

import com.ForgeEssentials.core.OutputHandler;

public class SaveUtil {
	
		public static void saveGame(){
		OutputHandler.SOP("Saving worlds...");
		
		MinecraftServer var3 = MinecraftServer.getServer();
        
        if (var3.getConfigurationManager() != null)
        {
            var3.getConfigurationManager().saveAllPlayerData();
        }

        try
        {
            for (int var4 = 0; var4 < var3.worldServers.length; ++var4)
            {
                if (var3.worldServers[var4] != null)
                {
                    WorldServer var5 = var3.worldServers[var4];
                    boolean var6 = var5.canNotSave;
                    var5.canNotSave = false;
                    var5.saveAllChunks(true, (IProgressUpdate)null);
                    var5.canNotSave = var6;
                }
            }
        }
        catch (MinecraftException var7)
        {
        	OutputHandler.SOP("We're sorry, but we could not save the game.");
        }
        
        OutputHandler.SOP("World save complete");
	}

}
