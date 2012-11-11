package com.ForgeEssentials.client.core;

import java.util.EnumSet;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.RenderManager;
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

		float x = (float) RenderManager.renderPosX;
		float y = (float) RenderManager.renderPosY;
		float z = (float) RenderManager.renderPosZ;

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor3f(255,  0,  0);
		//GL11.glLineWidth(20f);
        
        GL11.glTranslatef((float)5-x, (float)5-y, (float)5-z);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        //GL11.glTranslatef(.5F, .5F, .5F);
        
		GL11.glBegin(GL11.GL_LINES);
		
        // FRONT
		GL11.glVertex3f(0, 0, 0);
		GL11.glVertex3f(0, 1, 0);
		GL11.glVertex3f(0, 0, 0);
		GL11.glVertex3f(1, 0, 0);
		GL11.glVertex3f(1, 0, 0);
		GL11.glVertex3f(1, 1, 0);
		GL11.glVertex3f(0, 1, 0);
		GL11.glVertex3f(1, 1, 0);
		
		// BACK
		GL11.glVertex3f(0, 0, -1);
		GL11.glVertex3f(0, 1, -1);
		GL11.glVertex3f(0, 0, -1);
		GL11.glVertex3f(1, 0, -1);
		GL11.glVertex3f(1, 0, -1);
		GL11.glVertex3f(1, 1, -1);
		GL11.glVertex3f(0, 1, -1);
		GL11.glVertex3f(1, 1, -1);
		
		// betweens.
		GL11.glVertex3f(0, 0, 0);
		GL11.glVertex3f(0, 0, -1);
		
		GL11.glVertex3f(0, 1, 0);
		GL11.glVertex3f(0, 1, -1);
		
		GL11.glVertex3f(1, 0, 0);
		GL11.glVertex3f(1, 0, -1);
		
		GL11.glVertex3f(1, 1, 0);
		GL11.glVertex3f(1, 1, -1);
		
		GL11.glEnd();
		
		// betweens.
		
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}
}
