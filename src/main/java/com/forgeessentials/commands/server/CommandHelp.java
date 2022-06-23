package com.forgeessentials.commands.server;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.minecraft.command.CommandException;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.compat.HelpFixer;
import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.scripting.ScriptArguments;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandHelp extends ParserCommandBase
{

    private static String[] messages;

    private HelpFixer fixer;

    public CommandHelp()
    {
        fixer = new HelpFixer();
        // CONFIG ForgeEssentials.getConfigManager().registerLoader(ForgeEssentials.getConfigManager().getMainConfigName(), this);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "help";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "?" };
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/help <page|text>: List or search for commands";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.commands.help";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            if (arguments.isTabCompletion)
                return;
            showHelpPage(arguments.server, arguments.sender);
        }
        else
        {
            String name = arguments.remove().toLowerCase();
            try
            {
                int page = Integer.parseInt(name);
                if (arguments.isTabCompletion)
                    return;
                showHelpPage(arguments.server, arguments.sender, page);
            }
            catch (NumberFormatException e)
            {
                if (arguments.isTabCompletion)
                {
                    arguments.tabCompletion = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().getTabCompletions(arguments.sender,
                            name, BlockPos.ORIGIN);
                    return;
                }

                ICommand command = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().getCommands().get(name);

                SortedSet<ICommand> results = new TreeSet<>(new Comparator<ICommand>() {
                    @Override
                    public int compare(ICommand a, ICommand b)
                    {
                        return a.getName().compareTo(b.getName());
                    }
                });
                Set<Map.Entry<String, ICommand>> commands = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().getCommands()
                        .entrySet();
                for (Entry<String, ICommand> cmd : commands)
                {
                    String usage = cmd.getValue().getUsage(arguments.sender);
                    if (cmd.getKey().toLowerCase().contains(name) || (usage != null && usage.contains(name)))
                        results.add(cmd.getValue());
                }

                TextFormatting color = ChatOutputHandler.chatConfirmationColor;
                if (results.size() > 1 || command == null)
                    arguments.confirm("Searching commands by \"%s\"", name);

                if (command != null)
                {
                    sendCommandUsageMessage(arguments.sender, command, color);
                    results.remove(command);
                    color = TextFormatting.GRAY;
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

    public void sendCommandUsageMessage(ICommandSender sender, ICommand command, TextFormatting color)
    {
        ITextComponent chatMsg = new TranslationTextComponent(command.getUsage(sender));
        chatMsg.getStyle().withColor(color);
        chatMsg.getStyle().withClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/" + command.getName() + " "));
        ChatOutputHandler.sendMessage(sender, chatMsg);
    }

    public void showHelpPage(MinecraftServer server, ICommandSender sender) throws CommandException
    {
        if (messages.length == 0)
            showHelpPage(server, sender, 1);
        for (int i = 0; i < messages.length; i++)
            ChatOutputHandler.chatConfirmation(sender, ScriptArguments.processSafe(messages[i], sender));
    }

    public void showHelpPage(MinecraftServer server, ICommandSender sender, int page) throws CommandException
    {
        fixer.execute(server, sender, new String[] { Integer.toString(page) });
    }

    protected List<ICommand> getSortedPossibleCommands(ICommandSender sender, MinecraftServer server)
    {
        return fixer.getSortedPossibleCommands(sender, server);
    }

    static ForgeConfigSpec.ConfigValue<String[]> FEmessages;

    public static void load(ForgeConfigSpec.Builder SERVER_BUILDER)
    {
        SERVER_BUILDER.comment("Configure ForgeEssentials Core.").push(FEConfig.CONFIG_MAIN_CORE);
        FEmessages = SERVER_BUILDER.comment("Add custom messages here that will appear when /help is run")
                .define("custom_help", new String[] {});
        SERVER_BUILDER.pop();
    }

    public static void bakeConfig(boolean reload)
    {
        messages = FEmessages.get();
    }
}
