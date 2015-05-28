package com.forgeessentials.commands.server;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.OutputHandler;

public class CommandHelp extends ParserCommandBase
{

    private boolean hasCustomHelpPage = false;

    @Override
    public String getCommandName()
    {
        return "help";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/help <page|text>: List or search for commands";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.commands.help";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void parse(CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            if (arguments.isTabCompletion)
                return;
            showHelpPage(arguments.sender);
        }
        else
        {
            String name = arguments.remove().toLowerCase();
            try
            {
                int page = Integer.parseInt(name);
                if (arguments.isTabCompletion)
                    return;
                showHelpPage(arguments.sender, page);
            }
            catch (NumberFormatException e)
            {
                if (arguments.isTabCompletion)
                {
                    arguments.tabCompletion = MinecraftServer.getServer().getCommandManager().getPossibleCommands(arguments.sender, name);
                    return;
                }

                ICommand command = (ICommand) MinecraftServer.getServer().getCommandManager().getCommands().get(name);

                SortedSet<ICommand> results = new TreeSet<ICommand>(new Comparator<ICommand>() {
                    @Override
                    public int compare(ICommand a, ICommand b)
                    {
                        return a.getCommandName().compareTo(b.getCommandName());
                    }
                });
                Set<Map.Entry<String, ICommand>> commands = MinecraftServer.getServer().getCommandManager().getCommands().entrySet();
                for (Entry<String, ICommand> cmd : commands)
                {
                    String usage = cmd.getValue().getCommandUsage(arguments.sender);
                    if (cmd.getKey().toLowerCase().contains(name) || (usage != null && usage.contains(name)))
                        results.add(cmd.getValue());
                }

                EnumChatFormatting color = OutputHandler.chatConfirmationColor;
                if (results.size() > 1 || command == null)
                    arguments.notify(Translator.format("Searching commandy by \"%s\"", name));

                if (command != null)
                {
                    sendCommandUsageMessage(arguments.sender, command, color);
                    results.remove(command);
                    color = EnumChatFormatting.GRAY;
                }

                int count = command == null ? 0 : 1;
                for (ICommand cmd : results)
                {
                    if (++count > 7)
                    {
                        arguments.notify("...too many search results");
                        break;
                    }
                    sendCommandUsageMessage(arguments.sender, cmd, color);
                }
            }
        }
    }

    public void sendCommandUsageMessage(ICommandSender sender, ICommand command, EnumChatFormatting color)
    {
        IChatComponent chatMsg = new ChatComponentTranslation(command.getCommandUsage(sender));
        chatMsg.getChatStyle().setColor(color);
        chatMsg.getChatStyle().setChatClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/" + command.getCommandName()));
        sender.addChatMessage(chatMsg);
    }

    public void showHelpPage(ICommandSender sender)
    {
        if (!hasCustomHelpPage)
            showHelpPage(sender, 0);

        // TODO Add custom help page
    }

    public void showHelpPage(ICommandSender sender, int page)
    {
        List<ICommand> list = getSortedPossibleCommands(sender);
        byte b0 = 7;
        int i = (list.size() - 1) / b0;
        page = Math.max(0, Math.min(page, i));

        int j = Math.min((page + 1) * b0, list.size());
        ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation("commands.help.header", new Object[] { Integer.valueOf(page + 1),
                Integer.valueOf(i + 1) });
        chatcomponenttranslation1.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
        sender.addChatMessage(chatcomponenttranslation1);

        for (int l = page * b0; l < j; ++l)
        {
            ICommand icommand1 = list.get(l);
            ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation(icommand1.getCommandUsage(sender), new Object[0]);
            chatcomponenttranslation.getChatStyle()
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + icommand1.getCommandName() + " "));
            sender.addChatMessage(chatcomponenttranslation);
        }

        if (page == 0 && sender instanceof EntityPlayer)
        {
            ChatComponentTranslation chatcomponenttranslation2 = new ChatComponentTranslation("commands.help.footer", new Object[0]);
            chatcomponenttranslation2.getChatStyle().setColor(EnumChatFormatting.GREEN);
            sender.addChatMessage(chatcomponenttranslation2);
        }
    }

    protected List<ICommand> getSortedPossibleCommands(ICommandSender sender)
    {
        @SuppressWarnings("unchecked")
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

}
