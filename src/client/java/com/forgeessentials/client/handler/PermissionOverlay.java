package com.forgeessentials.client.handler;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.client.core.ClientProxy;
import com.forgeessentials.commons.network.Packet3PlayerPermissions;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.registry.GameData;

public class PermissionOverlay extends Gui implements IMessageHandler<Packet3PlayerPermissions, IMessage>
{

    protected ResourceLocation deniedPlaceTexture;

    protected ResourceLocation deniedBreakTexture;

    protected Packet3PlayerPermissions permissions = new Packet3PlayerPermissions();

    public PermissionOverlay()
    {
        if (!ClientProxy.allowPermissionRender)
        {
            return;
        }
        MinecraftForge.EVENT_BUS.register(this);
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
        }
        return null;
    }

    @SubscribeEvent
    public void renderGameOverlayEvent(RenderGameOverlayEvent event)
    {
        if (!event.isCancelable() && event.type == ElementType.HOTBAR)
        {
            Minecraft.getMinecraft().renderEngine.bindTexture(deniedPlaceTexture);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);

            ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            int width = res.getScaledWidth();
            int height = res.getScaledHeight();

            for (int i = 0; i < 9; ++i)
            {
                ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.mainInventory[i];
                if (stack == null || !(stack.getItem() instanceof ItemBlock))
                    continue;
                Block block = ((ItemBlock) stack.getItem()).field_150939_a;
                int blockId = GameData.getBlockRegistry().getId(block);
                if (!permissions.placeIds.contains(blockId))
                    continue;
                int x = width / 2 - 90 + i * 20 + 2;
                int y = height - 16 - 3;
                drawTexturedRect(x + 8, y + 1, 8, 8);
            }
        }
        else if (event.isCancelable() && event.type == ElementType.CROSSHAIRS)
        {
            ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            int width = res.getScaledWidth();
            int height = res.getScaledHeight();

            MovingObjectPosition mop = Minecraft.getMinecraft().objectMouseOver;
            if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK)
            {
                Block block = Minecraft.getMinecraft().theWorld.getBlock(mop.blockX, mop.blockY, mop.blockZ);
                int blockId = GameData.getBlockRegistry().getId(block);
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
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(xPos, yPos + height, zLevel, 0, 1);
        tessellator.addVertexWithUV(xPos + width, yPos + height, zLevel, 1, 1);
        tessellator.addVertexWithUV(xPos + width, yPos, zLevel, 1, 0);
        tessellator.addVertexWithUV(xPos, yPos, zLevel, 0, 0);
        tessellator.draw();
    }

}
