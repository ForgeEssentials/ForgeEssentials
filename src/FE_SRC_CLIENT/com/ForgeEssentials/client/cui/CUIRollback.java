package com.ForgeEssentials.client.cui;

import java.util.Iterator;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
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
        PlayerInfoClient info = ForgeEssentialsClient.getInfo();

        if (player == null || info == null || info.rbList.isEmpty())
            return;

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator tess = Tessellator.instance;
        Tessellator.renderingWorldRenderer = false;
        
        double x = RenderManager.renderPosX, y = RenderManager.renderPosY, z = RenderManager.renderPosZ;
        
        i ++;
        
        if (i % 240 == 0) System.out.println(ForgeEssentialsClient.getInfo().rbList.keySet().size());
        
        for (ClientPoint p : ForgeEssentialsClient.getInfo().rbList.keySet())
        {
            GL11.glTranslated(p.x - x, p.y - y + 1, p.z - z - 1);
        
            if (i % 240 == 0) System.out.println(p.x + "; " + p.y + "; " + p.z);
            
            x = p.x;
            y = p.y;
            z = p.z;
            
            GL11.glScalef(1.0F, -1.0F, -1.0F);
            
            if (ForgeEssentialsClient.getInfo().rbList.get(p))
                GL11.glColor3f(200, 0, 20);
            else
                GL11.glColor3f(0, 145, 40);
            
            renderBlockBox(tess);
        }
        
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
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
