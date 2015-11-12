package com.forgeessentials.worldborder.effect;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.scripting.ScriptArguments;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

public class EffectCommand extends WorldBorderEffect
{

    public String command = "/say @player Go back while you still can!";

    public long interval = 0;

    @Override
    public void activate(WorldBorder border, EntityPlayerMP player)
    {
        if (interval <= 0)
            doEffect(player);
    }

    @Override
    public void tick(WorldBorder border, EntityPlayerMP player)
    {
        if (interval <= 0)
            return;
        PlayerInfo pi = PlayerInfo.get(player);
        if (pi.checkTimeout(this.getClass().getName()))
        {
            doEffect(player);
            pi.startTimeout(this.getClass().getName(), interval);
        }
    }

    public void doEffect(EntityPlayerMP player)
    {
        String cmd = ScriptArguments.processSafe(command, player);
        MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), cmd);
    }
}
