package com.ForgeEssentials.client.gui;

import java.util.EnumSet;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class FEKeyBinding extends KeyHandler{

	public FEKeyBinding(KeyBinding[] keyBindings) {
		super(new KeyBinding[] { new KeyBinding("ForgeEssentials Menu", 88) }, new boolean[] { false });
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return "forgeessentials.keyhandler";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
		
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		
		if (FMLClientHandler.instance().getClient().currentScreen == null)
        {
                FMLClientHandler.instance().getClient().displayGuiScreen(new GuiFEMain());
                World world = FMLClientHandler.instance().getClient().theWorld;
        }
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

}
