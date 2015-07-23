/**
 * Copy of
 * https://github.com/matthewprenger/HelpFixer/blob/master/src/main/java/com/matthewprenger/helpfixer/HelpFixer.java
 * for FE specifically.
 */
package com.forgeessentials.compat;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.command.CommandHelp;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class HelpFixer extends CommandHelp
{

    public static boolean hideWorldEditCommands = true;

    @Override
    @SuppressWarnings("unchecked")
    public List<ICommand> getSortedPossibleCommands(ICommandSender sender)
    {
        List<ICommand> list = MinecraftServer.getServer().getCommandManager().getPossibleCommands(sender);
        if (hideWorldEditCommands)
        {
            for (Iterator<ICommand> it = list.iterator(); it.hasNext();)
            {
                ICommand command = it.next();
                if (command.getClass().getName().startsWith("com.sk89q.worldedit"))
                    it.remove();
            }
        }
        Collections.sort(list);
        return list;
    }

}
