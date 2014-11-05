package com.forgeessentials.commands.shortcut;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;

/**
 * This system allows someone to make shortcut commands.
 * Example:
 * "/fly 'true|false'" ==> "/capabilities $sender allowflying 'true|false'"
 *
 * @author Dries007
 */
public class ShortcutCommands {

    private static final String ARGS_COMMENT =
            "The argumtens to be send to the command. Use double quotes.\n" +
                    "Usable variables:\n" +
                    "$sender => senders username\n" +
                    "$arg => the next argument provided to the shortcut. If the argument is not provided, the proper syntax will be shown. Don't use these after using $oArg\n"
                    +
                    "$oArg => the next argument, but optional. If there is no argument, doesn't do anything.";

    static ArrayList<CommandWrapper> list = new ArrayList<CommandWrapper>();

    static HashMap<String, CommandWrapper> cmdMap = new HashMap<String, CommandWrapper>();

    static Configuration config;

    /**
     * Registers all commands
     *
     * @param e
     */
    public static void load()
    {
        CommandHandler ch = (CommandHandler) MinecraftServer.getServer().getCommandManager();
        for (CommandWrapper cmd : list)
        {
            ch.registerCommand(cmd);
        }
    }

    /**
     * load config
     *
     * @param folder
     */
    public static void loadConfig(File folder)
    {
        File confFile = new File(folder, "CommandShortcuts.cfg");
        config = new Configuration(confFile);
        parseConfig();
    }

    public static void parseConfig()
    {
        if (config == null)
        {
            return;
        }

        config.load();
        ArrayList<String> names = new ArrayList<String>();
        String main = "shortcuts";

        String general = main + ".general";
        config.addCustomCategoryComment(general, "Add a name to the 'list' list and use 'fereload' to genarate a new config category.  Separate by new line without commas.");
        names.addAll(
                Arrays.asList(config.get(general, "list", new String[] { "fly" }, "Add names here and reload to add templates you can edit.").getStringList()));

        // remove unused config categorys
        ArrayList<String> cats = new ArrayList<String>(config.getCategoryNames());

        cats.remove(main);
        cats.remove(general);

        for (String name : names)
        {
            String category = main + "." + name;
            cats.remove(category);

            String command = config.get(category, "command", "capabilities", "Basic command you want to forward to. No syntax here.").getString();
            String[] args = config.get(category, "args", new String[] { "\"$sender\"", "\"allowflying\"", "\"$arg\"" }, ARGS_COMMENT).getStringList();
            String syntax = config.get(category, "syntax", "\"<true|false>\"", "The syntax for this shortcut. Use double quotes.").getString();

            list.add(new CommandWrapper(command, name, args, syntax));
        }

        for (String cat : cats)
        {
            config.removeCategory(config.getCategory(cat));
        }

        config.save();
    }

    /**
     * This method infuses the wrapperArgs (from config) with the args send when the command was used.
     *
     * @param sender
     * @param wrapperArgs
     * @param sendArgs
     * @return
     */
    public static String[] parseArgs(ICommandSender sender, String[] wrapperArgs, String[] sendArgs) throws CommandException
    {
        ArrayList<String> output = new ArrayList<String>();
        int i = 0;
        for (String arg : wrapperArgs)
        {
            if (arg.startsWith("\"") && arg.endsWith("\""))
            {
                arg = arg.substring(1, arg.length() - 1);
            }

            if (arg.equalsIgnoreCase("$sender"))
            {
                arg = sender.getCommandSenderName();
            }
            else if (arg.equalsIgnoreCase("$arg"))
            {
                if (sendArgs.length > i)
                {
                    arg = sendArgs[i];
                    i++;
                }
                else
                {
                    throw new CommandException("commands.generic.syntax");
                }
            }
            else if (arg.equalsIgnoreCase("$oArg"))
            {
                if (sendArgs.length > i)
                {
                    arg = sendArgs[i];
                    i++;
                }
            }
            output.add(arg);
        }

        if (sendArgs.length <= i)
        {
            for (int i2 = i; i < sendArgs.length; i2++)
            {
                output.add(sendArgs[i2]);
            }
        }

        return output.toArray(new String[output.size()]);
    }
}
