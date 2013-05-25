package com.ForgeEssentials.core.misc.scripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.api.permissions.Group;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.IPlayerTracker;

public class ScriptPlayerTracker implements IPlayerTracker{

	static List<String> loginscripts = new ArrayList<String>();
	static List<String> logoutscripts = new ArrayList<String>();
	static File scriptfolder;
	static File loginplayer;
	
	@Override
	public void onPlayerLogin(EntityPlayer player) {
		OutputHandler.info("Running command scripts!");
		try{
			if (!scriptfolder.exists()){
			scriptfolder  = new File(ForgeEssentials.FEDIR, "scripting");
			loginplayer = new File(scriptfolder, "login/player/");
			}
		}catch (Exception e){
			OutputHandler.warning("Something broke - we couldn't setup the scripting folders. You'll have to do it yourself.");
		}
		
		// now run player scripts
		try{
			File pscript = new File(loginplayer, player.username+ ".txt");
			FileInputStream stream = new FileInputStream(pscript);
			InputStreamReader streamReader = new InputStreamReader(stream);
			BufferedReader reader = new BufferedReader(streamReader);
			String read = reader.readLine();
			while (read != null)
			{
				// ignore the comment things...
				if (read.startsWith("#"))
				{
					read = reader.readLine();
					continue;
				}

				// add to the rules list.
				loginscripts.add(read);

				// read the next string
				read = reader.readLine();
				
				reader.close();
				streamReader.close();
				stream.close();

			}
		}catch (Exception e){
			OutputHandler.warning("Could not find command script for player " + player.username + ", ignoring!");
		}finally{
			for (Object s : loginscripts.toArray()){
				String s1 = s.toString();
				MinecraftServer.getServer().getCommandManager().executeCommand(player, s1);
				OutputHandler.info("Successfully run command scripts for player "+ player.username);
			}
		}
		
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
		// do nothing
		
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
		// do nothing 
		
	}

}
