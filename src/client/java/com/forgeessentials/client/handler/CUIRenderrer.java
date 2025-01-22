package com.forgeessentials.client.handler;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.forgeessentials.commons.network.Packet1SelectionUpdate;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;

@SideOnly(value = Side.CLIENT)
public class CUIRenderrer implements IMessageHandler<Packet1SelectionUpdate, IMessage>
{
    private static final float ALPHA = .25f;

    private static Selection selection;

    @SubscribeEvent
    public void render(RenderWorldLastEvent event)
    {
        EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
        if (player == null)
            return;

        if (selection == null || selection.getDimension() != FMLClientHandler.instance().getClient().thePlayer.dimension)
            return;

        double renderPosX = TileEntityRendererDispatcher.staticPlayerX;
        double renderPosY = TileEntityRendererDispatcher.staticPlayerY;
        double renderPosZ = TileEntityRendererDispatcher.staticPlayerZ;
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
    	WorldRenderer wr = Tessellator.getInstance().getWorldRenderer();

        wr.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

        // FRONT
        wr.pos(-0.5, -0.5, -0.5).endVertex();
        wr.pos(-0.5, 0.5, -0.5).endVertex();

        wr.pos(-0.5, 0.5, -0.5).endVertex();
        wr.pos(0.5, 0.5, -0.5).endVertex();

        wr.pos(0.5, 0.5, -0.5).endVertex();
        wr.pos(0.5, -0.5, -0.5).endVertex();

        wr.pos(0.5, -0.5, -0.5).endVertex();
        wr.pos(-0.5, -0.5, -0.5).endVertex();

        // BACK
        wr.pos(-0.5, -0.5, 0.5).endVertex();
        wr.pos(-0.5, 0.5, 0.5).endVertex();

        wr.pos(-0.5, 0.5, 0.5).endVertex();
        wr.pos(0.5, 0.5, 0.5).endVertex();

        wr.pos(0.5, 0.5, 0.5).endVertex();
        wr.pos(0.5, -0.5, 0.5).endVertex();

        wr.pos(0.5, -0.5, 0.5).endVertex();
        wr.pos(-0.5, -0.5, 0.5).endVertex();

        // betweens.
        wr.pos(0.5, 0.5, -0.5).endVertex();
        wr.pos(0.5, 0.5, 0.5).endVertex();

        wr.pos(0.5, -0.5, -0.5).endVertex();
        wr.pos(0.5, -0.5, 0.5).endVertex();

        wr.pos(-0.5, -0.5, -0.5).endVertex();
        wr.pos(-0.5, -0.5, 0.5).endVertex();

        wr.pos(-0.5, 0.5, -0.5).endVertex();
        wr.pos(-0.5, 0.5, 0.5).endVertex();

        Tessellator.getInstance().draw();
    }

    @Override
    public IMessage onMessage(Packet1SelectionUpdate message, MessageContext ctx)
    {
        selection = message.getSelection();
        return null;
    }

    @SubscribeEvent
    public void connectionOpened(ClientConnectedToServerEvent e)
    {
        selection = null;
    }

}
