package com.forgeessentials.client.cui;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.forgeessentials.client.core.ClientProxy;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;

//Depreciated
@SideOnly(value = Side.CLIENT)
public class CUIRenderrer
{

    private static final float ALPHA = .25f;

    @SubscribeEvent
    public void render(RenderWorldLastEvent event)
    {
        EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
        if (player == null)
            return;

        Selection sel = ClientProxy.getSelection();
        if (sel == null || sel.getDimension() != FMLClientHandler.instance().getClient().thePlayer.dimension)
            return;

        // RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        int renderPosX = 0; // renderManager.renderPosX;
        int renderPosY = 0; // renderManager.renderPosY;
        int renderPosZ = 0; // renderManager.renderPosZ;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glLineWidth(2);

        boolean seeThrough = true;
        while (true)
        {
            if (seeThrough)
            {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            }
            else
            {
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }

            // render start
            if (sel.getStart() != null)
            {
                Point p = sel.getStart();
                GL11.glPushMatrix();
                GL11.glTranslated(p.getX() - renderPosX + 0.5, p.getY() - renderPosY + 0.5, p.getZ() - renderPosZ + 0.5);
                GL11.glScalef(0.96F, 0.96F, 0.96F);
                if (seeThrough)
                    GL11.glColor4f(1, 0, 0, ALPHA);
                else
                    GL11.glColor3f(1, 0, 0);
                renderBox();
                GL11.glPopMatrix();
            }

            // render end
            if (sel.getEnd() != null)
            {
                Point p = sel.getEnd();
                GL11.glPushMatrix();
                GL11.glTranslated(p.getX() - renderPosX + 0.5, p.getY() - renderPosY + 0.5, p.getZ() - renderPosZ + 0.5);
                GL11.glScalef(0.98F, 0.98F, 0.98F);
                if (seeThrough)
                    GL11.glColor4f(0, 1, 0, ALPHA);
                else
                    GL11.glColor3f(0, 1, 0);
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
                GL11.glTranslated((float) (p1.getX() + p2.getX()) / 2 - renderPosX + 0.5, (float) (p1.getY() + p2.getY()) / 2 - renderPosY + 0.5,
                        (float) (p1.getZ() + p2.getZ()) / 2 - renderPosZ + 0.5);
                GL11.glScalef(1 + size.getX(), 1 + size.getY(), 1 + size.getZ());
                if (seeThrough)
                    GL11.glColor4f(0, 0, 1, ALPHA);
                else
                    GL11.glColor3f(0, 1, 1);
                renderBox();
                GL11.glPopMatrix();
            }

            if (!seeThrough)
                break;
            seeThrough = false;
        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    /**
     * must be translated to proper point before calling
     */
    private static void renderBox()
    {
        WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();
        renderer.startDrawing(GL11.GL_LINES);

        // FRONT
        renderer.addVertex(-0.5, -0.5, -0.5);
        renderer.addVertex(-0.5, 0.5, -0.5);

        renderer.addVertex(-0.5, 0.5, -0.5);
        renderer.addVertex(0.5, 0.5, -0.5);

        renderer.addVertex(0.5, 0.5, -0.5);
        renderer.addVertex(0.5, -0.5, -0.5);

        renderer.addVertex(0.5, -0.5, -0.5);
        renderer.addVertex(-0.5, -0.5, -0.5);

        // BACK
        renderer.addVertex(-0.5, -0.5, 0.5);
        renderer.addVertex(-0.5, 0.5, 0.5);

        renderer.addVertex(-0.5, 0.5, 0.5);
        renderer.addVertex(0.5, 0.5, 0.5);

        renderer.addVertex(0.5, 0.5, 0.5);
        renderer.addVertex(0.5, -0.5, 0.5);

        renderer.addVertex(0.5, -0.5, 0.5);
        renderer.addVertex(-0.5, -0.5, 0.5);

        // betweens.
        renderer.addVertex(0.5, 0.5, -0.5);
        renderer.addVertex(0.5, 0.5, 0.5);

        renderer.addVertex(0.5, -0.5, -0.5);
        renderer.addVertex(0.5, -0.5, 0.5);

        renderer.addVertex(-0.5, -0.5, -0.5);
        renderer.addVertex(-0.5, -0.5, 0.5);

        renderer.addVertex(-0.5, 0.5, -0.5);
        renderer.addVertex(-0.5, 0.5, 0.5);

        renderer.draw();
    }

}
