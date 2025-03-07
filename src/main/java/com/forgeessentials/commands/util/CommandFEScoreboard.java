package com.forgeessentials.commands.util;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.command.server.CommandScoreboard;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import com.forgeessentials.multiworld.AnimatedScoreObjective;

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
        if (args.length > 1
                && args[0].equalsIgnoreCase("objectives")
                && args[1].equalsIgnoreCase("edit"))
        {
            if (args.length < 5)
            {
                throw new WrongUsageException("commands.scoreboard.objectives.edit.usage", new Object[0]);
            }
            editObjective(sender, args, 2);
        }
        else
        {
            super.processCommand(sender, args);
        }
    }

    protected void editObjective(ICommandSender sender, String[] args, int index) throws CommandException
    {
        String option = args[index++].toLowerCase();
        String name = args[index++];
        String value = args[index++];
        Scoreboard scoreboard = getScoreboard();
        ScoreObjective objective = scoreboard.getObjective(name);
        switch (option)
        {
        case "displayname":
            objective.setDisplayName(value);
            break;
        case "criteria":
            IScoreObjectiveCriteria iscoreobjectivecriteria = (IScoreObjectiveCriteria) IScoreObjectiveCriteria.INSTANCES.get(value);
            String displayName = objective.getDisplayName();
            if (objective instanceof AnimatedScoreObjective)
            {
                displayName = ((AnimatedScoreObjective) objective)._GetDisplayName();
            }

            scoreboard.removeObjective(objective);
            objective = scoreboard.addScoreObjective(name, iscoreobjectivecriteria);
            if (!displayName.equalsIgnoreCase(name))
            {
                objective.setDisplayName(displayName);
            }
            break;
        case "animate":
            if (objective instanceof AnimatedScoreObjective)
            {
                ((AnimatedScoreObjective) objective).toggleAnimate();
            }
            else
            {
                throw new WrongUsageException("commands.scoreboard.objectives.edit.usage", new Object[0]);
            }
            break;
        default:
            throw new WrongUsageException("commands.scoreboard.objectives.edit.usage", new Object[0]);
        }
        notifyOperators(sender, this, "commands.scoreboard.objectives.edit.success", new Object[] { option, name, value });
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length > 1 && args[0].equalsIgnoreCase("objectives"))
        {
            if (args.length == 2)
            {
                return getListOfStringsMatchingLastWord(args, new String[] { "list", "add", "edit", "remove", "setdisplay" });
            }
            else if (args.length == 3 && args[1].equalsIgnoreCase("edit"))
            {
                return getListOfStringsMatchingLastWord(args, new String[] { "displayname", "criteria", "animate" });
            }
        }
        return addTabCompletionOptions(sender, args, pos);
    }

    protected void addObjective(ICommandSender sender, String[] args, int index) throws CommandException
    {
        super.addObjective(sender, args, index);
    }
}
