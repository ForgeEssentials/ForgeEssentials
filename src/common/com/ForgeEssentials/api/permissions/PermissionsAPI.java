package com.ForgeEssentials.api.permissions;

import java.util.ArrayList;

import com.ForgeEssentials.core.ForgeEssentials;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;


public class PermissionsAPI
{
	public static ArrayList<IPermissionsRegister> registers = new ArrayList<IPermissionsRegister>();
	
	public static boolean checkPermAllowed(PermQueryPlayer query)
	{
		MinecraftForge.EVENT_BUS.post(query);
		return query.getResult().equals(Result.ALLOW);
	}

	public static Result checkPermResult(PermQueryPlayer query)
	{
		MinecraftForge.EVENT_BUS.post(query);
		return query.getResult();
	}
	
	public static void registerPermissionsRegistrar(IPermissionsRegister register)
	{
		registers.add(register);
	}
}
