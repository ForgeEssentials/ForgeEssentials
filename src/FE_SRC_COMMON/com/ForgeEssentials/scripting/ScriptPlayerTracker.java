package com.ForgeEssentials.scripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.IPlayerTracker;

public class ScriptPlayerTracker implements IPlayerTracker{

	static List<String> loginscripts = new ArrayList<String>();
	static List<String> respawnscripts = new ArrayList<String>();
	static File scriptfolder = new File(ForgeEssentials.FEDIR, "scripting/");
	static File loginplayer = new File(scriptfolder, "login/player/");
	static File logingroup = new File(scriptfolder, "login/group/");
	static File respawngroup = new File(scriptfolder, "respawn/group/");
	static File respawnplayer = new File(scriptfolder, "respawn/player/");
	
	@Override
	public void onPlayerLogin(EntityPlayer player) {
		OutputHandler.felog.info("Running command scripts for player " + player.username + " on login");
		
		//  run player scripts
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
			OutputHandler.felog.warning("Could not find command script for player " + player.username + ", ignoring!");
		}
		// now run group scripts - must be global
		try{
			File gscript = new File(logingroup, APIRegistry.perms.getHighestGroup(player).name + ".txt");
			FileInputStream stream = new FileInputStream(gscript);
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
			OutputHandler.felog.warning("Could not find command script for group " + APIRegistry.perms.getHighestGroup(player).toString() + ", ignoring!");
		}
		finally{
			for (Object s : loginscripts.toArray()){
				String s1 = s.toString();
				MinecraftServer.getServer().getCommandManager().executeCommand(player, s1);
				OutputHandler.felog.info("Successfully run command scripts for player "+ player.username);
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
		OutputHandler.felog.info("Running command scripts for player " + player.username + " on respawn.");
		
		
		//  run player scripts
		try{
			File pscript = new File(respawnplayer, player.username+ ".txt");
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
				respawnscripts.add(read);

				// read the next string
				read = reader.readLine();
				
				reader.close();
				streamReader.close();
				stream.close();

			}
		}catch (Exception e){
			OutputHandler.felog.warning("Could not find command script for player " + player.username + ", ignoring!");
		}
		// now run group scripts - must be global
		try{
			File gscript = new File(respawngroup, APIRegistry.perms.getHighestGroup(player).name + ".txt");
			FileInputStream stream = new FileInputStream(gscript);
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
				respawnscripts.add(read);

				// read the next string
				read = reader.readLine();
				
				reader.close();
				streamReader.close();
				stream.close();

			}
		}catch (Exception e){
			OutputHandler.felog.warning("Could not find command script for group " + APIRegistry.perms.getHighestGroup(player).toString() + ", ignoring!");
		}
		finally{
			for (Object s : respawnscripts.toArray()){
				String s1 = s.toString();
				MinecraftServer.getServer().getCommandManager().executeCommand(player, s1);
				OutputHandler.felog.info("Successfully run command scripts for player "+ player.username);
			}
		}
	}

}