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

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

public class CommandRules extends FEcmdModuleCommands implements ConfigurableCommand
{

    public static final String[] autocomargs = { "add", "remove", "move", "change", "book" };
    public static ArrayList<String> rules;
    public static File rulesFile = new File(ForgeEssentials.getFEDirectory(), "rules.txt");

    @Override
    public void loadConfig(Configuration config, String category)
    {
        rulesFile = new File(ForgeEssentials.getFEDirectory(), config.get(category, "filename", "rules.txt").getString());
        rules = loadRules();
    }

    public ArrayList<String> loadRules()
    {
        ArrayList<String> rules = new ArrayList<String>();

        OutputHandler.felog.info("Loading rules");
        if (!rulesFile.exists())
        {
            try
            {
                OutputHandler.felog.info("No rules file found. Generating with default rules..");

                rulesFile.createNewFile();

                // create streams
                FileOutputStream stream = new FileOutputStream(rulesFile);
                OutputStreamWriter streamWriter = new OutputStreamWriter(stream);
                BufferedWriter writer = new BufferedWriter(streamWriter);

                writer.write("# " + rulesFile.getName() + " | numbers are automatically added");
                writer.newLine();

                writer.write("Obey the Admins");
                rules.add("Obey the Admins");
                writer.newLine();

                writer.write("Do not grief");
                rules.add("Do not grief");
                writer.newLine();

                writer.close();
                streamWriter.close();
                stream.close();

                OutputHandler.felog.info("Completed generating rules file.");
            }
            catch (IOException e)
            {
                OutputHandler.felog.severe("Error writing the Rules file: " + rulesFile.getName());
            }
        }
        else
        {
            try
            {
                OutputHandler.felog.info("Rules file found. Reading...");

                FileInputStream stream = new FileInputStream(rulesFile);
                InputStreamReader streamReader = new InputStreamReader(stream);
                BufferedReader reader = new BufferedReader(streamReader);

                String read = reader.readLine();
                int counter = 0;

                while (read != null)
                {
                    // ignore the comment things...
                    if (read.startsWith("#"))
                    {
                        read = reader.readLine();
                        continue;
                    }

                    // add to the rules list.
                    rules.add(read);

                    // read the next string
                    read = reader.readLine();

                    // increment counter
                    counter++;
                }

                reader.close();
                streamReader.close();
                stream.close();

                OutputHandler.felog.info("Completed reading rules file. " + counter + " rules read.");
            }
            catch (IOException e)
            {
                OutputHandler.felog.severe("Error writing the Rules file: " + rulesFile.getName());
            }
        }

        return rules;
    }

    public void saveRules()
    {
        try
        {
            OutputHandler.felog.info("Saving rules");

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

            OutputHandler.felog.info("Completed saving rules file.");
        }
        catch (IOException e)
        {
            OutputHandler.felog.severe("Error writing the Rules file: " + rulesFile.getName());
        }
    }

    @Override
    public String getCommandName()
    {
        return "rules";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (args.length == 0)
        {
            for (String rule : rules)
            {
                OutputHandler.chatNotification(sender, rule);
            }
            return;
        }
        else if (args[0].equalsIgnoreCase("book"))
        {
            NBTTagCompound tag = new NBTTagCompound();
            NBTTagList pages = new NBTTagList();

            HashMap<String, String> map = new HashMap<String, String>();

            for (int i = 0; i < rules.size(); i++)
            {
                map.put(EnumChatFormatting.UNDERLINE + "Rule #" + (i + 1) + "\n\n", EnumChatFormatting.RESET + FunctionHelper.formatColors(rules.get(i)));
            }

            SortedSet<String> keys = new TreeSet<String>(map.keySet());
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
                OutputHandler.chatNotification(sender, " - /rules [#]");
                if (PermissionsManager.checkPermission(sender, getPermissionNode() + ".edit"))
                {
                    OutputHandler.chatNotification(sender, " - /rules &lt;#> [changedRule]");
                    OutputHandler.chatNotification(sender, " - /rules add &lt;newRule>");
                    OutputHandler.chatNotification(sender, " - /rules remove &lt;#>");
                    OutputHandler.chatNotification(sender, " - /rules move &lt;#> &lt;#>");
                }
                return;
            }

            OutputHandler.chatNotification(sender, rules.get(parseIntBounded(sender, args[0], 1, rules.size()) - 1));
            return;
        }

        if (!PermissionsManager.checkPermission(sender, getPermissionNode() + ".edit"))
            throw new TranslatedCommandException(
                    "You have insufficient permissions to do that. If you believe you received this message in error, please talk to a server admin.");

        int index;

        if (args[0].equalsIgnoreCase("remove"))
        {
            index = parseIntBounded(sender, args[1], 1, rules.size());

            rules.remove(index - 1);
            OutputHandler.chatConfirmation(sender, Translator.format("Rule # %s removed", args[1]));
        }
        else if (args[0].equalsIgnoreCase("add"))
        {
            String newRule = "";
            for (int i = 1; i < args.length; i++)
            {
                newRule = newRule + args[i] + " ";
            }
            newRule = FunctionHelper.formatColors(newRule);
            rules.add(newRule);
            OutputHandler.chatConfirmation(sender, Translator.format("Rule added as # %s.", args[1]));
        }
        else if (args[0].equalsIgnoreCase("move"))
        {
            index = parseIntBounded(sender, args[1], 1, rules.size());

            String temp = rules.remove(index - 1);

            index = parseIntWithMin(sender, args[2], 1);

            if (index < rules.size())
            {
                rules.add(index - 1, temp);
                OutputHandler.chatConfirmation(sender, Translator.format("Rule # %1$s moved to # %2$s", args[1], args[2]));
            }
            else
            {
                rules.add(temp);
                OutputHandler.chatConfirmation(sender, Translator.format("Rule # %1$s moved to last position.", args[1]));
            }
        }
        else if (args[0].equalsIgnoreCase("change"))
        {
            index = parseIntBounded(sender, args[1], 1, rules.size());

            String newRule = "";
            for (int i = 2; i < args.length; i++)
            {
                newRule = newRule + args[i] + " ";
            }
            newRule = FunctionHelper.formatColors(newRule);
            rules.set(index - 1, newRule);
            OutputHandler.chatConfirmation(sender, Translator.format("Rules # %1$s changed to '%2$s'.", index + "", newRule));
        }
        else
            throw new TranslatedCommandException(getCommandUsage(sender));
        saveRules();
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length == 0)
        {
            for (String rule : rules)
            {
                OutputHandler.sendMessage(sender, rule);
            }
            return;
        }
        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("help"))
            {
                OutputHandler.chatConfirmation(sender, " - /rules [#]");
                OutputHandler.chatConfirmation(sender, " - /rules &lt;#> [changedRule]");
                OutputHandler.chatConfirmation(sender, " - /rules add &lt;newRule>");
                OutputHandler.chatConfirmation(sender, " - /rules remove &lt;#>");
                OutputHandler.chatConfirmation(sender, " - /rules move &lt;#> &lt;#>");

            }

            OutputHandler.sendMessage(sender, rules.get(parseIntBounded(sender, args[0], 1, rules.size()) - 1));
            return;
        }

        int index;

        if (args[0].equalsIgnoreCase("remove"))
        {
            index = parseIntBounded(sender, args[1], 1, rules.size());

            rules.remove(index - 1);
            OutputHandler.chatConfirmation(sender, Translator.format("Rule # %s removed", args[1]));
        }
        else if (args[0].equalsIgnoreCase("add"))
        {
            String newRule = "";
            for (int i = 1; i < args.length; i++)
            {
                newRule = newRule + args[i] + " ";
            }
            newRule = FunctionHelper.formatColors(newRule);
            rules.add(newRule);
            OutputHandler.chatConfirmation(sender, Translator.format("Rule added as # %s.", args[1]));
        }
        else if (args[0].equalsIgnoreCase("move"))
        {
            index = parseIntBounded(sender, args[1], 1, rules.size());

            String temp = rules.remove(index - 1);

            index = parseIntWithMin(sender, args[2], 1);

            if (index < rules.size())
            {
                rules.add(index - 1, temp);
                OutputHandler.chatConfirmation(sender, Translator.format("Rule # %1$s moved to # %2$s", args[1], args[2]));
            }
            else
            {
                rules.add(temp);
                OutputHandler.chatConfirmation(sender, Translator.format("Rule # %1$s moved to last position.", args[1]));
            }
        }
        else if (args[0].equalsIgnoreCase("change"))
        {
            index = parseIntBounded(sender, args[1], 1, rules.size());

            String newRule = "";
            for (int i = 2; i < args.length; i++)
            {
                newRule = newRule + args[i] + " ";
            }
            newRule = FunctionHelper.formatColors(newRule);
            rules.set(index - 1, newRule);
            OutputHandler.chatConfirmation(sender, Translator.format("Rules # %1$s changed to '%2$s'.", index + "", newRule));
        }
        else
        {
            throw new TranslatedCommandException(getCommandUsage(sender));
        }
        saveRules();
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".edit", RegisteredPermValue.OP);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, autocomargs);
        }
        else if (args.length == 2)
        {
            List<String> opt = new ArrayList<String>();
            for (int i = 1; i < rules.size() + 1; i++)
            {
                opt.add(i + "");
            }
            return opt;
        }
        else if (args.length == 3 && args[0].equalsIgnoreCase("move"))
        {
            List<String> opt = new ArrayList<String>();
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
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
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

}
