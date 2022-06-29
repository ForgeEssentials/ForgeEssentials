/**
 * Copy of
 * https://github.com/matthewprenger/HelpFixer/blob/master/src/main/java/com/matthewprenger/helpfixer/HelpFixer.java
 * for FE specifically.
 */
package com.forgeessentials.compat;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.command.CommandException;
import net.minecraft.command.impl.HelpCommand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

public class HelpFixer extends HelpCommand
{

    public static boolean hideWorldEditCommands = true;

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
    }

}
