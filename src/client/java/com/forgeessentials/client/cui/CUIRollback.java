package com.forgeessentials.client.cui;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.core.PlayerInfoClient;
import com.forgeessentials.commons.Point;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class CUIRollback {
    @SubscribeEvent
    public void render(RenderWorldLastEvent event)
    {
        EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
        PlayerInfoClient info = ForgeEssentialsClient.info;

        if (player == null || info == null || info.rbList.isEmpty())
        {
            return;
        }

        GL11.glPushMatrix();

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glLineWidth(1.5F);
        GL11.glBegin(GL11.GL_LINES);

        for (Point p : ForgeEssentialsClient.info.rbList.keySet())
        {
            switch (ForgeEssentialsClient.info.rbList.get(p))
            {
            // Break
            case 0:
                GL11.glColor4f(1, 0, 0, 0.5F);
                break;
            // Place
            case 1:
                GL11.glColor4f(0, 1, 0, 0.5F);
                break;
            // Interact
            case 2:
                GL11.glColor4f(0, 0, 1, 0.5F);
                break;
            default:
                GL11.glColor4f(1, 1, 1, 0.5F);
                break;
            }

            renderBlockBox(p);
        }

        GL11.glEnd();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL11.glPopMatrix();
    }

    private void renderBlockBox(Point p)
    {
        double x = p.getX() - RenderManager.renderPosX;
        double z = p.getZ() - RenderManager.renderPosZ + 1;
        double y = p.getY() - RenderManager.renderPosY;

        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x, y + 1, z);

        GL11.glVertex3d(x, y + 1, z);
        GL11.glVertex3d(x + 1, y + 1, z);

        GL11.glVertex3d(x + 1, y + 1, z);
        GL11.glVertex3d(x + 1, y, z);

        GL11.glVertex3d(x + 1, y, z);
        GL11.glVertex3d(x, y, z);

        GL11.glVertex3d(x, y, z - 1);
        GL11.glVertex3d(x, y + 1, z - 1);

        GL11.glVertex3d(x, y, z - 1);
        GL11.glVertex3d(x + 1, y, z - 1);

        GL11.glVertex3d(x + 1, y, z - 1);
        GL11.glVertex3d(x + 1, y + 1, z - 1);

        GL11.glVertex3d(x, y + 1, z - 1);
        GL11.glVertex3d(x + 1, y + 1, z - 1);

        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x, y, z - 1);

        GL11.glVertex3d(x, y + 1, z);
        GL11.glVertex3d(x, y + 1, z - 1);

        GL11.glVertex3d(x + 1, y, z);
        GL11.glVertex3d(x + 1, y, z - 1);

        GL11.glVertex3d(x + 1, y + 1, z);
        GL11.glVertex3d(x + 1, y + 1, z - 1);
    }
}
