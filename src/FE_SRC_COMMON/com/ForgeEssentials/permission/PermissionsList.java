package com.ForgeEssentials.permission;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import com.google.common.base.Strings;

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

			writer.write("#// ------------ PERMISSIONS LIST ------------ \\#");  writer.newLine();
			writer.write("#// ------------ Created on "+getDateString());  writer.newLine();
			writer.write("#// ------------------------------------------ \\#");  writer.newLine();
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
	
	private String getDateString()
	{
		Calendar cal = Calendar.getInstance();
		StringBuilder builder = new StringBuilder();
		builder.append(cal.get(Calendar.MONTH)).append(' ');
		builder.append(cal.get(Calendar.DAY_OF_MONTH)).append(", ");
		builder.append(cal.get(Calendar.YEAR));
		return builder.toString();
	}

}
