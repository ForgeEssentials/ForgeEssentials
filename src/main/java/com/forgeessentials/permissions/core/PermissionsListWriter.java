package com.forgeessentials.permissions.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collection;

import com.forgeessentials.permissions.ModulePermissions;
import com.forgeessentials.util.FunctionHelper;

public class PermissionsListWriter {

	private static final String OUTPUT_FILE = "PermissionsList.txt";

	private File output;

	public PermissionsListWriter()
	{
		output = new File(ModulePermissions.moduleFolder, OUTPUT_FILE);
		if (output.exists())
		{
			output.delete();
		}
	}

	public void write(Collection<String> permissions)
	{
		int permsize = permissions.size();
		try
		{
			output.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(output));
			writer.write("#// ------------ PERMISSIONS LIST ------------ \\\\#");
			writer.newLine();
			writer.write("#// --------------- " + FunctionHelper.getCurrentDateString() + " --------------- \\\\#");
			writer.newLine();
			writer.write("#// ------------ Total amount: " + permsize + " ------------ \\\\#");
			writer.newLine();
			writer.write("#// ------------------------------------------ \\\\#");
			writer.newLine();
			writer.newLine();
			for (String perm : permissions)
			{
				writer.write(perm);
				writer.newLine();
			}
			writer.close();
		}
		catch (Exception e)
		{

		}
	}
}
