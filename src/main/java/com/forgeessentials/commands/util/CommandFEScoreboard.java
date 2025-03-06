package com.forgeessentials.commands.util;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandScoreboard;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.world.World;

public class CommandFEScoreboard extends CommandScoreboard
{
    @Override public String getCommandName()
    {
        return "fescoreboard";
    }

    World currentWorld = null;

    @Override protected Scoreboard getScoreboard()
    {
        if (currentWorld != null)
        {
            return currentWorld.getScoreboard();
        }
        else
        {
            return super.getScoreboard();
        }
    }

    @Override public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        currentWorld = sender.getEntityWorld();
        super.processCommand(sender, args);
    }

    protected void addObjective(ICommandSender sender, String[] args, int index) throws CommandException
    {
        super.addObjective(sender, args, index);
    }
}
