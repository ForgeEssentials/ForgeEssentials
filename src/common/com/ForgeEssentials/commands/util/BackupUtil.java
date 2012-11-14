package com.ForgeEssentials.commands.util;

import java.io.File;
import java.util.List;

import net.minecraft.server.MinecraftServer;

public class BackupUtil {

	static List<String> files;
	static String backupLoc;
	static MinecraftServer server;
	
	/**
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
}
