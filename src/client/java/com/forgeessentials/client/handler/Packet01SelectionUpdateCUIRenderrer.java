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

	    final Tessellator tessellator = Tessellator.getInstance();
	    BufferBuilder bufferbuilder = tessellator.getBuilder();
	    final MatrixStack matrixStack = event.getMatrixStack();
	    Vector3d projectedView = instance.gameRenderer.getMainCamera().getPosition();
	
	    matrixStack.pushPose();
	    matrixStack.translate(-projectedView.x+.5, -projectedView.y+.5, -projectedView.z+.5);
	    RenderSystem.lineWidth(4);
	    Matrix4f matrix = matrixStack.last().pose();
	    if(selection.getStart()!=null) {
		    renderBox(matrix, bufferbuilder, tessellator, new Vector3d(selection.getStart().getX(), selection.getStart().getY(), selection.getStart().getZ())
		    		, 0.49, 0.49, 0.49, 1.0f, 0.0f, 0.0f, ALPHA);
	    }
	    if(selection.getEnd()!=null) {
		    renderBox(matrix, bufferbuilder, tessellator, new Vector3d(selection.getEnd().getX(), selection.getEnd().getY(), selection.getEnd().getZ())
		    		, 0.48, 0.48, 0.48, 0.0f, 0.0f, 1.0f, ALPHA);
	    }
	    if (selection.getStart() != null && selection.getEnd() != null)
        {
            Point p1 = selection.getStart();
            Point p2 = selection.getEnd();
            Point size = selection.getSize();
            Double x = (p1.getX() + p2.getX()) / 2.0d;
            Double y = (p1.getY() + p2.getY()) / 2.0d;
            Double z = (p1.getZ() + p2.getZ()) / 2.0d;
            renderBox(matrix, bufferbuilder, tessellator, new Vector3d(x, y, z)
		    		, (size.getX()/2d)+1d, (size.getY()/2d)+1d, (size.getZ()/2d)+1d, 0.0f, 1.0f, 0.0f, ALPHA);
        }
	
	    matrixStack.popPose();
    }

    private static void renderBox(Matrix4f matrix, BufferBuilder buffer, Tessellator tessellator, Vector3d cornerVertex, Double offsetX, Double offsetY, Double offsetZ, float r, float g, float b, float alpha)
    {
        // FRONT
    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x-offsetX), (float)(cornerVertex.y-offsetY), (float)(cornerVertex.z-offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x-offsetX), (float)(cornerVertex.y+offsetY), (float)(cornerVertex.z-offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x-offsetX), (float)(cornerVertex.y+offsetY), (float)(cornerVertex.z-offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x+offsetX), (float)(cornerVertex.y+offsetY), (float)(cornerVertex.z-offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x+offsetX), (float)(cornerVertex.y+offsetY), (float)(cornerVertex.z-offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x+offsetX), (float)(cornerVertex.y-offsetY), (float)(cornerVertex.z-offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x+offsetX), (float)(cornerVertex.y-offsetY), (float)(cornerVertex.z-offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x-offsetX), (float)(cornerVertex.y-offsetY), (float)(cornerVertex.z-offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();

        // BACK
    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x-offsetX), (float)(cornerVertex.y-offsetY), (float)(cornerVertex.z+offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x-offsetX), (float)(cornerVertex.y+offsetY), (float)(cornerVertex.z+offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x-offsetX), (float)(cornerVertex.y+offsetY), (float)(cornerVertex.z+offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x+offsetX), (float)(cornerVertex.y+offsetY), (float)(cornerVertex.z+offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x+offsetX), (float)(cornerVertex.y+offsetY), (float)(cornerVertex.z+offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x+offsetX), (float)(cornerVertex.y-offsetY), (float)(cornerVertex.z+offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x+offsetX), (float)(cornerVertex.y-offsetY), (float)(cornerVertex.z+offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x-offsetX), (float)(cornerVertex.y-offsetY), (float)(cornerVertex.z+offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();

        // betweens.
    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x+offsetX), (float)(cornerVertex.y+offsetY), (float)(cornerVertex.z-offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x+offsetX), (float)(cornerVertex.y+offsetY), (float)(cornerVertex.z+offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x+offsetX), (float)(cornerVertex.y-offsetY), (float)(cornerVertex.z-offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x+offsetX), (float)(cornerVertex.y-offsetY), (float)(cornerVertex.z+offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x-offsetX), (float)(cornerVertex.y-offsetY), (float)(cornerVertex.z-offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x-offsetX), (float)(cornerVertex.y-offsetY), (float)(cornerVertex.z+offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(cornerVertex.x-offsetX), (float)(cornerVertex.y+offsetY), (float)(cornerVertex.z-offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(cornerVertex.x-offsetX), (float)(cornerVertex.y+offsetY), (float)(cornerVertex.z+offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();
    }
}
