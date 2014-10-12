package com.forgeessentials.worldborder.Effects;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.worldborder.WorldBorder;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class serverkick implements IEffect {
    private String message = "You passed the world border!";

    @Override
    public void registerConfig(Configuration config, String category)
    {
        message = config.get(category, "Message", message, "Message to send to the player on the kick screen.").getString();
    }

    @Override
    public void execute(WorldBorder wb, EntityPlayerMP player)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
        {
            player.playerNetServerHandler.kickPlayerFromServer(message);
        }
        else
        {
            OutputHandler.chatNotification(player, "You should have been kicked from the server with this message:");
            OutputHandler.sendMessage(player, message);
            OutputHandler.chatNotification(player, "Since this is SSP, thats not possible.");
        }
    }
}
