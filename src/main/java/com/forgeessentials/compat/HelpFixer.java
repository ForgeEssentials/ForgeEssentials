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
import net.minecraft.event.ClickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public class HelpFixer extends CommandHelp
{

    public static boolean hideWorldEditCommands = true;

    @Override
    public List<ICommand> getSortedPossibleCommands(ICommandSender sender)
    {
        List<ICommand> list = MinecraftServer.getServer().getCommandManager().getPossibleCommands(sender);
        if (hideWorldEditCommands)
        {
            for (Iterator<ICommand> it = list.iterator(); it.hasNext();)
            {
                ICommand command = it.next();
                if (command.getClass().getName().startsWith("com.sk89q.worldedit") && !command.getCommandName().equals("/help"))
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
                return o1.getCommandName().compareTo(o2.getCommandName());
            }
        });
        return list;
    }

    /**
     * Fix for retard mods who think they can just return null in {@link ICommand#getCommandUsage(ICommandSender)}
     */
    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        List<ICommand> commands = getSortedPossibleCommands(sender);
        byte cmdsPerPage = 7;
        int i = (commands.size() - 1) / cmdsPerPage;

        int startPage;
        try
        {
        	startPage = args.length == 0 ? 0 : parseInt(args[0], 1, i + 1) - 1;
        }
        catch (NumberInvalidException e)
        {
            Map<String, ICommand> cmdMap = getCommands();
            ICommand cmd = cmdMap.get(args[0]);
            if (cmd != null)
            {
                String usage = cmd.getCommandUsage(sender);
                if (usage == null)
                    usage = "/" + cmd.getCommandName();
                throw new WrongUsageException(usage, new Object[0]);
            }
            else if (MathHelper.parseIntWithDefault(args[0], -1) != -1)
            {
                throw e;
            }
            throw new CommandNotFoundException();
        }

        int endIndex = Math.min((startPage + 1) * cmdsPerPage, commands.size());
        ChatComponentTranslation msg = new ChatComponentTranslation("commands.help.header", new Object[] { Integer.valueOf(startPage + 1),
                Integer.valueOf(i + 1) });
        msg.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
        sender.addChatMessage(msg);

        for (int index = startPage * cmdsPerPage; index < endIndex; ++index)
        {
            ICommand cmd = commands.get(index);
            String usage = cmd.getCommandUsage(sender);
            if (usage == null)
                usage = "/" + cmd.getCommandName();
            ChatComponentTranslation msg2 = new ChatComponentTranslation(usage, new Object[0]);
            msg2.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + cmd.getCommandName() + " "));
            sender.addChatMessage(msg2);
        }

        if (startPage == 0 && sender instanceof EntityPlayer)
        {
            ChatComponentTranslation msg3 = new ChatComponentTranslation("commands.help.footer", new Object[0]);
            msg3.getChatStyle().setColor(EnumChatFormatting.GREEN);
            sender.addChatMessage(msg3);
        }
    }

}
