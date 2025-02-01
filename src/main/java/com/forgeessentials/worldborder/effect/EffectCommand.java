package com.forgeessentials.worldborder.effect;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.scripting.ScriptArguments;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.PlayerInfo;
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
    public void provideArguments(CommandParserArgs args) throws CommandException
    {
        if (args.isEmpty())
            throw new TranslatedCommandException("Missing interval argument");
        interval = args.parseInt();
        if (args.isEmpty())
            throw new TranslatedCommandException("Missing command argument");
        command = args.toString();
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
        MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), cmd);
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
