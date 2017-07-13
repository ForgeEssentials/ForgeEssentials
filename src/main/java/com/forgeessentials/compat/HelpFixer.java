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
import net.minecraft.command.CommandHelp;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

public class HelpFixer extends CommandHelp
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
        // Ok there is some retard who thinks he should implement ICommand instead of extending CommandBase and then
        // fails to properly implement compareTo (it always returns 0).
        // So to prevent crashes from these kind of things, we just provide our own comparator
        Collections.sort(list, new Comparator<ICommand>() {
            @Override
            public int compare(ICommand o1, ICommand o2)
            {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return list;
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
        TextComponentTranslation msg = new TextComponentTranslation("commands.help.header", new Object[] { Integer.valueOf(startPage + 1),
                Integer.valueOf(i + 1) });
        msg.getStyle().setColor(TextFormatting.DARK_GREEN);
        sender.sendMessage(msg);

        for (int index = startPage * cmdsPerPage; index < endIndex; ++index)
        {
            ICommand cmd = commands.get(index);
            String usage = cmd.getUsage(sender);
            if (usage == null)
                usage = "/" + cmd.getName();
            TextComponentTranslation msg2 = new TextComponentTranslation(usage, new Object[0]);
            msg2.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + cmd.getName() + " "));
            sender.sendMessage(msg2);
        }

        if (startPage == 0 && sender instanceof EntityPlayer)
        {
            TextComponentTranslation msg3 = new TextComponentTranslation("commands.help.footer", new Object[0]);
            msg3.getStyle().setColor(TextFormatting.GREEN);
            sender.sendMessage(msg3);
        }
    }

}
