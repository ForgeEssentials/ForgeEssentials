package com.ForgeEssentials.client.core;

import java.util.EnumSet;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

import net.minecraft.src.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public class CUIRenderrer
{

	@ForgeSubscribe
	public void render(RenderWorldLastEvent event)
	{
		GL11.glBegin(GL11.GL_LINES);
		// EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
		// GL11.glTranslated(player.posX, player.posY, player.posZ);
		// GL11.glVertex3d(player.posX+2, player.posY+2, player.posZ+2);
		// GL11.glVertex3d(player.posX+20, player.posY+20, player.posZ+20);
		GL11.glColor3d(100, 100, 100);
		GL11.glVertex3d(2, 2, 2);
		GL11.glVertex3d(20, 20, 20);
		GL11.glEnd();
	}
}
