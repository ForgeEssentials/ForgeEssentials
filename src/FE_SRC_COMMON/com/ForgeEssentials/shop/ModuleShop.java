package com.ForgeEssentials.shop.event;

import net.minecraftforge.common.MinecraftForge;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.core.moduleLauncher.FEModule;
import com.ForgeEssentials.core.moduleLauncher.FEModule.*;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@FEModule(name = "Shop", parentMod = ForgeEssentials.class)
public class ModuleShop {

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new SignListener());
	}

}
