package com.forgeessentials.client.cui;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.core.PlayerInfoClient;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//Depreciated
@SideOnly(value = Side.CLIENT)
public class CUIRenderrer {

    @SubscribeEvent
    public void render(RenderWorldLastEvent event)
    {
        EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
        PlayerInfoClient info = ForgeEssentialsClient.info;
        if (player == null || info == null)
            return;
        
        Selection sel = info.getSelection();
        if (sel == null || sel.getDimension() != FMLClientHandler.instance().getClient().thePlayer.dimension)
            return;

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(2);

        // render start
        if (sel.getStart() != null)
        {
            Point p = sel.getStart();
            GL11.glPushMatrix();
            GL11.glTranslated(p.x - RenderManager.renderPosX + 0.5, p.y - RenderManager.renderPosY + 0.5, p.z - RenderManager.renderPosZ + 0.5);
            GL11.glScalef(0.96F, 0.96F, 0.96F);
            GL11.glColor3f(255, 0, 0);
            renderBox();
            GL11.glPopMatrix();
        }

        // render end
        if (sel.getEnd() != null)
        {
            Point p = sel.getEnd();
            GL11.glPushMatrix();
            GL11.glTranslated(p.x - RenderManager.renderPosX + 0.5, p.y - RenderManager.renderPosY + 0.5, p.z - RenderManager.renderPosZ + 0.5);
            GL11.glScalef(0.98F, 0.98F, 0.98F);
            GL11.glColor3f(0, 255, 0);
            renderBox();
            GL11.glPopMatrix();
        }

        // render box
        if (sel.getStart() != null && sel.getEnd() != null)
        {
            Point p1 = sel.getStart();
            Point p2 = sel.getEnd();
            Point size = sel.getSize();
            GL11.glPushMatrix();
            GL11.glTranslated((float) (p1.x + p2.x) / 2 - RenderManager.renderPosX + 0.5, (float) (p1.y + p2.y) / 2 - RenderManager.renderPosY + 0.5, (float) (p1.z + p2.z) / 2 - RenderManager.renderPosZ + 0.5);
            GL11.glScalef(1 + size.getX(), 1 + size.getY(), 1 + size.getZ());
            GL11.glColor3f(0, 255, 255);
            renderBox();
            GL11.glPopMatrix();
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    /**
     * must be translated to proper point before calling
     */
    private static void renderBox()
    {
        Tessellator.instance.startDrawing(GL11.GL_LINES);

        // FRONT
        Tessellator.instance.addVertex(-0.5, -0.5, -0.5);
        Tessellator.instance.addVertex(-0.5, 0.5, -0.5);

        Tessellator.instance.addVertex(-0.5, 0.5, -0.5);
        Tessellator.instance.addVertex(0.5, 0.5, -0.5);

        Tessellator.instance.addVertex(0.5, 0.5, -0.5);
        Tessellator.instance.addVertex(0.5, -0.5, -0.5);

        Tessellator.instance.addVertex(0.5, -0.5, -0.5);
        Tessellator.instance.addVertex(-0.5, -0.5, -0.5);

        // BACK
        Tessellator.instance.addVertex(-0.5, -0.5, 0.5);
        Tessellator.instance.addVertex(-0.5, 0.5, 0.5);

        Tessellator.instance.addVertex(-0.5, 0.5, 0.5);
        Tessellator.instance.addVertex(0.5, 0.5, 0.5);

        Tessellator.instance.addVertex(0.5, 0.5, 0.5);
        Tessellator.instance.addVertex(0.5, -0.5, 0.5);

        Tessellator.instance.addVertex(0.5, -0.5, 0.5);
        Tessellator.instance.addVertex(-0.5, -0.5, 0.5);

        // betweens.
        Tessellator.instance.addVertex(0.5, 0.5, -0.5);
        Tessellator.instance.addVertex(0.5, 0.5, 0.5);

        Tessellator.instance.addVertex(0.5, -0.5, -0.5);
        Tessellator.instance.addVertex(0.5, -0.5, 0.5);

        Tessellator.instance.addVertex(-0.5, -0.5, -0.5);
        Tessellator.instance.addVertex(-0.5, -0.5, 0.5);

        Tessellator.instance.addVertex(-0.5, 0.5, -0.5);
        Tessellator.instance.addVertex(-0.5, 0.5, 0.5);
        
        Tessellator.instance.draw();
    }

}
