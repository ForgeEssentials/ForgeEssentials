package com.ForgeEssentials.permission;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import com.ForgeEssentials.util.FunctionHelper;

public class PermissionsList
{
	private static final String	OUTPUT_FILE	= "PermissionsList.txt";
	private File				output;

	public PermissionsList()
	{
		output = new File(ModulePermissions.permsFolder, OUTPUT_FILE);
	}

	public boolean shouldMake()
	{
		return !output.exists();
	}

	public void output(Set<String> permissions)
	{
		try
		{
			output.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(output));

			writer.write("#// ------------ PERMISSIONS LIST ------------ \\\\#"); writer.newLine();
			writer.write("#// ------------ "+FunctionHelper.getCurrentDateString()+" ------------ \\\\#");  									writer.newLine();
			writer.write("#// ------------------------------------------ \\\\#"); writer.newLine();
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
