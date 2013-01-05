package com.ForgeEssentials.backup;

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
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.FMLLog;

public class BackupThread extends Thread
{
	private final File		backupDir;
	public static String	backupName;

	String					source;
	String					output;
	List<String>			fileList;
	ICommandSender			user;
	MinecraftServer			server;

	public BackupThread(ICommandSender user, MinecraftServer server)
	{
		backupDir = new File(BackupConfig.backupDir.getAbsolutePath());
		this.server = server;
		this.user = user;
	}

	@Override
	public void run()
	{
		// backing up.
		user.sendChatToPlayer("World save completed. Starting backup...");

		// get sources...
		if (server.isDedicatedServer())
			source = new File(server.getFolderName()).getAbsolutePath() + File.separator;
		else
			source = new File("saves" + File.separator + server.getFolderName()).getAbsolutePath() + File.separator;

		user.sendChatToPlayer("Generating backup from world " + source);

		// generate file-list
		fileList = new ArrayList<String>();
		generateFileList(new File(source));

		// create output
		Calendar cal = Calendar.getInstance();
		Integer day = cal.get(Calendar.DAY_OF_MONTH);
		Integer month = cal.get(Calendar.MONTH);
		Integer year = cal.get(Calendar.YEAR);
		Integer hour = cal.get(Calendar.HOUR_OF_DAY);
		Integer min = cal.get(Calendar.MINUTE);
		String output = backupName.replaceAll("%day", day.toString()).replaceAll("%month", month.toString()).replaceAll("%year", year.toString()).replaceAll("%hour", hour.toString()).replaceAll("%min", min.toString()).replaceAll("%world", server.getFolderName());

		byte[] buffer = new byte[1024];
		try
		{
			FileOutputStream fos = new FileOutputStream(new File(backupDir, output + ".zip"));
			ZipOutputStream zos = new ZipOutputStream(fos);
			for (String file : fileList)
			{

				ZipEntry ze = new ZipEntry(file);
				zos.putNextEntry(ze);

				FileInputStream in = new FileInputStream(source + file);

				int len;
				while ((len = in.read(buffer)) > 0)
					zos.write(buffer, 0, len);

				in.close();
			}

			zos.closeEntry();
			zos.close();
			user.sendChatToPlayer("Backup successfully completed and saved in " + backupDir.getAbsolutePath());
		}
		catch (IOException ex)
		{
			FMLLog.severe(ex.getMessage());
		}

		ModuleBackup.thread = null;
	}

	private void generateFileList(File node)
	{
		// add file only
		if (node.isFile())
			fileList.add(generateZipEntry(node.getAbsolutePath().toString()));

		if (node.isDirectory())
		{
			String[] subNote = node.list();
			for (String filename : subNote)
				generateFileList(new File(node, filename));
		}
	}

	private String generateZipEntry(String file)
	{
		return file.substring(source.length(), file.length());
	}
}