package com.ForgeEssentials.client.core;

import java.util.EnumSet;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntityRenderer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;

public class CUIRenderrer
{

	@ForgeSubscribe
	public void render(RenderWorldLastEvent event)
	{
		EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;

		if (player == null)
			return;

		float ticks = event.partialTicks;

		float x = (float) (player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) ticks);
		float y = (float) (player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) ticks);
		float z = (float) (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) ticks);

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glTranslatef(x, y, z);
		GL11.glColor3f(255,  0,  0);
		GL11.glLineWidth(90f);
        GL11.glScalef(-1.0F, -1.0F, -1.0F);
        
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3f(0, y+1, 0);
		GL11.glVertex3f(0, y+2, 0);
		GL11.glEnd();
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}
}
