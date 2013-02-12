package com.ForgeEssentials.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class Backup implements Runnable
{
	private Thread			thread;
	private WorldServer		world;
	private boolean			isWorld;
	private String			name;
	private File			basefolder = ModuleBackup.baseFolder;
	private File			folder;
	private List<String>	fileList;
	private File			source;
	private String			backupName;

	public Backup()
	{
		for(int i : DimensionManager.getIDs())
		{
			new Backup(i);
		}
	}
	
	public Backup(int dim)
	{
		this.isWorld = true;
		this.world = MinecraftServer.getServer().worldServerForDimension(dim);
		this.name = world.getWorldInfo().getWorldName() + " DIM " + dim;
		this.source = world.getChunkSaveLocation();
		this.folder = new File(this.basefolder, name.replaceAll(" ", "_"));
		this.backupName = getFilename() + ".zip";

		thread = new Thread(this, "ForgeEssentials - Backup - " + this.name);
		thread.start();
	}
	
	public Backup(File folder)
	{
		this.isWorld = false;
		this.name = folder.getName();
		this.source = folder;
		this.folder = new File(this.basefolder, name.replaceAll(" ", "_"));
		this.backupName = getFilename() + ".zip";
		
		thread = new Thread(this, "ForgeEssentials - Backup - " + this.name);
		thread.start();
	}
	
	@Override
	public void run()
	{
		msg("Starting backup of " + name);

		if (!folder.exists())
		{
			folder.mkdirs();
		}
		
		if(isWorld)
		{
			boolean canNotSave = world.canNotSave;
			world.canNotSave = false;

			try
			{
				world.saveAllChunks(true, (IProgressUpdate) null);
				doFolder(source);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				msg("Error while making backup of " + name);
				msg(e.toString());
			}	
			world.canNotSave = canNotSave;
		}
		else
		{
			doFolder(source);
		}
		
		msg("Backup done.");
	}

	public void msg(String msg)
	{
		MinecraftServer.logger.info(msg);
		for (int var2 = 0; var2 < FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.size(); ++var2)
		{
			((EntityPlayerMP) FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.get(var2)).sendChatToPlayer(FEChatFormatCodes.AQUA + msg);
		}
	}

	/**
	 * Constructs filename
	 * @return filename
	 */
	private String getFilename()
	{
		Calendar cal = Calendar.getInstance();
		Integer day = cal.get(Calendar.DAY_OF_MONTH);
		Integer month = cal.get(Calendar.MONTH);
		Integer year = cal.get(Calendar.YEAR);
		Integer hour = cal.get(Calendar.HOUR_OF_DAY);
		Integer min = cal.get(Calendar.MINUTE);
		return BackupConfig.backupName.replaceAll("%day", day.toString())
				.replaceAll("%month", month.toString())
				.replaceAll("%year", year.toString())
				.replaceAll("%hour", hour.toString())
				.replaceAll("%min", min.toString())
				.replaceAll("%name", name.replaceAll(" ", "_"));
	}
	
	/**
	 * make a folder backup
	 * @param folder
	 */
	private void doFolder(File folder)
	{
		fileList = new ArrayList<String>();
		String dir = folder.getAbsolutePath().replace(folder.getName(), "");

		generateFileList(dir, folder);
		zipIt(dir);
	}

	/**
	 * Make the filelist
	 * @param dir
	 * @param node
	 */
	private void generateFileList(String dir, File node)
	{
		if (node.isFile())
		{
			fileList.add(generateZipEntry(dir, node.getAbsolutePath().toString()));
		}

		if (node.isDirectory())
		{
			String[] subNote = node.list();
			for (String filename : subNote)
			{
				if(!filename.startsWith("DIM"))
				{
					generateFileList(dir, new File(node, filename));
				}
			}
		}
	}

	/**
	 * format the entry
	 * @param dir
	 * @param file
	 * @return
	 */
	private String generateZipEntry(String dir, String file)
	{
		return file.substring(dir.length(), file.length());
	}

	/**
	 * Make the actual zip from the filelist
	 * @param dir
	 */
	private void zipIt(String dir)
	{
		byte[] buffer = new byte[1024];
		try
		{
			FileOutputStream fos = new FileOutputStream(new File(folder, backupName));
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (String file : fileList)
			{
				ZipEntry ze = new ZipEntry(file);
				zos.putNextEntry(ze);

				FileInputStream in = new FileInputStream(dir + file);

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
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
