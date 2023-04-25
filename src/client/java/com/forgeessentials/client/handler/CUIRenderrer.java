package com.forgeessentials.client.handler;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.forgeessentials.commons.network.packets.Packet1SelectionUpdate;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

@OnlyIn(Dist.CLIENT)
public class CUIRenderrer extends Packet1SelectionUpdate
{
    private static final float ALPHA = .25f;
    private static Selection selection;

    @SubscribeEvent
    public void render(RenderWorldLastEvent event)
    {
    	Minecraft instance = Minecraft.getInstance();
    	PlayerEntity player = instance.player;
        if (player == null)
            return;

        if (selection == null || selection.getDimension() != instance.player.clientLevel.dimension().location().toString())
            return;

        double renderPosX = TileEntityRendererDispatcher.instance.camera.getBlockPosition().getX();
        double renderPosY = TileEntityRendererDispatcher.instance.camera.getBlockPosition().getY();
        double renderPosZ = TileEntityRendererDispatcher.instance.camera.getBlockPosition().getZ();
        GL11.glPushMatrix();
        GL11.glTranslated(-renderPosX + 0.5, -renderPosY + 0.5, -renderPosZ + 0.5);

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
            if (selection.getStart() != null)
            {
                Point p = selection.getStart();
                GL11.glPushMatrix();
                GL11.glTranslated(p.getX(), p.getY(), p.getZ());
                GL11.glScalef(0.96F, 0.96F, 0.96F);
                if (seeThrough)
                    GL11.glColor4f(1, 0, 0, ALPHA);
                else
                    GL11.glColor3f(1, 0, 0);
                renderBox();
                GL11.glPopMatrix();
            }

            // render end
            if (selection.getEnd() != null)
            {
                Point p = selection.getEnd();
                GL11.glPushMatrix();
                GL11.glTranslated(p.getX(), p.getY(), p.getZ());
                GL11.glScalef(0.98F, 0.98F, 0.98F);
                if (seeThrough)
                    GL11.glColor4f(0, 1, 0, ALPHA);
                else
                    GL11.glColor3f(0, 1, 0);
                renderBox();
                GL11.glPopMatrix();
            }

            // render box
            if (selection.getStart() != null && selection.getEnd() != null)
            {
                Point p1 = selection.getStart();
                Point p2 = selection.getEnd();
                Point size = selection.getSize();
                GL11.glPushMatrix();
                GL11.glTranslated((float) (p1.getX() + p2.getX()) / 2, (float) (p1.getY() + p2.getY()) / 2, (float) (p1.getZ() + p2.getZ()) / 2);
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
        GL11.glPopMatrix();
    }

    /**
     * must be translated to proper point before calling
     */
    private static void renderBox()
    {
        BufferBuilder wr = Tessellator.getInstance().getBuilder();

        wr.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        // FRONT
        wr.vertex(-0.5, -0.5, -0.5).endVertex();
        wr.vertex(-0.5, 0.5, -0.5).endVertex();

        wr.vertex(-0.5, 0.5, -0.5).endVertex();
        wr.vertex(0.5, 0.5, -0.5).endVertex();

        wr.vertex(0.5, 0.5, -0.5).endVertex();
        wr.vertex(0.5, -0.5, -0.5).endVertex();

        wr.vertex(0.5, -0.5, -0.5).endVertex();
        wr.vertex(-0.5, -0.5, -0.5).endVertex();

        // BACK
        wr.vertex(-0.5, -0.5, 0.5).endVertex();
        wr.vertex(-0.5, 0.5, 0.5).endVertex();

        wr.vertex(-0.5, 0.5, 0.5).endVertex();
        wr.vertex(0.5, 0.5, 0.5).endVertex();

        wr.vertex(0.5, 0.5, 0.5).endVertex();
        wr.vertex(0.5, -0.5, 0.5).endVertex();

        wr.vertex(0.5, -0.5, 0.5).endVertex();
        wr.vertex(-0.5, -0.5, 0.5).endVertex();

        // betweens.
        wr.vertex(0.5, 0.5, -0.5).endVertex();
        wr.vertex(0.5, 0.5, 0.5).endVertex();

        wr.vertex(0.5, -0.5, -0.5).endVertex();
        wr.vertex(0.5, -0.5, 0.5).endVertex();

        wr.vertex(-0.5, -0.5, -0.5).endVertex();
        wr.vertex(-0.5, -0.5, 0.5).endVertex();

        wr.vertex(-0.5, 0.5, -0.5).endVertex();
        wr.vertex(-0.5, 0.5, 0.5).endVertex();
        
        Tessellator.getInstance().end();
    }

    @Override
	public void handle(Context context) {
    	Packet1SelectionUpdate packet1 = new Packet1SelectionUpdate();
    	selection = packet1.getSelection();
    	context.setPacketHandled(true);
	}
}
