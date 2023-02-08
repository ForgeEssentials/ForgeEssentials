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
import java.util.SortedSet;
import java.util.TreeSet;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandRules extends BaseCommand implements ConfigurableCommand
{

    public CommandRules(String name, int permissionLevel, boolean enabled)
    {
        super(enabled);
    }

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
    public String getPrimaryAlias()
    {
        return "rules";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".rules";
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(getPermissionNode() + ".edit", DefaultPermissionLevel.OP, "Edit rules");
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.literal("help")
                        .executes(CommandContext -> execute(CommandContext, "help")
                                )
                        )
                .then(Commands.literal("page")
                        .then(Commands.argument("page", IntegerArgumentType.integer(1, rules.size()))
                                .executes(CommandContext -> execute(CommandContext, "page")
                                        )
                                )
                        )
                .then(Commands.literal("remove")
                        .then(Commands.argument("page", IntegerArgumentType.integer(1, rules.size()))
                                .executes(CommandContext -> execute(CommandContext, "remove")
                                        )
                                )
                        )
                .then(Commands.literal("move")
                        .then(Commands.argument("page1", IntegerArgumentType.integer(1, rules.size()))
                                .then(Commands.argument("page2", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                                        .executes(CommandContext -> execute(CommandContext, "move")
                                                )
                                        )
                                )
                        )
                .then(Commands.literal("add")
                        .then(Commands.argument("rule", MessageArgument.message()))
                                .executes(CommandContext -> execute(CommandContext, "add")
                                )
                        )
                .then(Commands.literal("change")
                        .then(Commands.argument("page", IntegerArgumentType.integer(1, rules.size()))
                                .then(Commands.argument("rule", MessageArgument.message())
                                        .executes(CommandContext -> execute(CommandContext, "change")
                                                )
                                        )
                                )
                        )
                .then(Commands.literal("book")
                        .executes(CommandContext -> execute(CommandContext, "book")
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "blank")
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        ServerPlayerEntity Splayer = getServerPlayer(ctx.getSource());
        if (params.toString() == "blank")
        {
            for (String rule : rules)
            {
                ChatOutputHandler.chatNotification(ctx.getSource(), rule);
            }
            return Command.SINGLE_SUCCESS;
        }
        else if (params.toString() == "book")
        {
            ListNBT pages = new ListNBT();
            ItemStack is = new ItemStack(Items.WRITTEN_BOOK);

            HashMap<String, String> map = new HashMap<>();

            for (int i = 0; i < rules.size(); i++)
            {
                map.put(TextFormatting.UNDERLINE + "Rule #" + (i + 1) + "\n\n", TextFormatting.RESET + ChatOutputHandler.formatColors(rules.get(i)));
            }

            SortedSet<String> keys = new TreeSet<>(map.keySet());
            for (String name : keys)
            {
                pages.add(StringNBT.valueOf(name + map.get(name)));
            }

            is.addTagElement("author", StringNBT.valueOf("ForgeEssentials"));
            is.addTagElement("title", StringNBT.valueOf("Rule Book"));

            
            is.addTagElement("pages", pages);
            Splayer.inventory.add(is);
            return Command.SINGLE_SUCCESS;
        }
        else if (params.toString() == "help")
        {
            ChatOutputHandler.chatNotification(ctx.getSource(), " - /rules [#]");
            if (PermissionAPI.hasPermission(Splayer, getPermissionNode() + ".edit"))
            {
                ChatOutputHandler.chatNotification(ctx.getSource(), " - /rules &lt;#> [changedRule]");
                ChatOutputHandler.chatNotification(ctx.getSource(), " - /rules add &lt;newRule>");
                ChatOutputHandler.chatNotification(ctx.getSource(), " - /rules remove &lt;#>");
                ChatOutputHandler.chatNotification(ctx.getSource(), " - /rules move &lt;#> &lt;#>");
            }
            return Command.SINGLE_SUCCESS;
        }
        else if (params.toString() == "page")
        {
            ChatOutputHandler.chatNotification(ctx.getSource(), rules.get(parseInt(IntegerArgumentType.getInteger(ctx, "page"), 1, rules.size()) - 1));
            return Command.SINGLE_SUCCESS;
        }

        if (!PermissionAPI.hasPermission(ctx.getSource().getPlayerOrException(), getPermissionNode() + ".edit"))
            throw new TranslatedCommandException(
                    "You have insufficient permissions to do that. If you believe you received this message in error, please talk to a server admin.");

        int index;

        if (params.toString() == "remove")
        {
            index = parseInt(IntegerArgumentType.getInteger(ctx, "page"), 1, rules.size());

            rules.remove(index - 1);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Rule # %s removed", IntegerArgumentType.getInteger(ctx, "page")));
        }
        else if (params.toString() == "add")
        {
            String newRule = MessageArgument.getMessage(ctx, "rule").getContents();
            newRule = ChatOutputHandler.formatColors(newRule);
            rules.add(newRule);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Rule added as # %s.", MessageArgument.getMessage(ctx, "rule").getContents()));
        }
        else if (params.toString() == "move")
        {
            index = parseInt(IntegerArgumentType.getInteger(ctx, "page1"), 1, rules.size());

            String temp = rules.remove(index - 1);

            index = parseInt(IntegerArgumentType.getInteger(ctx, "page2"), 1, Integer.MAX_VALUE);
            if (index < rules.size())
            {
                rules.add(index - 1, temp);
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Rule # %1$s moved to # %2$s", IntegerArgumentType.getInteger(ctx, "page1"), IntegerArgumentType.getInteger(ctx, "page2")));
            }
            else
            {
                rules.add(temp);
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Rule # %1$s moved to last position.", IntegerArgumentType.getInteger(ctx, "page1")));
            }
        }
        else if (params.toString() == "change")
        {
            index = parseInt(IntegerArgumentType.getInteger(ctx, "page"), 1, rules.size());

            String newRule = MessageArgument.getMessage(ctx, "rule").getContents();
            newRule = ChatOutputHandler.formatColors(newRule);
            rules.set(index - 1, newRule);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Rules # %1$s changed to '%2$s'.", index + "", newRule));
        }
        saveRules();
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params.toString() == "blank")
        {
            for (String rule : rules)
            {
                ChatOutputHandler.sendMessage(ctx.getSource(), rule);
            }
            return Command.SINGLE_SUCCESS;
        }
        if (params.toString() == "help")
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), " - /rules [#]");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), " - /rules &lt;#> [changedRule]");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), " - /rules add &lt;newRule>");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), " - /rules remove &lt;#>");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), " - /rules move &lt;#> &lt;#>");

        }
        if (params.toString() == "page")
        {
            ChatOutputHandler.sendMessage(ctx.getSource(), rules.get(parseInt(IntegerArgumentType.getInteger(ctx, "page"), 1, rules.size()) - 1));
            return Command.SINGLE_SUCCESS;
        }
        int index;

        if (params.toString() ==  "remove")
        {
            index = parseInt(IntegerArgumentType.getInteger(ctx, "page"), 1, rules.size());

            rules.remove(index - 1);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Rule # %s removed", IntegerArgumentType.getInteger(ctx, "page")));
        }
        else if (params.toString() == "add")
        {
            String newRule = MessageArgument.getMessage(ctx, "rule").getContents();
            newRule = ChatOutputHandler.formatColors(newRule);
            rules.add(newRule);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Rule added as # %s.", MessageArgument.getMessage(ctx, "rule").getContents()));
        }
        else if (params.toString() == "move")
        {
            index = parseInt(IntegerArgumentType.getInteger(ctx, "page1"), 1, rules.size());

            String temp = rules.remove(index - 1);

            index = parseInt(IntegerArgumentType.getInteger(ctx, "page2"), 1, Integer.MAX_VALUE);
            if (index < rules.size())
            {
                rules.add(index - 1, temp);
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Rule # %1$s moved to # %2$s", IntegerArgumentType.getInteger(ctx, "page1"), IntegerArgumentType.getInteger(ctx, "page2")));
            }
            else
            {
                rules.add(temp);
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Rule # %1$s moved to last position.", IntegerArgumentType.getInteger(ctx, "page1")));
            }
        }
        else if (params.toString() == "change")
        {
            index = parseInt(IntegerArgumentType.getInteger(ctx, "page"), 1, rules.size());

            String newRule = MessageArgument.getMessage(ctx, "rule").getContents();
            newRule = ChatOutputHandler.formatColors(newRule);
            rules.set(index - 1, newRule);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Rules # %1$s changed to '%2$s'.", index + "", newRule));
        }
        saveRules();
        return Command.SINGLE_SUCCESS;
    }

    static ForgeConfigSpec.ConfigValue<String> name;
    @Override
    public void loadConfig(ForgeConfigSpec.Builder BUILDER, String category)
    {
    	BUILDER.push(category);
    	name = BUILDER.comment("Name for rules file").define("filename", "rules.txt");
    	BUILDER.pop();
    }

    @Override
    public void loadData()
    {
        /* do nothing */
    }

    @Override
    public void bakeConfig(boolean reload)
    {
    	rulesFile = new File(ForgeEssentials.getFEDirectory(), name.get());
    	rules = loadRules();
    }
}
