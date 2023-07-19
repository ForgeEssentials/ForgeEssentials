package com.forgeessentials.client.handler;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import com.forgeessentials.commons.network.packets.Packet07Remote;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet07RemoteHandler extends Packet07Remote
{
    Packet07RemoteHandler(String linkg)
    {
        super(linkg);
    }

    public static Packet07RemoteHandler decode(PacketBuffer buf)
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

            TextComponent qrLink = new StringTextComponent("[QR code]");
            ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, link);
            qrLink.withStyle((style) -> {
                return style.withClickEvent(click);
            });
            qrLink.withStyle(TextFormatting.RED);
            qrLink.withStyle(TextFormatting.UNDERLINE);
            TextComponent msg = new StringTextComponent("Click in-game with mouse to close qrCode");
            qrLink.append(msg);
            instance.player.sendMessage(qrLink, instance.player.getGameProfile().getId());
        }
        catch (IOException e)
        {
            TextComponent cmsg = new StringTextComponent("Could not load QR Code. " + e.getMessage());
            cmsg.withStyle(TextFormatting.RED);
            instance.player.sendMessage(cmsg, instance.player.getGameProfile().getId());
            e.printStackTrace();
        }
    }
}
