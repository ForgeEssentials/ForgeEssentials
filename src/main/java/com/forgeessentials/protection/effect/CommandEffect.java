package com.forgeessentials.protection.effect;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.scripting.ScriptParser;
import com.forgeessentials.util.output.LoggingHandler;

public class CommandEffect extends ZoneEffect
{

    protected String command;

    public CommandEffect(EntityPlayerMP player, int interval, String command)
    {
        super(player, interval, false);
        this.command = command;
    }

    @Override
    public void execute()
    {
        try
        {
            ScriptParser.run(command, player, null);
        }
        catch (CommandException e)
        {
            LoggingHandler.felog.error(String.format("Error executing zone command: %s", e.getMessage()));
        }
    }

}
