package com.ForgeEssentials.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.ForgeEssentials.util.OutputHandler;

import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class Backup implements Runnable
{
	private Thread			thread;
	private WorldServer		world;
	private boolean			isWorld;
	private String			name;
	private File			basefolder	= ModuleBackup.baseFolder;
	private File			folder;
	private List<String>	fileList;
	private File			source;
	private String			backupName;
	private boolean			worldSave;
	private boolean			done		= false;

	public Backup(boolean worldSave)
	{
		for (int i : DimensionManager.getIDs())
		{
			new Backup(i, worldSave);
		}
	}

	public Backup(int dim, boolean worldSave)
	{
		isWorld = true;
		this.worldSave = worldSave;
		world = DimensionManager.getWorld(dim);

		if (BackupConfig.backupIfUnloaded || BackupConfig.whitelist.contains(dim))
		{
			if (world == null)
			{
				DimensionManager.initDimension(dim);
				world = DimensionManager.getWorld(dim);
			}
		}
		else
		{
			if (world == null)
				return;
		}

		name = world.getWorldInfo().getWorldName() + " DIM " + dim;
		source = world.getChunkSaveLocation();
		folder = new File(basefolder, name.replaceAll(" ", "_"));
		backupName = getFilename() + ".zip";

		thread = new Thread(this, "ForgeEssentials - Backup - " + name);
		thread.start();
	}

	public Backup(WorldServer world, boolean worldSave)
	{
		isWorld = true;
		this.worldSave = worldSave;
		this.world = world;

		name = world.getWorldInfo().getWorldName() + " DIM " + world.provider.dimensionId;
		source = world.getChunkSaveLocation();
		folder = new File(basefolder, name.replaceAll(" ", "_"));
		backupName = getFilename() + ".zip";

		thread = new Thread(this, "ForgeEssentials - Backup - " + name);
		thread.start();
	}

	public Backup(File folder)
	{
		isWorld = false;
		name = folder.getName();
		source = folder;
		this.folder = new File(basefolder, name.replaceAll(" ", "_"));
		backupName = getFilename() + ".zip";

		thread = new Thread(this, "ForgeEssentials - Backup - " + name);
		thread.start();
	}

	@Override
	public void run()
	{
		ModuleBackup.msg("Starting backup of " + name);

		if (!folder.exists())
		{
			folder.mkdirs();
		}

		/*
		 * Only needed when making a world backup.
		 * Saves the world to disk and turns off saving.
		 */
		boolean canNotSave = true;
		if (isWorld && worldSave)
		{
			canNotSave = world.canNotSave;
			world.canNotSave = false;
			try
			{
				world.saveAllChunks(true, (IProgressUpdate) null);
				doFolder(source);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				ModuleBackup.msg("Error while making backup of " + name);
				ModuleBackup.msg(e.toString());
			}
		}

		/*
		 * Does actual backup
		 */
		doFolder(source);

		/*
		 * Turns worls save back on if it was on.
		 */
		if (isWorld && worldSave)
		{
			world.canNotSave = canNotSave;
		}

		ModuleBackup.msg("Backup of " + name + " done.");
		done = true;

		System.gc();
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
				if (!filename.startsWith("DIM"))
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
		if(fileList.isEmpty())
		{
			OutputHandler.info("No files to backup in " + dir);
			return;
		}
		
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
			zos.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public boolean isDone()
	{
		return done;
	}
}
