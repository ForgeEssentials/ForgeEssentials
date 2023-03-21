/**
 * Copy of
 * https://github.com/matthewprenger/HelpFixer/blob/master/src/main/java/com/matthewprenger/helpfixer/HelpFixer.java
 * for FE specifically.
 */
package com.forgeessentials.compat;

import net.minecraft.command.impl.HelpCommand;

public class HelpFixer extends HelpCommand
{

    public static boolean hideWorldEditCommands = true;
/*
    @Override
    @SuppressWarnings("unchecked")
    public List<ICommand> getSortedPossibleCommands(ICommandSender sender, MinecraftServer server)
    {
        List<ICommand> list = server.getCommandManager().getPossibleCommands(sender);
        if (hideWorldEditCommands)
        {
            for (Iterator<ICommand> it = list.iterator(); it.hasNext();)
            {
                ICommand command = it.next();
                if (command.getClass().getName().startsWith("com.sk89q.worldedit") && !command.getName().equals("/help"))
                    it.remove();
            }
        }
    }*/

}
