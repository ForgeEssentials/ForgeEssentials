package com.ForgeEssentials.scripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.api.APIRegistry;
import com.ForgeEssentials.util.OutputHandler;

public enum EventType {
	LOGIN("login", ModuleScripting.logingroup, ModuleScripting.loginplayer),
	RESPAWN("respawn", ModuleScripting.respawngroup, ModuleScripting.respawnplayer);
	
	protected String name;
	protected File group;
	protected File player;
	
	private EventType(String name, File group, File player){
		this.name = name;
		this.group = group;
		this.player = player;
		
	}
	public static EventType getEventTypeForName(String name){
		if(name.equals("login"))
			return EventType.LOGIN;
		else if(name.equals("respawn"))
			return EventType.RESPAWN;
		else return null;
	}
	public static void run(EntityPlayer player, EventType event){
		ArrayList<String> scripts = new ArrayList<String>();
		OutputHandler.felog.info("Running command scripts for player " + player.username);	
				
				//  run player scripts
				try{
					File pscript = new File(event.player, player.username+ ".txt");

					OutputHandler.felog.info("Reading command script file " + pscript.getAbsolutePath());	
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
						scripts.add(read);

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
					File gscript = new File(event.group, APIRegistry.perms.getHighestGroup(player).name + ".txt");
					OutputHandler.felog.info("Reading command script file " + gscript.getAbsolutePath());	
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
						scripts.add(read);

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
					for (Object s : scripts.toArray()){
						String s1 = s.toString();
						MinecraftServer.getServer().getCommandManager().executeCommand(player, s1);
						OutputHandler.felog.info("Successfully run command scripts for player "+ player.username);
					}
				}
			}

}
