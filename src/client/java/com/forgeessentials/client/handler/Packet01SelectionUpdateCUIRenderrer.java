package com.forgeessentials.client.handler;

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

        if (selection == null)
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
            renderBigBox(matrix, bufferbuilder, tessellator, new Vector3d(selection.getStart().getX(), selection.getStart().getY(), selection.getStart().getZ()),
            		new Vector3d(selection.getEnd().getX(), selection.getEnd().getY(), selection.getEnd().getZ())
		    		, 0.5, 0.5, 0.5, 0.0f, 1.0f, 0.0f, ALPHA);
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

    private static void renderBigBox(Matrix4f matrix, BufferBuilder buffer, Tessellator tessellator, Vector3d v, Vector3d v2, Double offsetX, Double offsetY, Double offsetZ, float r, float g, float b, float alpha)
    {
    	Double x1;
    	Double y1;
    	Double z1;
    	Double x2;
    	Double y2;
    	Double z2;
    	if(v.x>v2.x) {
    		x1=v.x;
    		x2=v2.x;
    	}
    	else if(v.x<v2.x) {
    		x1=v2.x;
    		x2=v.x;
    	}
    	else {
    		x1=x2=v.x;
    	}
    	if(v.y>v2.y) {
    		y1=v.y;
    		y2=v2.y;
    	}
    	else if(v.y<v2.y) {
    		y1=v2.y;
    		y2=v.y;
    	}
    	else {
    		y1=y2=v.y;
    	}
    	if(v.z>v2.z) {
    		z1=v.z;
    		z2=v2.z;
    	}
    	else if(v.z<v2.z) {
    		z1=v2.z;
    		z2=v.z;
    	}
    	else {
    		z1=z2=v.z;
    	}
        // FRONT
    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(x1+offsetX), (float)(y1+offsetY), (float)(z1+offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(x1+offsetX), (float)(y2-offsetY), (float)(z1+offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(x1+offsetX), (float)(y1+offsetY), (float)(z1+offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(x1+offsetX), (float)(y1+offsetY), (float)(z2-offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(x1+offsetX), (float)(y1+offsetY), (float)(z2-offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(x1+offsetX), (float)(y2-offsetY), (float)(z2-offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(x1+offsetX), (float)(y2-offsetY), (float)(z1+offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(x1+offsetX), (float)(y2-offsetY), (float)(z2-offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();

        // BACK
    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(x2-offsetX), (float)(y1+offsetY), (float)(z1+offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(x2-offsetX), (float)(y2-offsetY), (float)(z1+offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(x2-offsetX), (float)(y1+offsetY), (float)(z1+offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(x2-offsetX), (float)(y1+offsetY), (float)(z2-offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(x2-offsetX), (float)(y1+offsetY), (float)(z2-offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(x2-offsetX), (float)(y2-offsetY), (float)(z2-offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(x2-offsetX), (float)(y2-offsetY), (float)(z1+offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(x2-offsetX), (float)(y2-offsetY), (float)(z2-offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();

        // betweens.
    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(x1+offsetX), (float)(y1+offsetY), (float)(z1+offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(x2-offsetX), (float)(y1+offsetY), (float)(z1+offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(x1+offsetX), (float)(y2-offsetY), (float)(z1+offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(x2-offsetX), (float)(y2-offsetY), (float)(z1+offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(x1+offsetX), (float)(y1+offsetY), (float)(z2-offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(x2-offsetX), (float)(y1+offsetY), (float)(z2-offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();

    	buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, (float)(x1+offsetX), (float)(y2-offsetY), (float)(z2-offsetZ)).color(r, g, b, alpha).endVertex();
        buffer.vertex(matrix, (float)(x2-offsetX), (float)(y2-offsetY), (float)(z2-offsetZ)).color(r, g, b, alpha).endVertex();
        tessellator.end();
    }
}
