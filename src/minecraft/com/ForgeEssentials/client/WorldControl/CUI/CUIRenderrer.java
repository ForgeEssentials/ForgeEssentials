package com.ForgeEssentials.client.WorldControl.CUI;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.RenderManager;
import net.minecraft.src.Tessellator;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.ForgeEssentials.client.core.PlayerInfoClient;
import com.ForgeEssentials.client.core.ProxyClient;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import com.ForgeEssentials.AreaSelector.Point;
import com.ForgeEssentials.AreaSelector.Selection;
import com.ForgeEssentials.core.ForgeEssentials;

@SideOnly(value = Side.CLIENT)
public class CUIRenderrer
{

	@ForgeSubscribe
	public void render(RenderWorldLastEvent event)
	{
		EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
		PlayerInfoClient info = ProxyClient.info;

		if (player == null || (info.getPoint1() == null && info.getPoint2() == null))
			return;

		float ticks = event.partialTicks;

		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Tessellator tess = Tessellator.instance;
		tess.renderingWorldRenderer = false;
		
		// GL11.glLineWidth(20f);

		boolean render1 = false;

		// render p1
		if (info.getPoint1() != null)
		{
			Point p1 = info.getPoint1();
			GL11.glTranslated((float) p1.x - RenderManager.renderPosX, (float) (p1.y + 1) - RenderManager.renderPosY, (float) p1.z - RenderManager.renderPosZ);
			GL11.glScalef(1.0F, -1.0F, -1.0F);
			GL11.glColor3f(255,  0, 0);
			renderBlockBox(tess);
			render1 = true;
		}

		// render p2
		if (info.getPoint2() != null)
		{
			Point p1 = info.getPoint1();
			Point p2 = info.getPoint2();

			if (render1)
			{
				float x = (float) (p2.x - p1.x);
				float y = (float) (p1.y - p2.y) + 1;
				float z = (float) (p1.z - p2.z) - 1;

				GL11.glTranslated(x, y, z);
			}
			else
				GL11.glTranslated((float) p2.x - RenderManager.renderPosX, (float) (p2.y + 1) - RenderManager.renderPosY, (float) p2.z - RenderManager.renderPosZ);

			GL11.glScalef(1.0F, -1.0F, -1.0F);
			GL11.glColor3f(0, 255, 0);
			renderBlockBox(tess);
		}

		if (info.getSelection() != null)
		{
			Selection sel = info.getSelection();
			
			float x = (float) (sel.getLowPoint().x - sel.getEnd().x);
			float y = (float) (sel.getLowPoint().y - sel.getEnd().y);
			float z = (float) (sel.getLowPoint().z - sel.getEnd().z) -1 ;

			// translae tp the lowppoint.. hopefully...
			GL11.glTranslated(x, y, z);
			
			GL11.glScalef(1.0F, -1.0F, -1.0F);
			GL11.glColor3f(0, 5, 100);
			//renderBlockBox(tess);
			renderBlockBoxTo(tess, new Point(sel.getXDiff(), -sel.getYDiff(), -sel.getZDiff()));
		}

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		//tess.renderingWorldRenderer = true;
		GL11.glPopMatrix();
	}

	/**
	 * must be translated to proper point before calling
	 */
	private void renderBlockBox(Tessellator tess)
	{
		tess.startDrawing(GL11.GL_LINES);

		// FRONT
		tess.addVertex(0, 0, 0);
		tess.addVertex(0, 1, 0);
		
		tess.addVertex(0, 1, 0);
		tess.addVertex(1, 1, 0);
		
		tess.addVertex(1, 1, 0);
		tess.addVertex(1, 0, 0);
		
		tess.addVertex(1, 0, 0);
		tess.addVertex(0, 0, 0);

		// BACK
		tess.addVertex(0, 0, -1);
		tess.addVertex(0, 1, -1);
		tess.addVertex(0, 0, -1);
		tess.addVertex(1, 0, -1);
		tess.addVertex(1, 0, -1);
		tess.addVertex(1, 1, -1);
		tess.addVertex(0, 1, -1);
		tess.addVertex(1, 1, -1);

		// betweens.
		tess.addVertex(0, 0, 0);
		tess.addVertex(0, 0, -1);

		tess.addVertex(0, 1, 0);
		tess.addVertex(0, 1, -1);

		tess.addVertex(1, 0, 0);
		tess.addVertex(1, 0, -1);

		tess.addVertex(1, 1, 0);
		tess.addVertex(1, 1, -1);

		tess.draw();
	}
	
	private void renderBlockBoxTo(Tessellator tess, Point p2)
	{
		tess.startDrawing(GL11.GL_LINES);

		// FRONT
		tess.addVertex(0, 0, 0);
		tess.addVertex(0, p2.y, 0);
		
		tess.addVertex(0, p2.y, 0);
		tess.addVertex(p2.x, p2.y, 0);
		
		tess.addVertex(p2.x, p2.y, 0);
		tess.addVertex(p2.x, 0, 0);
		
		tess.addVertex(p2.x, 0, 0);
		tess.addVertex(0, 0, 0);

		// BACK
		tess.addVertex(0, 0, p2.z);
		tess.addVertex(0, p2.y, p2.z);
		tess.addVertex(0, 0, p2.z);
		tess.addVertex(p2.x, 0, p2.z);
		tess.addVertex(p2.x, 0, p2.z);
		tess.addVertex(p2.x, p2.y, p2.z);
		tess.addVertex(0, p2.y, p2.z);
		tess.addVertex(p2.x, p2.y, p2.z);

		// betweens.
		tess.addVertex(0, 0, 0);
		tess.addVertex(0, 0, p2.z);

		tess.addVertex(0, p2.y, 0);
		tess.addVertex(0, p2.y, p2.z);

		tess.addVertex(p2.x, 0, 0);
		tess.addVertex(p2.x, 0, p2.z);

		tess.addVertex(p2.x, p2.y, 0);
		tess.addVertex(p2.x, p2.y, p2.z);

		tess.draw();
	}

}
