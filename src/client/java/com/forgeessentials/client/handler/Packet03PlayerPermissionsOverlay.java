package com.forgeessentials.client.handler;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.PostLayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent.PreLayer;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import org.lwjgl.opengl.GL11;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

public class Packet03PlayerPermissionsOverlay extends GuiComponent
{

    protected ResourceLocation deniedPlaceTexture;

    protected ResourceLocation deniedBreakTexture;

    public Set<String> placeIds = new HashSet<>();;

    public Set<String> breakIds = new HashSet<>();;

    public Packet03PlayerPermissionsOverlay()
    {
        deniedPlaceTexture = new ResourceLocation(ForgeEssentialsClient.MODID.toLowerCase(),
                "textures/gui/denied_place.png");
        deniedBreakTexture = new ResourceLocation(ForgeEssentialsClient.MODID.toLowerCase(),
                "textures/gui/denied_break.png");
    }

    @SubscribeEvent
    public void placeEvent(RightClickItem e) {
        Minecraft instance = Minecraft.getInstance();
    	ItemStack stack = instance.player.getMainHandItem();
    	if(stack.getItem().equals(Blocks.AIR.asItem())) {
    		return;
    	}
    	String itemId = ForgeRegistries.ITEMS.getKey(stack.getItem()).toString();
    	if (placeIds.contains(itemId)) {
             e.setCanceled(true);
    	}
    }
    @SubscribeEvent
    public void placeEvent(RightClickBlock e) {
        Minecraft instance = Minecraft.getInstance();
    	ItemStack stack = instance.player.getMainHandItem();
    	if(stack.getItem().equals(Blocks.AIR.asItem())) {
    		return;
    	}
    	String itemId = ForgeRegistries.ITEMS.getKey(stack.getItem()).toString();
    	if (placeIds.contains(itemId)) {
             e.setCanceled(true);
    	}
    }

    @SubscribeEvent
    public void breakEvent(LeftClickBlock e) {
        Minecraft instance = Minecraft.getInstance();
        BlockState blockstate = instance.level.getBlockState(e.getPos());
        String blockId = ForgeRegistries.BLOCKS.getKey(blockstate.getBlock()).toString();
        if (breakIds.contains(blockId)) {
         e.setCanceled(true);
        }	
    }

    @SubscribeEvent
    public void renderGameOverlayEvent(RenderGameOverlayEvent event)
    {
        if (event.getType() == ElementType.LAYER)
        {
            IIngameOverlay overlay = null;
            if (event instanceof PreLayer) {
                overlay = ((PreLayer) event).getOverlay();
            } else if (event instanceof PostLayer) {
                overlay = ((PostLayer) event).getOverlay();
            }
            if (!event.isCancelable() && overlay == ForgeIngameGui.HOTBAR_ELEMENT)
            {
                Minecraft instance = Minecraft.getInstance();
                instance.getTextureManager().bindForSetup(deniedBreakTexture);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_BLEND);

                int width = instance.getWindow().getGuiScaledWidth();
                int height = instance.getWindow().getGuiScaledHeight();

                for (int i = 0; i < 9; ++i)
                {
                    ItemStack stack = instance.player.getInventory().getItem(i);
                    if (stack == ItemStack.EMPTY)
                        continue;
                    String itemId = ForgeRegistries.ITEMS.getKey(stack.getItem()).toString();
                    if (!placeIds.contains(itemId))
                        continue;
                    int x = width / 2 - 90 + i * 20 + 2;
                    int y = height - 16 - 3;
                    drawTexturedRect(x + 8, y + 1, 8, 8);
                }
            }
            else if (event.isCancelable() && overlay == ForgeIngameGui.CROSSHAIR_ELEMENT)
            {
                Minecraft instance = Minecraft.getInstance();
                float width = instance.getWindow().getGuiScaledWidth();
                float height = instance.getWindow().getGuiScaledHeight();

                Entity entity = instance.getCameraEntity();
                HitResult block = entity.pick(instance.player.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue(), 0.0F, false);
                if (block.getType() == HitResult.Type.BLOCK)
                {
                    BlockPos blockpos = ((BlockHitResult) block).getBlockPos();
                    BlockState blockstate = instance.level.getBlockState(blockpos);
                    String blockId = ForgeRegistries.BLOCKS.getKey(blockstate.getBlock()).toString();
                    if (breakIds.contains(blockId))
                    {
                        instance.textureManager.bindForSetup(deniedBreakTexture);
                        drawTexturedRect(width / 2 - 5, height / 2 - 5, 10, 10);
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    public void drawTexturedRect(double xPos, double yPos, double width, double height)
    {
        BufferBuilder wr = Tesselator.getInstance().getBuilder();
        wr.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_TEX);
        int zLevel = 1000;
        wr.vertex(xPos, yPos + height, zLevel).uv(0, 1).endVertex();
        wr.vertex(xPos + width, yPos + height, zLevel).uv(1, 1).endVertex();
        wr.vertex(xPos + width, yPos, zLevel).uv(1, 0).endVertex();
        wr.vertex(xPos, yPos, zLevel).uv(0, 0).endVertex();
        Tesselator.getInstance().end();
    }

}
