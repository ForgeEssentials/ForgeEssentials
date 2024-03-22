package com.forgeessentials.commands.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.ChatFormatting;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandRules extends ForgeEssentialsCommandBuilder
{

    public CommandRules(boolean enabled)
    {
        super(enabled);
    }

    public static final String[] autocomargs = { "add", "remove", "move", "change", "book" };
    public static ArrayList<String> rules = new ArrayList<>();
    public static File rulesFile = new File(ForgeEssentials.getFEDirectory(), "rules.txt");

    public static ArrayList<String> loadRules()
    {
        ArrayList<String> rules = new ArrayList<>();

        if (!rulesFile.exists())
        {
            LoggingHandler.felog.info("No rules file found. Generating with default rules..");
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(rulesFile.toPath()))))
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
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(rulesFile.toPath()))))
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
    public @NotNull String getPrimaryAlias()
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
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(ModuleCommands.PERM + ".rules.edit", DefaultPermissionLevel.OP, "Edit rules");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.literal("help").executes(CommandContext -> execute(CommandContext, "help")))
                .then(Commands.literal("page")
                        .then(Commands.argument("page", IntegerArgumentType.integer(1, rules.size()))
                                .executes(CommandContext -> execute(CommandContext, "page"))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("page", IntegerArgumentType.integer(1, rules.size()))
                                .executes(CommandContext -> execute(CommandContext, "remove"))))
                .then(Commands.literal("move")
                        .then(Commands.argument("page1", IntegerArgumentType.integer(1, rules.size()))
                                .then(Commands.argument("page2", IntegerArgumentType.integer(1, Integer.MAX_VALUE))
                                        .executes(CommandContext -> execute(CommandContext, "move")))))
                .then(Commands.literal("add").then(Commands.argument("newRule", StringArgumentType.greedyString()))
                        .executes(CommandContext -> execute(CommandContext, "add")))
                .then(Commands.literal("change")
                        .then(Commands.argument("page", IntegerArgumentType.integer(1, rules.size()))
                                .then(Commands.argument("rule", StringArgumentType.greedyString())
                                        .executes(CommandContext -> execute(CommandContext, "change")))))
                .then(Commands.literal("book").executes(CommandContext -> execute(CommandContext, "book")))
                .executes(CommandContext -> execute(CommandContext, "blank"));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        System.out.println("Found root node");
        ServerPlayer Splayer = getServerPlayer(ctx.getSource());
        switch (params) {
            case "blank":
                for (String rule : rules) {
                    ChatOutputHandler.chatNotification(ctx.getSource(), rule);
                }
                return Command.SINGLE_SUCCESS;
            case "book":
                ListTag pages = new ListTag();
                ItemStack is = new ItemStack(Items.WRITTEN_BOOK);

                HashMap<String, String> map = new HashMap<>();

                for (int i = 0; i < rules.size(); i++) {
                    map.put(ChatFormatting.UNDERLINE + "Rule #" + (i + 1) + "\n\n",
                            ChatFormatting.RESET + ChatOutputHandler.formatColors(rules.get(i)));
                }

                SortedSet<String> keys = new TreeSet<>(map.keySet());
                for (String name : keys) {
                    pages.add(StringTag.valueOf(name + map.get(name)));
                }

                is.addTagElement("author", StringTag.valueOf("ForgeEssentials"));
                is.addTagElement("title", StringTag.valueOf("Rule Book"));

                is.addTagElement("pages", pages);
                Splayer.getInventory().add(is);
                return Command.SINGLE_SUCCESS;
            case "help":
                ChatOutputHandler.chatNotification(ctx.getSource(), " - /rules [#]");
                if (hasPermission(Splayer.createCommandSourceStack(), ModuleCommands.PERM + ".rules.edit")) {
                    ChatOutputHandler.chatNotification(ctx.getSource(), " - /rules <#> [changedRule]");
                    ChatOutputHandler.chatNotification(ctx.getSource(), " - /rules add <newRule>");
                    ChatOutputHandler.chatNotification(ctx.getSource(), " - /rules remove <#>");
                    ChatOutputHandler.chatNotification(ctx.getSource(), " - /rules move <#> <#>");
                }
                return Command.SINGLE_SUCCESS;
            case "page":
                ChatOutputHandler.chatNotification(ctx.getSource(),
                        rules.get(parseInt(IntegerArgumentType.getInteger(ctx, "page"), 1, rules.size()) - 1));
                return Command.SINGLE_SUCCESS;
        }

        if (!hasPermission(ctx.getSource(), ModuleCommands.PERM + ".rules.edit"))
        {
            ChatOutputHandler.chatError(ctx.getSource(),
                    "You have insufficient permissions to do that. If you believe you received this message in error, please talk to a server admin.");
            return Command.SINGLE_SUCCESS;
        }

        int index;

        switch (params) {
            case "remove":
                index = parseInt(IntegerArgumentType.getInteger(ctx, "page"), 1, rules.size());

                rules.remove(index - 1);
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Rule # %s removed", IntegerArgumentType.getInteger(ctx, "page")));
                break;
            case "add": {
                String newRule = StringArgumentType.getString(ctx, "newRule");
                newRule = ChatOutputHandler.formatColors(newRule);
                rules.add(newRule);
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Rule added as # %s.", StringArgumentType.getString(ctx, "newRule")));
                break;
            }
            case "move":
                index = parseInt(IntegerArgumentType.getInteger(ctx, "page1"), 1, rules.size());

                String temp = rules.remove(index - 1);

                index = parseInt(IntegerArgumentType.getInteger(ctx, "page2"), 1, Integer.MAX_VALUE);
                if (index < rules.size()) {
                    rules.add(index - 1, temp);
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Rule # %1$s moved to # %2$s",
                            IntegerArgumentType.getInteger(ctx, "page1"), IntegerArgumentType.getInteger(ctx, "page2")));
                } else {
                    rules.add(temp);
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator
                            .format("Rule # %1$s moved to last position.", IntegerArgumentType.getInteger(ctx, "page1")));
                }
                break;
            case "change": {
                index = parseInt(IntegerArgumentType.getInteger(ctx, "page"), 1, rules.size());

                String newRule = StringArgumentType.getString(ctx, "rule");
                newRule = ChatOutputHandler.formatColors(newRule);
                rules.set(index - 1, newRule);
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Rules # %1$s changed to '%2$s'.", index + "", newRule));
                break;
            }
        }
        saveRules();
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("blank"))
        {
            for (String rule : rules)
            {
                ChatOutputHandler.sendMessage(ctx.getSource(), rule);
            }
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), " - /rules [#]");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), " - /rules &lt;#> [changedRule]");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), " - /rules add &lt;newRule>");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), " - /rules remove &lt;#>");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), " - /rules move &lt;#> &lt;#>");

        }
        if (params.equals("page"))
        {
            ChatOutputHandler.sendMessage(ctx.getSource(),
                    rules.get(parseInt(IntegerArgumentType.getInteger(ctx, "page"), 1, rules.size()) - 1));
            return Command.SINGLE_SUCCESS;
        }
        int index;

        switch (params) {
            case "remove":
                index = parseInt(IntegerArgumentType.getInteger(ctx, "page"), 1, rules.size());

                rules.remove(index - 1);
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Rule # %s removed", IntegerArgumentType.getInteger(ctx, "page")));
                break;
            case "add": {
                String newRule = StringArgumentType.getString(ctx, "newRule");
                newRule = ChatOutputHandler.formatColors(newRule);
                rules.add(newRule);
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Rule added as # %s.", StringArgumentType.getString(ctx, "newRule")));
                break;
            }
            case "move":
                index = parseInt(IntegerArgumentType.getInteger(ctx, "page1"), 1, rules.size());

                String temp = rules.remove(index - 1);

                index = parseInt(IntegerArgumentType.getInteger(ctx, "page2"), 1, Integer.MAX_VALUE);
                if (index < rules.size()) {
                    rules.add(index - 1, temp);
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Rule # %1$s moved to # %2$s",
                            IntegerArgumentType.getInteger(ctx, "page1"), IntegerArgumentType.getInteger(ctx, "page2")));
                } else {
                    rules.add(temp);
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator
                            .format("Rule # %1$s moved to last position.", IntegerArgumentType.getInteger(ctx, "page1")));
                }
                break;
            case "change": {
                index = parseInt(IntegerArgumentType.getInteger(ctx, "page"), 1, rules.size());

                String newRule = StringArgumentType.getString(ctx, "rule");
                newRule = ChatOutputHandler.formatColors(newRule);
                rules.set(index - 1, newRule);
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Rules # %1$s changed to '%2$s'.", index + "", newRule));
                break;
            }
        }
        saveRules();
        return Command.SINGLE_SUCCESS;
    }
}
