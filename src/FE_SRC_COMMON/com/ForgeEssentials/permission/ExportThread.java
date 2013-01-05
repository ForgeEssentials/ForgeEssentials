package com.ForgeEssentials.permission;

import net.minecraft.command.ICommandSender;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ExportThread extends Thread
{
	File exportDir;
	ICommandSender user;
	
	public ExportThread(String exportDir, ICommandSender sender)
	{
		this.exportDir = new File(ModulePermissions.permsFolder, exportDir);
		user = sender;
	}
	
	
	public void run()
	{
		user.sendChatToPlayer(" {PermSQL} getting dump...");
		HashMap<String, Object> map = SqlHelper.dump();
		user.sendChatToPlayer(" {PermSQL} dump complete.");
		
		user.sendChatToPlayer(" {PermSQL} Savin permissions");
		FlatFilePermissions permissions = new FlatFilePermissions(exportDir);
		permissions.save((ArrayList<PermissionHolder>)map.get("playerPerms"), (ArrayList<PermissionHolder>)map.get("groupPerms"));
		
		user.sendChatToPlayer(" {PermSQL} Export COmplete");
	}
	
	private void output(String msg)
	{
		
	}
}
