package com.forgeessentials.commands.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

public class CommandRules extends ForgeEssentialsCommandBase implements ConfigurableCommand
{

    public static final String[] autocomargs = { "add", "remove", "move", "change", "book" };
    public static ArrayList<String> rules;
    public static File rulesFile = new File(ForgeEssentials.getFEDirectory(), "rules.txt");

    public ArrayList<String> loadRules()
    {
        ArrayList<String> rules = new ArrayList<>();

        if (!rulesFile.exists())
        {
            LoggingHandler.felog.info("No rules file found. Generating with default rules..");
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rulesFile))))
            {
                writer.write("# " + rulesFile.getName() + " | numbers are automatically added");
                writer.newLine();

                writer.write("Obey the Admins");
                rules.add("Obey the Admins");
                writer.newLine();

                writer.write("Do not grief");
                rules.add("Do not grief");
                writer.newLine();

                LoggingHandler.felog.info("Completed generating rules file.");
            }
            catch (IOException e)
            {
                LoggingHandler.felog.error("Error writing the Rules file: " + rulesFile.getName());
            }
        }
        else
        {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(rulesFile))))
            {
                String line;
                while ((line = reader.readLine()) != null)
                {
                    if (line.startsWith("#"))
                        continue;
                    rules.add(line);
                }
            }
            catch (IOException e)
            {
                LoggingHandler.felog.error("Error writing the Rules file: " + rulesFile.getName());
            }
        }

        return rules;
    }

    public void saveRules()
    {
        try
        {
            LoggingHandler.felog.info("Saving rules");

            if (!rulesFile.exists())
            {
                rulesFile.createNewFile();
            }

            // create streams
            FileOutputStream stream = new FileOutputStream(rulesFile);
            OutputStreamWriter streamWriter = new OutputStreamWriter(stream);
            BufferedWriter writer = new BufferedWriter(streamWriter);

            writer.write("# " + rulesFile.getName() + " | numbers are automatically added");
            writer.newLine();

            for (String rule : rules)
            {
                writer.write(rule);
                writer.newLine();
            }

            writer.close();
            streamWriter.close();
            stream.close();

            LoggingHandler.felog.info("Completed saving rules file.");
        }
        catch (IOException e)
        {
            LoggingHandler.felog.error("Error writing the Rules file: " + rulesFile.getName());
        }
    }

    @Override
    public String getCommandName()
    {
        return "ferules";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "rules" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        // Needs elaboration.
        if (sender instanceof EntityPlayer)
        {
            return "/rules [#|add|remove|move|change|help|book] Gets or sets the rules of the server.";
        }
        else
        {
            return "/rules [#|add|remove|move|change|help] Gets or sets the rules of the server.";
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".rules";
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".edit", PermissionLevel.OP);
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException
    {
        if (args.length == 0)
        {
            for (String rule : rules)
            {
                ChatOutputHandler.chatNotification(sender, rule);
            }
            return;
        }
        else if (args[0].equalsIgnoreCase("book"))
        {
            NBTTagCompound tag = new NBTTagCompound();
            NBTTagList pages = new NBTTagList();

            HashMap<String, String> map = new HashMap<>();

            for (int i = 0; i < rules.size(); i++)
            {
                map.put(EnumChatFormatting.UNDERLINE + "Rule #" + (i + 1) + "\n\n", EnumChatFormatting.RESET + ChatOutputHandler.formatColors(rules.get(i)));
            }

            SortedSet<String> keys = new TreeSet<>(map.keySet());
            for (String name : keys)
            {
                pages.appendTag(new NBTTagString(name + map.get(name)));
            }

            tag.setString("author", "ForgeEssentials");
            tag.setString("title", "Rule Book");
            tag.setTag("pages", pages);

            ItemStack is = new ItemStack(Items.written_book);
            is.setTagCompound(tag);
            sender.inventory.addItemStackToInventory(is);
            return;
        }
        else if (args.length == 1)
        {

            if (args[0].equalsIgnoreCase("help"))
            {
                ChatOutputHandler.chatNotification(sender, " - /rules [#]");
                if (PermissionManager.checkPermission(sender, getPermissionNode() + ".edit"))
                {
                    ChatOutputHandler.chatNotification(sender, " - /rules &lt;#> [changedRule]");
                    ChatOutputHandler.chatNotification(sender, " - /rules add &lt;newRule>");
                    ChatOutputHandler.chatNotification(sender, " - /rules remove &lt;#>");
                    ChatOutputHandler.chatNotification(sender, " - /rules move &lt;#> &lt;#>");
                }
                return;
            }

            ChatOutputHandler.chatNotification(sender, rules.get(parseInt(args[0], 1, rules.size()) - 1));
            return;
        }

        if (!PermissionManager.checkPermission(sender, getPermissionNode() + ".edit"))
            throw new TranslatedCommandException(
                    "You have insufficient permissions to do that. If you believe you received this message in error, please talk to a server admin.");

        int index;

        if (args[0].equalsIgnoreCase("remove"))
        {
            index = parseInt(args[1], 1, rules.size());

            rules.remove(index - 1);
            ChatOutputHandler.chatConfirmation(sender, Translator.format("Rule # %s removed", args[1]));
        }
        else if (args[0].equalsIgnoreCase("add"))
        {
            String newRule = "";
            for (int i = 1; i < args.length; i++)
            {
                newRule = newRule + args[i] + " ";
            }
            newRule = ChatOutputHandler.formatColors(newRule);
            rules.add(newRule);
            ChatOutputHandler.chatConfirmation(sender, Translator.format("Rule added as # %s.", args[1]));
        }
        else if (args[0].equalsIgnoreCase("move"))
        {
            index = parseInt(args[1], 1, rules.size());

            String temp = rules.remove(index - 1);

            index = parseInt(args[2], 1);

            if (index < rules.size())
            {
                rules.add(index - 1, temp);
                ChatOutputHandler.chatConfirmation(sender, Translator.format("Rule # %1$s moved to # %2$s", args[1], args[2]));
            }
            else
            {
                rules.add(temp);
                ChatOutputHandler.chatConfirmation(sender, Translator.format("Rule # %1$s moved to last position.", args[1]));
            }
        }
        else if (args[0].equalsIgnoreCase("change"))
        {
            index = parseInt(args[1], 1, rules.size());

            String newRule = "";
            for (int i = 2; i < args.length; i++)
            {
                newRule = newRule + args[i] + " ";
            }
            newRule = ChatOutputHandler.formatColors(newRule);
            rules.set(index - 1, newRule);
            ChatOutputHandler.chatConfirmation(sender, Translator.format("Rules # %1$s changed to '%2$s'.", index + "", newRule));
        }
        else
            throw new TranslatedCommandException(getCommandUsage(sender));
        saveRules();
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 0)
        {
            for (String rule : rules)
            {
                ChatOutputHandler.sendMessage(sender, rule);
            }
            return;
        }
        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("help"))
            {
                ChatOutputHandler.chatConfirmation(sender, " - /rules [#]");
                ChatOutputHandler.chatConfirmation(sender, " - /rules &lt;#> [changedRule]");
                ChatOutputHandler.chatConfirmation(sender, " - /rules add &lt;newRule>");
                ChatOutputHandler.chatConfirmation(sender, " - /rules remove &lt;#>");
                ChatOutputHandler.chatConfirmation(sender, " - /rules move &lt;#> &lt;#>");

            }

            ChatOutputHandler.sendMessage(sender, rules.get(parseInt(args[0], 1, rules.size()) - 1));
            return;
        }

        int index;

        if (args[0].equalsIgnoreCase("remove"))
        {
            index = parseInt(args[1], 1, rules.size());

            rules.remove(index - 1);
            ChatOutputHandler.chatConfirmation(sender, Translator.format("Rule # %s removed", args[1]));
        }
        else if (args[0].equalsIgnoreCase("add"))
        {
            String newRule = "";
            for (int i = 1; i < args.length; i++)
            {
                newRule = newRule + args[i] + " ";
            }
            newRule = ChatOutputHandler.formatColors(newRule);
            rules.add(newRule);
            ChatOutputHandler.chatConfirmation(sender, Translator.format("Rule added as # %s.", args[1]));
        }
        else if (args[0].equalsIgnoreCase("move"))
        {
            index = parseInt(args[1], 1, rules.size());

            String temp = rules.remove(index - 1);

            index = parseInt(args[2], 1);

            if (index < rules.size())
            {
                rules.add(index - 1, temp);
                ChatOutputHandler.chatConfirmation(sender, Translator.format("Rule # %1$s moved to # %2$s", args[1], args[2]));
            }
            else
            {
                rules.add(temp);
                ChatOutputHandler.chatConfirmation(sender, Translator.format("Rule # %1$s moved to last position.", args[1]));
            }
        }
        else if (args[0].equalsIgnoreCase("change"))
        {
            index = parseInt(args[1], 1, rules.size());

            String newRule = "";
            for (int i = 2; i < args.length; i++)
            {
                newRule = newRule + args[i] + " ";
            }
            newRule = ChatOutputHandler.formatColors(newRule);
            rules.set(index - 1, newRule);
            ChatOutputHandler.chatConfirmation(sender, Translator.format("Rules # %1$s changed to '%2$s'.", index + "", newRule));
        }
        else
        {
            throw new TranslatedCommandException(getCommandUsage(sender));
        }
        saveRules();
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, autocomargs);
        }
        else if (args.length == 2)
        {
            List<String> opt = new ArrayList<>();
            for (int i = 1; i < rules.size() + 1; i++)
            {
                opt.add(i + "");
            }
            return opt;
        }
        else if (args.length == 3 && args[0].equalsIgnoreCase("move"))
        {
            List<String> opt = new ArrayList<>();
            for (int i = 1; i < rules.size() + 2; i++)
            {
                opt.add(i + "");
            }
            return opt;
        }
        else
        {
            return null;
        }
    }

    @Override
    public void loadConfig(Configuration config, String category)
    {
        rulesFile = new File(ForgeEssentials.getFEDirectory(), config.get(category, "filename", "rules.txt").getString());
        rules = loadRules();
    }

    @Override
    public void loadData()
    {
        /* do nothing */
    }

}
