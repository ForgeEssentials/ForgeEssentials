package com.forgeessentials.client.cui;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.core.PlayerInfoClient;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

//Depreciated
@SideOnly(value = Side.CLIENT)
public class CUIRenderrer {

    @SubscribeEvent
    public void render(RenderWorldLastEvent event)
    {
        EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
        PlayerInfoClient info = ForgeEssentialsClient.info;

        if (player == null || info == null || info.getPoint1() == null && info.getPoint2() == null)
        // OutputHandler.devdebug("NOT RENDERRING");
        {
            return;
        }

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator tess = Tessellator.instance;
        Tessellator.renderingWorldRenderer = false;

        // GL11.glLineWidth(20f);

        boolean render1 = false;

        // render p1
        if (info.getPoint1() != null)
        {
            Point p1 = info.getPoint1();
            GL11.glTranslated(p1.x - RenderManager.renderPosX, p1.y + 1 - RenderManager.renderPosY, p1.z - RenderManager.renderPosZ);
            GL11.glScalef(1.0F, -1.0F, -1.0F);
            GL11.glColor3f(255, 0, 0);
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
                float x = p2.getX() - p1.getX();
                float y = (float) (p1.getY() - p2.getY()) + 1;
                float z = (float) (p1.getZ() - p2.getZ()) - 1;

                GL11.glTranslated(x, y, z);
            }
            else
            {
                GL11.glTranslated(p2.x - RenderManager.renderPosX, p2.y + 1 - RenderManager.renderPosY, p2.z - RenderManager.renderPosZ);
            }

            GL11.glScalef(1.0F, -1.0F, -1.0F);
            GL11.glColor3f(0, 255, 0);
            renderBlockBox(tess);
        }

        if (info.getSelection() != null)
        {
            Selection sel = info.getSelection();

            float x = sel.getLowPoint().getX() - sel.getEnd().getX();
            float y = sel.getLowPoint().getY() - sel.getEnd().getY();
            float z = (float) (sel.getLowPoint().getZ() - sel.getEnd().getZ()) - 1;

            // translate to the low point..
            GL11.glTranslated(x, y, z);

            GL11.glScalef(1.0F, -1.0F, -1.0F);
            GL11.glColor3f(0, 5, 100);
            // renderBlockBox(tess);
            renderBlockBoxTo(tess, new Point(sel.getXLength(), -sel.getYLength(), -sel.getZLength()));
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        // tess.renderingWorldRenderer = true;
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
