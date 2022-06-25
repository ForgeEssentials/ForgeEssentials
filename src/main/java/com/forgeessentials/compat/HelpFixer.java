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

    /**
     * Fix for retard mods who think they can just return null in {@link ICommand#getUsage(ICommandSender)}
     * 
     * @throws CommandException
     */
    @Override
    @SuppressWarnings("unchecked")
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        List<ICommand> commands = getSortedPossibleCommands(sender, server);
        byte cmdsPerPage = 7;
        int i = (commands.size() - 1) / cmdsPerPage;

        int startPage;
        try
        {
            startPage = args.length == 0 ? 0 : parseInt(args[0], 1, i + 1) - 1;
        }
        catch (NumberInvalidException e)
        {
            Map<String, ICommand> cmdMap = getCommandMap(server);
            ICommand cmd = cmdMap.get(args[0]);
            if (cmd != null)
            {
                String usage = cmd.getUsage(sender);
                if (usage == null)
                    usage = "/" + cmd.getName();
                throw new WrongUsageException(usage, new Object[0]);
            }
            else if (MathHelper.getInt(args[0], -1) != -1)
            {
                throw e;
            }
            throw new CommandNotFoundException();
        }

        int endIndex = Math.min((startPage + 1) * cmdsPerPage, commands.size());
        TranslationTextComponent msg = new TranslationTextComponent("commands.help.header", new Object[] { Integer.valueOf(startPage + 1),
                Integer.valueOf(i + 1) });
        msg.getStyle().withColor(TextFormatting.DARK_GREEN);
        sender.sendMessage(msg);

        for (int index = startPage * cmdsPerPage; index < endIndex; ++index)
        {
            ICommand cmd = commands.get(index);
            String usage = cmd.getUsage(sender);
            if (usage == null)
                usage = "/" + cmd.getName();
            TranslationTextComponent msg2 = new TranslationTextComponent(usage, new Object[0]);
            msg2.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + cmd.getName() + " "));
            sender.sendMessage(msg2);
        }

        if (startPage == 0 && sender instanceof PlayerEntity)
        {
            TranslationTextComponent msg3 = new TranslationTextComponent("commands.help.footer", new Object[0]);
            msg3.getStyle().withColor(TextFormatting.GREEN);
            sender.sendMessage(msg3);
        }
    }

}
