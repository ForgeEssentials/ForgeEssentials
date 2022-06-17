package com.forgeessentialsclient.handler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.lwjgl.opengl.GL11;

import com.forgeessentialsclient.ForgeEssentialsClient;
import com.forgeessentialsclient.utils.commons.network.packets.Packet3PlayerPermissions;

public class PermissionOverlay extends AbstractGui
{

    protected ResourceLocation deniedPlaceTexture;

    protected ResourceLocation deniedBreakTexture;

    protected Packet3PlayerPermissions permissions = new Packet3PlayerPermissions();

    private int zLevel = 100;
    
    public PermissionOverlay()
    {
        deniedPlaceTexture = new ResourceLocation(ForgeEssentialsClient.MODID.toLowerCase(), "textures/gui/denied_place.png");
        deniedBreakTexture = new ResourceLocation(ForgeEssentialsClient.MODID.toLowerCase(), "textures/gui/denied_break.png");
    }

    @SubscribeEvent
    public void renderGameOverlayEvent(RenderGameOverlayEvent event)
    {
        if (!event.isCancelable() && event.getType() == ElementType.HOTBAR)
        {
        	Minecraft instance = Minecraft.getInstance();
        	instance.getTextureManager().bind(deniedBreakTexture);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            
            int width = instance.getWindow().getGuiScaledWidth();
            int height = instance.getWindow().getGuiScaledHeight();

            for (int i = 0; i < 9; ++i)
            {
            	ItemStack stack = instance.player.inventory.getItem(i);
                if (stack == ItemStack.EMPTY)
                    continue;
                int id = Item.getId(stack.getItem());
                if (!permissions.placeIds.contains(id))
                    continue;
                int x = width / 2 - 90 + i * 20 + 2;
                int y = height - 16 - 3;
                drawTexturedRect(x + 8, y + 1, 8, 8);
            }
        }
        else if (event.isCancelable() && event.getType() == ElementType.CROSSHAIRS)
        {
        	Minecraft instance = Minecraft.getInstance();
        	float width = instance.getWindow().getGuiScaledWidth();
        	float height = instance.getWindow().getGuiScaledHeight();

            RayTraceResult mop = instance.hitResult;
            if (mop != null && mop.hitInfo == Type.BLOCK)
            {
                BlockState block = instance.level.getBlockState(new BlockPos(mop.getLocation()));
                int blockId = Block.getId(block);
                if (permissions.breakIds.contains(blockId))
                {
                	instance.textureManager.bind(deniedBreakTexture);
                	drawTexturedRect(width / 2 - 5, height / 2 - 5, 10, 10);
                	event.setCanceled(true);
                }
            }
        }
    }

    public void drawTexturedRect(double xPos, double yPos, double width, double height)
    {
        BufferBuilder wr = Tessellator.getInstance().getBuilder();
        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        wr.vertex(xPos, yPos + height, zLevel).uv(0, 1).endVertex();
        wr.vertex(xPos + width, yPos + height, zLevel).uv(1, 1).endVertex();
        wr.vertex(xPos + width, yPos, zLevel).uv(1, 0).endVertex();
        wr.vertex(xPos, yPos, zLevel).uv(0, 0).endVertex();
        Tessellator.getInstance().end();
    }

}
