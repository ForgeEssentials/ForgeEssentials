/**
 * Copy of
 * https://github.com/matthewprenger/HelpFixer/blob/master/src/main/java/com/matthewprenger/helpfixer/HelpFixer.java
 * for FE specifically.
 */
package com.forgeessentials.core.commands;

import net.minecraft.command.CommandHelp;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HelpFixer extends CommandHelp{

    @SuppressWarnings("unchecked")
    @Override
    protected List<ICommand> getSortedPossibleCommands(ICommandSender sender) {
        List<ICommand> list = MinecraftServer.getServer().getCommandManager().getPossibleCommands(sender);
        Collections.sort(list, new Comparator<ICommand>() {

            @Override
            public int compare(ICommand o1, ICommand o2)
            {
                return o1.getCommandName().compareTo(o2.getCommandName());
            }
        });
        return list;
    }

    @Override
    public int compareTo(Object o)
    {
        if (o instanceof ICommand)
        {
            return this.compareTo((ICommand) o);
        }
        else
        {
            return 0;
        }
    }
}
