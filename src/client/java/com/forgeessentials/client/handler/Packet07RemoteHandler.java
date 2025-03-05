package com.forgeessentials.client.handler;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.network.NetworkEvent;

import com.forgeessentials.commons.network.packets.Packet07Remote;
import com.mojang.blaze3d.platform.NativeImage;

public class Packet07RemoteHandler extends Packet07Remote
{
    Packet07RemoteHandler(String linkg)
    {
        super(linkg);
    }

    public static Packet07RemoteHandler decode(FriendlyByteBuf buf)
    {
        return new Packet07RemoteHandler(buf.readUtf());
    }

    @Override
    public void handle(NetworkEvent.Context context)
    {
        Minecraft instance = Minecraft.getInstance();
        try
        {
            BufferedImage img = ImageIO.read(new URL(link));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            DynamicTexture qrCodeTexture = new DynamicTexture(NativeImage.read(is));
            Packet07RemoteQRRenderer.qrCode = instance.getTextureManager()
                    .register("qr_code", qrCodeTexture);

            TextComponent qrLink = new TextComponent("[QR code]");
            ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, link);
            qrLink.withStyle((style) -> style.withClickEvent(click));
            qrLink.withStyle(ChatFormatting.RED);
            qrLink.withStyle(ChatFormatting.UNDERLINE);
            TextComponent msg = new TextComponent("Click in-game with mouse to close qrCode");
            qrLink.append(msg);
            instance.player.sendSystemMessage(qrLink);
        }
        catch (IOException e)
        {
            TextComponent cmsg = new TextComponent("Could not load QR Code. " + e.getMessage());
            cmsg.withStyle(ChatFormatting.RED);
            instance.player.sendSystemMessage(cmsg);
            e.printStackTrace();
        }
    }
}
