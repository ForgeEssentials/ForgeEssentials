package com.forgeessentials.client.handler;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import com.forgeessentials.client.ForgeEssentialsClient;
import com.forgeessentials.commons.network.packets.Packet7Remote;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkEvent;

public class Packet7RemoteHandler extends Packet7Remote {
	Packet7RemoteHandler(String linkg) {
		super(linkg);
	}

	public static Packet7RemoteHandler decode(PacketBuffer buf) {
		return new Packet7RemoteHandler(buf.readUtf());
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		try {
			BufferedImage img = ImageIO.read(new URL(link));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, "png", baos);
			InputStream is = new ByteArrayInputStream(baos.toByteArray());
			DynamicTexture qrCodeTexture = new DynamicTexture(NativeImage.read(is));
			ForgeEssentialsClient.qrCodeRenderer.qrCode = Minecraft.getInstance().getTextureManager()
					.register("qr_code", qrCodeTexture);
		} catch (IOException e) {
			TextComponent cmsg = new StringTextComponent("Could not load QR Code. " + e.getMessage());
			cmsg.withStyle(TextFormatting.RED);
			Minecraft instance = Minecraft.getInstance();
			instance.player.sendMessage(cmsg, instance.player.getUUID());
			e.printStackTrace();
		}
	}
}
