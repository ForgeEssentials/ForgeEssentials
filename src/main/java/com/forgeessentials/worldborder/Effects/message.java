package com.forgeessentials.worldborder.Effects;

import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.worldborder.WorldBorder;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;

public class message implements IEffect {
    private String message = "You passed the world border!";

    @Override
    public void registerConfig(Configuration config, String category)
    {
        message = config.get(category, "Message", message, "Message to send to the player. You can use color codes.").getString();
    }

    @Override
    public void execute(WorldBorder wb, EntityPlayerMP player)
    {
        OutputHandler.sendMessage(player, FunctionHelper.formatColors(message));
    }
}
