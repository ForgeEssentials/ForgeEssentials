package com.ForgeEssentials.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

import com.ForgeEssentials.core.FEConfig;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.OutputHandler;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

public class CommandBackup extends ForgeEssentialsCommandBase{

	static List<String> files;
	static String backupLoc;
	static MinecraftServer server;
	
	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processCommandPlayer(EntityPlayer player, String[] args) {
		
		
	}

	/** Not implemented
	public void makeFileList(File node){
	if(node.isFile()){
			files.add(makeZipEntry(node.getAbsolutePath().toString()));
		}
		if (node.isDirectory());{
		String[] subNote = node.list();	
		for (String filename : subNote){
			makeFileList(node, filename));
		}
		}
		*/
			
		
		@Override
	    //TODO fix this
		public void processCommandConsole(ICommandSender sender, String[] args) {
		OutputHandler.SOP("Not implemented");
			/** Not implemented
		OutputHandler.SOP("Starting backup...");
		files = new ArrayList<String>();
		Calendar calendar = Calendar.getInstance();
		if (server.isDedicatedServer())	backupLoc = (new File(server.getFolderName())).getAbsolutePath() + File.separator; 
		else backupLoc = new File("saves" + File.separator + server.getFolderName()).getAbsolutePath() + File.separator;
		File backupDir = new File(ForgeEssentials.FEDIR, "/backups");
		if (!backupDir.exists()){
			backupDir.mkdirs();
		}
		OutputHandler.SOP("Backing up world from " + backupLoc + "to" + backupDir);
		makeFileList(new File(backupLoc));
		Integer day = calendar.get(calendar.DAY_OF_MONTH);
		Integer month = calendar.get(calendar.MONTH);
		Integer year = calendar.get(calendar.YEAR);
		Integer hr = calendar.get(calendar.HOUR_OF_DAY);
		Integer min = calendar.get(calendar.MINUTE);
		}
		*/
		
	}

	@Override
	public String getSyntaxConsole() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSyntaxPlayer(EntityPlayer player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInfoConsole() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getInfoPlayer(EntityPlayer player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canConsoleUseCommand() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player) {
		// TODO Auto-generated method stub
		return false;
	}

}
