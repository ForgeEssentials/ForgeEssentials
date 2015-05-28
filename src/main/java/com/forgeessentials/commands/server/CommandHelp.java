package com.forgeessentials.commands.server;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.compat.HelpFixer;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader;
import com.forgeessentials.scripting.ScriptArguments;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.OutputHandler;

public class CommandHelp extends ParserCommandBase implements ConfigLoader
{

    private static final String CONFIG_HELP = "Add custom messages here that will appear when /help is run";

    private String[] messages;

    private HelpFixer fixer;

    public CommandHelp()
    {
        fixer = new HelpFixer();
        ForgeEssentials.getConfigManager().registerLoader(ForgeEssentials.getConfigManager().getMainConfigName(), this);
    }

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
                    arguments.notify(Translator.format("Searching commands by \"%s\"", name));

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
        if (messages.length == 0)
            showHelpPage(sender, 0);
        for (int i = 0; i < messages.length; i++)
            OutputHandler.chatConfirmation(sender, ScriptArguments.process(messages[i], sender));
    }

    public void showHelpPage(ICommandSender sender, int page)
    {
        fixer.processCommand(sender, new String[] { "" + page });
    }

    protected List<ICommand> getSortedPossibleCommands(ICommandSender sender)
    {
        return fixer.getSortedPossibleCommands(sender);
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        messages = config.get(ForgeEssentials.CONFIG_CAT, "custom_help", new String[] {}, CONFIG_HELP).getStringList();
    }

    @Override
    public void save(Configuration config)
    {
        /* do nothing */
    }

    @Override
    public boolean supportsCanonicalConfig()
    {
        return true;
    }

}
