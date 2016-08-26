package com.forgeessentials.worldborder.effect;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.scripting.ScriptArguments;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

/**
 * Expected syntax: <interval> <command>
 */
public class EffectCommand extends WorldBorderEffect
{

    public String command = "say @player Go back while you still can!";

    public int interval = 0;

    @Override
    public boolean provideArguments(String[] args)
    {
        if (args.length < 2)
            return false;
        interval = Integer.parseInt(args[0]);
        command = StringUtils.join(ServerUtil.dropFirst(args), " ");
        return true;
    }

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
            pi.startTimeout(this.getClass().getName(), interval * 1000);
        }
    }

    public void doEffect(EntityPlayerMP player)
    {
        String cmd = ScriptArguments.processSafe(command, player);
        FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(FMLCommonHandler.instance().getMinecraftServerInstance(), cmd);
    }

    public String toString()
    {
        return "command trigger: " + triggerDistance + "interval: " + interval + " command: " + command;
    }

    public String getSyntax()
    {
        return "<interval> <command>";
    }
}
