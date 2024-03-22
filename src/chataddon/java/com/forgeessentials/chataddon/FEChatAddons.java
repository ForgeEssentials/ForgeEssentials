package com.forgeessentials.chataddon;

import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod("fechataddons")
public class FEChatAddons{
	public FEChatAddons(){
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, 
        		()->new IExtensionPoint.DisplayTest(()->"ANY", (remote, isServer)-> true));
	}
}
