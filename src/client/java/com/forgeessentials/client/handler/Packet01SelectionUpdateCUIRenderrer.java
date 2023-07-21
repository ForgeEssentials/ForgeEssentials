package com.forgeessentials.client.handler;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Packet01SelectionUpdateCUIRenderrer
{
    private static final float ALPHA = .25f;
    public Selection selection;


    @SubscribeEvent
    public void render(RenderWorldLastEvent event)
    {
        Minecraft instance = Minecraft.getInstance();
        PlayerEntity player = instance.player;
        if (player == null)
            return;

        if (selection == null
                || !selection.getDimension().equals(instance.player.clientLevel.dimension().location().toString()))
            return;

        renderLine(event, selection.getStart(), selection.getEnd());
        

//        // render box
//        if (selection.getStart() != null && selection.getEnd() != null)
//        {
//            Point p1 = selection.getStart();
//            Point p2 = selection.getEnd();
//            Point size = selection.getSize();
//            GL11.glPushMatrix();
//            GL11.glTranslated((float) (p1.getX() + p2.getX()) / 2, (float) (p1.getY() + p2.getY()) / 2,
//                    (float) (p1.getZ() + p2.getZ()) / 2);
//            GL11.glScalef(1 + size.getX(), 1 + size.getY(), 1 + size.getZ());
//            GL11.glPopMatrix();
//        }
    }

	public static void renderLine(RenderWorldLastEvent event, Point pointStart, Point pointEnd)
	{
	    final Tessellator tessellator = Tessellator.getInstance();
	    BufferBuilder bufferbuilder = tessellator.getBuilder();
	    //RenderSystem.disableRescaleNormal();
	    //RenderSystem.scalef(1.0F, 1.0F, 1.0F);
	    final MatrixStack matrixStack = event.getMatrixStack();
	    Minecraft instance = Minecraft.getInstance();
	    Vector3d projectedView = instance.gameRenderer.getMainCamera().getPosition();
	
	    matrixStack.pushPose();
	    matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
	    RenderSystem.lineWidth(4);
	    Matrix4f matrix = matrixStack.last().pose();
	    if(pointStart!=null) {
		    renderBox(matrix, bufferbuilder, tessellator, new Vector3d(pointStart.getX(), pointStart.getY(), pointStart.getZ())
		    		, 0.5, 1.0f, 0.0f, 0.0f, ALPHA);
	    }
	    if(pointEnd!=null) {
		    renderBox(matrix, bufferbuilder, tessellator, new Vector3d(pointEnd.getX(), pointEnd.getY(), pointEnd.getZ())
		    		, 0.9, 0.0f, 0.0f, 1.0f, ALPHA);
	    }
	
	    matrixStack.popPose();
	}

    private static void renderBox(Matrix4f matrix, BufferBuilder buffer, Tessellator tessellator, Vector3d cornerVertex, Double offset, float r, float g, float b, float alpha)
    {
        // FRONT
    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x-offset), (float)(cornerVertex.y), (float)(cornerVertex.z-offset)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x-offset), (float)(cornerVertex.y), (float)(cornerVertex.z-offset)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x-offset), (float)(cornerVertex.y+offset), (float)(cornerVertex.z-offset)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x+offset), (float)(cornerVertex.y+offset), (float)(cornerVertex.z-offset)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x+offset), (float)(cornerVertex.y+offset), (float)(cornerVertex.z-offset)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x+offset), (float)(cornerVertex.y-offset), (float)(cornerVertex.z-offset)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x+offset), (float)(cornerVertex.y-offset), (float)(cornerVertex.z-offset)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x-offset), (float)(cornerVertex.y-offset), (float)(cornerVertex.z-offset)).color(r, g, b, alpha).endVertex();
        tessellator.end();

        // BACK
    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x-offset), (float)(cornerVertex.y-offset), (float)(cornerVertex.z+offset)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x-offset), (float)(cornerVertex.y+offset), (float)(cornerVertex.z+offset)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x-offset), (float)(cornerVertex.y+offset), (float)(cornerVertex.z+offset)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x+offset), (float)(cornerVertex.y+offset), (float)(cornerVertex.z+offset)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x+offset), (float)(cornerVertex.y+offset), (float)(cornerVertex.z+offset)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x+offset), (float)(cornerVertex.y-offset), (float)(cornerVertex.z+offset)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x+offset), (float)(cornerVertex.y-offset), (float)(cornerVertex.z+offset)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x-offset), (float)(cornerVertex.y-offset), (float)(cornerVertex.z+offset)).color(r, g, b, alpha).endVertex();
        tessellator.end();

        // betweens.
    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x+offset), (float)(cornerVertex.y+offset), (float)(cornerVertex.z-offset)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x+offset), (float)(cornerVertex.y+offset), (float)(cornerVertex.z+offset)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x+offset), (float)(cornerVertex.y-offset), (float)(cornerVertex.z-offset)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x+offset), (float)(cornerVertex.y-offset), (float)(cornerVertex.z+offset)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x-offset), (float)(cornerVertex.y-offset), (float)(cornerVertex.z-offset)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x-offset), (float)(cornerVertex.y-offset), (float)(cornerVertex.z+offset)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x-offset), (float)(cornerVertex.y+offset), (float)(cornerVertex.z-offset)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x-offset), (float)(cornerVertex.y+offset), (float)(cornerVertex.z+offset)).color(r, g, b, alpha).endVertex();
        tessellator.end();
    }
}
