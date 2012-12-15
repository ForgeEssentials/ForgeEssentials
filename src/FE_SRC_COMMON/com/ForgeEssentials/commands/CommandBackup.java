package com.ForgeEssentials.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLLog;

public class CommandBackup extends ForgeEssentialsCommandBase{

	static String source;
	static String output;
	static List<String> fileList;
	static String backupdir;
	public static String backupName;

	@Override
	public String getCommandName() {
		return "backup";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args) {

	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args) {
		OutputHandler.SOP("Starting worldsave... You may experience server lag while this is being done.");
		MinecraftServer svr = MinecraftServer.getServer();
		if (svr.getConfigurationManager() != null){
			svr.getConfigurationManager().saveAllPlayerData();
		}
		try
        {
            for (int var4 = 0; var4 < svr.worldServers.length; ++var4)
            {
                if (svr.worldServers[var4] != null)
                {
                    WorldServer var5 = svr.worldServers[var4];
                    boolean var6 = var5.canNotSave;
                    var5.canNotSave = false;
                    var5.saveAllChunks(true, (IProgressUpdate)null);
                    var5.canNotSave = var6;
                }
            }
        }
		catch (MinecraftException var7){
			OutputHandler.SOP("Could not save world.");
		}
		OutputHandler.SOP("World save completed. Starting backup...");

        fileList = new ArrayList<String>();
		Calendar cal = Calendar.getInstance();
		if (svr.isDedicatedServer())	source = (new File(svr.getFolderName())).getAbsolutePath() + File.separator; 
		else source = new File("saves" + File.separator + svr.getFolderName()).getAbsolutePath() + File.separator;

	    OutputHandler.SOP("Generating backup from world " + source);

		generateFileList(new File(source));

		Integer day = cal.get(cal.DAY_OF_MONTH);
		Integer month = cal.get(cal.MONTH);
		Integer year = cal.get(cal.YEAR);
		Integer hour = cal.get(cal.HOUR_OF_DAY);
		Integer min = cal.get(cal.MINUTE);
		output = backupName.replaceAll("%day", day.toString())
				.replaceAll("%month", month.toString())
				.replaceAll("%year", year.toString())
				.replaceAll("%hour", hour.toString())
				.replaceAll("%min",	min.toString())
				.replaceAll("%world", svr.getFolderName());
		byte[] buffer = new byte[1024]; 
    	try
    	{ 
    		FileOutputStream fos = new FileOutputStream(backupdir + output + ".zip");
    		ZipOutputStream zos = new ZipOutputStream(fos);
            for(String file : fileList){
    			
    			ZipEntry ze= new ZipEntry(file);
    			zos.putNextEntry(ze);
 
    			FileInputStream in = new FileInputStream(source + file);
 
    			int len;
    			while ((len = in.read(buffer)) > 0) 
    			{
    				zos.write(buffer, 0, len);
    			}
 
    			in.close();
    		}
 
    		zos.closeEntry();
    		//remember close it
    		zos.close();
    		OutputHandler.SOP("Backup successfully completed and saved in ForgeEssentials/backups.");
    	}
    	catch(IOException ex)
    	{
    		FMLLog.severe(ex.getMessage());
    	}

		File backdir = new File(backupdir);
		if (!backdir.exists())
		{
			backdir.mkdirs();
		}

	}

	public static void generateFileList(File node)
    {
    	//add file only
    	if(node.isFile()){
    		fileList.add(generateZipEntry(node.getAbsolutePath().toString()));
    	}
    	
    	if(node.isDirectory()){
    		String[] subNote = node.list();
    		for(String filename : subNote){
    			generateFileList(new File(node, filename));
    		}
    	}
    }

	private static String generateZipEntry(String file){
    	return file.substring(source.length(), file.length());
    }

	@Override
	public boolean canConsoleUseCommand() {
		return true;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player) {
		return false;
	}

	@Override
	public String getCommandPerm() {
		//console command, controlled by /serverdo
		return null;
	}

}