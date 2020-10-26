package com.forgeessentials.client.handler;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import org.lwjgl.opengl.GL11;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.commons.network.Packet3PlayerPermissions;

public class PermissionOverlay extends Gui implements IMessageHandler<Packet3PlayerPermissions, IMessage>
{

    protected ResourceLocation deniedPlaceTexture;

    protected ResourceLocation deniedBreakTexture;

    protected Packet3PlayerPermissions permissions = new Packet3PlayerPermissions();

    public PermissionOverlay()
    {
        deniedPlaceTexture = new ResourceLocation(ForgeEssentialsClient.MODID.toLowerCase(), "textures/gui/denied_place.png");
        deniedBreakTexture = new ResourceLocation(ForgeEssentialsClient.MODID.toLowerCase(), "textures/gui/denied_break.png");
        zLevel = 100;
    }

    @Override
    public IMessage onMessage(Packet3PlayerPermissions message, MessageContext ctx)
    {
        if (message.reset)
        {
            permissions = message;
        }
        else
        {
            permissions.placeIds.addAll(message.placeIds);
            permissions.breakIds.addAll(message.breakIds);

            EntityPlayerSP player = Minecraft.getMinecraft().player;
            ItemStack stack = player.getHeldItemMainhand();
            if (stack != null)
            {
                int itemId = Item.REGISTRY.getIDForObject((stack.getItem()));
                for (int id : message.placeIds)
                    if (itemId == id)
                    {
                        player.stopActiveHand();
                        break;
                    }
            }
        }
        return null;
    }

    @SubscribeEvent
    public void renderGameOverlayEvent(RenderGameOverlayEvent event)
    {
        if (!event.isCancelable() && event.getType() == ElementType.HOTBAR)
        {
            Minecraft.getMinecraft().renderEngine.bindTexture(deniedPlaceTexture);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);

            ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
            int width = res.getScaledWidth();
            int height = res.getScaledHeight();

            for (int i = 0; i < 9; ++i)
            {
                ItemStack stack = Minecraft.getMinecraft().player.inventory.mainInventory.get(i);
                if (stack == null)
                    continue;
                int id = Item.REGISTRY.getIDForObject(stack.getItem());
                if (!permissions.placeIds.contains(id))
                    continue;
                int x = width / 2 - 90 + i * 20 + 2;
                int y = height - 16 - 3;
                drawTexturedRect(x + 8, y + 1, 8, 8);
            }
        }
        else if (event.isCancelable() && event.getType() == ElementType.CROSSHAIRS)
        {
            ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
            float width = res.getScaledWidth();
            float height = res.getScaledHeight();

            RayTraceResult mop = Minecraft.getMinecraft().objectMouseOver;
            if (mop != null && mop.typeOfHit == Type.BLOCK)
            {
                IBlockState block = Minecraft.getMinecraft().world.getBlockState(mop.getBlockPos());
                int blockId = Block.REGISTRY.getIDForObject(block.getBlock());
                if (permissions.breakIds.contains(blockId))
                {
                    Minecraft.getMinecraft().renderEngine.bindTexture(deniedBreakTexture);
                    drawTexturedRect(width / 2 - 5, height / 2 - 5, 10, 10);
                    event.setCanceled(true);
                }
            }
        }
    }

    public void drawTexturedRect(double xPos, double yPos, double width, double height)
    {
        BufferBuilder wr = Tessellator.getInstance().getBuffer();
        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        wr.pos(xPos, yPos + height, zLevel).tex(0, 1).endVertex();
        wr.pos(xPos + width, yPos + height, zLevel).tex(1, 1).endVertex();
        wr.pos(xPos + width, yPos, zLevel).tex(1, 0).endVertex();
        wr.pos(xPos, yPos, zLevel).tex(0, 0).endVertex();
        Tessellator.getInstance().draw();
    }

}
