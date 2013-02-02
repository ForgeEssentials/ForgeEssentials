package com.ForgeEssentials.commands.util;

import java.util.Set;

import com.ForgeEssentials.api.commands.EnumMobType;
import com.ForgeEssentials.api.commands.FEMob;
import com.ForgeEssentials.api.commands.FEMob.IsTamed;
import com.ForgeEssentials.api.modules.FEModule;
import com.ForgeEssentials.api.modules.event.FEModulePreInitEvent;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class MobTypeLoader
{
	public static void preLoad(FEModulePreInitEvent event)
	{
		OutputHandler.info("Discovering and loading modules...");
		OutputHandler.info("If you would like to disable a module, please look in ForgeEssentials/main.cfg.");

		// started ASM handling for the module loaidng.
		Set<ASMData> data = ((FMLPreInitializationEvent)event.getFMLEvent()).getAsmData().getAll(FEMob.class.getName());
		
		String className;
		EnumMobType type;
		boolean isTameable = false;
		boolean hasTameableAnnot = false;
		for(ASMData asm : data)
		{
			Class c = null;
			className = asm.getClassName();
			
			try
			{
				c = Class.forName(className);
			}
			catch (Exception e)
			{
				OutputHandler.info("Error trying to load " + asm.getClassName() + " as a FEMob!");
				e.printStackTrace();
				return;
			}
			
			if (!c.isAnnotationPresent(FEModule.class))
			{
				throw new IllegalArgumentException(c.getName() + " doesn't have the @FEMob annotation!");
			}
			FEMob annot = (FEMob) c.getAnnotation(FEMob.class);
			if (annot == null)
			{
				throw new IllegalArgumentException(c.getName() + " doesn't have the @FEMob annotation!");
			}
			
			type = annot.type();
			if(type == EnumMobType.TAMEABLE)
			{
				if(!c.isAnnotationPresent(IsTamed.class))
				{
					throw new IllegalArgumentException(c.getName() + " is registered as Tameable and doesn't have the @IsTamed annotation!");
				}
			}
			
			MobTypeRegistry.addMob(type, className);
		}
	}
}
