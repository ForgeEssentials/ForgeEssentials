package com.ForgeEssentials.auth;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import com.ForgeEssentials.util.OutputHandler;

public class pwdSaver 
{
	public static File dir;
	
	static
	{
		dir = new File(ModuleAuth.config.getFile().getParent(), "playerdata");
		dir.mkdirs();
	}
	
	public static boolean isRegisted(String username)	
	{
		return new File(dir, username + ".pwd").exists();
	}
	
	public static pwdData getData(String username) 
	{
		try
		{
			pwdData data = new pwdData();
			FileInputStream fstream = new FileInputStream(new File(dir, username + ".pwd"));
			
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			data.encPwd = br.readLine().getBytes();
			data.salt = br.readLine().getBytes();
			
			in.close();
			return data;
		}
		catch (Exception e)
		{
			OutputHandler.severe("Error: " + e);
		}
		return null;
	}

	public static void setData(String username, pwdData data)
	{
		try
		{
			PrintWriter out = new PrintWriter(new FileWriter(new File(dir, username + ".pwd")));
			
			out.println(new String(data.encPwd));
			out.println(new String(data.salt));
			
			out.close();
		}
		catch (Exception e)
		{
			OutputHandler.severe("Error: " + e);
		}
	}
}
