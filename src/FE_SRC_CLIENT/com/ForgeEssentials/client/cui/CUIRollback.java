package com.ForgeEssentials.client.cui;

import java.util.Iterator;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.ForgeEssentials.client.ForgeEssentialsClient;
import com.ForgeEssentials.client.PlayerInfoClient;
import com.ForgeEssentials.client.util.ClientPoint;

import cpw.mods.fml.client.FMLClientHandler;

public class CUIRollback
{
    int i = 0;
    
    @ForgeSubscribe
    public void render(RenderWorldLastEvent event)
    {
        EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
        Entity entity = event.context.mc.renderViewEntity;
        PlayerInfoClient info = ForgeEssentialsClient.getInfo();

        if (player == null || info == null || info.rbList.isEmpty())
            return;

        GL11.glPushMatrix();
        
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glLineWidth(1.5F);
        GL11.glBegin(GL11.GL_LINES);
        
        GL11.glColor4f(1, 0, 0, 1);
        
        i ++;
        
        if (i % 240 == 0) System.out.println(ForgeEssentialsClient.getInfo().rbList.keySet().size());
        
        for (ClientPoint p : ForgeEssentialsClient.getInfo().rbList.keySet())
        {
            if (i % 240 == 0) System.out.println(p.x + "; " + p.y + "; " + p.z);
            
            double x = p.getX() - RenderManager.renderPosX;
            double z = p.getZ() - RenderManager.renderPosZ;
            double y = p.getY() - RenderManager.renderPosY;
            
            /*
            GL11.glVertex3d(x, y, z);
            GL11.glVertex3d(x+1, y, z);
            
            GL11.glVertex3d(x, y, z);
            GL11.glVertex3d(x, y+1, z);
            
            GL11.glVertex3d(x, y, z);
            GL11.glVertex3d(x, y, z+1);
            */
        }
        
        GL11.glEnd();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        
        GL11.glPopMatrix();
    }
    
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
}
