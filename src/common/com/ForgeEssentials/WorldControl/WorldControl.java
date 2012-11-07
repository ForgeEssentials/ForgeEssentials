package com.ForgeEssentials.WorldControl;

import java.io.File;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.Vec3;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.WorldControl.commands.*;
import com.ForgeEssentials.core.FEConfig;
import com.ForgeEssentials.core.ForgeEssentials;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

// central class for all the WorldControl stuff
public class WorldControl
{
	// implicit constructor WorldControl()
	
	
	// load.
	public void preLoad(FMLPreInitializationEvent event)
	{
		
	}
	
	// load.
	public void load(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new WandController());
	}
	
	// load.
	public void serverStarting(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CommandWand());
		e.registerServerCommand(new CommandPos(1));
		e.registerServerCommand(new CommandPos(2));
	}
}
