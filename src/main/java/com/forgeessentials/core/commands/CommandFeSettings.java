package com.forgeessentials.core.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RootZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.registration.FECommandManager.ConfigurableCommand;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandFeSettings extends ForgeEssentialsCommandBuilder implements ConfigurableCommand
{

    public CommandFeSettings(boolean enabled)
    {
        super(enabled);
        instance = this;
    }

    public static final String CONFIG_FILE = "Settings";

    public static Map<String, String> aliases = new HashMap<>();

    public static Map<String, String> printMap = new TreeMap<>();
    private Properties props = new Properties() {

        private static final long serialVersionUID = 1L;

        @Override
        public synchronized Enumeration<Object> keys()
        {

            return Collections.enumeration(new TreeSet<>(super.keySet()));
        }

    };

    private static CommandFeSettings instance;

    public static CommandFeSettings getInstance()
    {
        return instance;
    }

    public static void addSetting(String category, String alias, String permissionNode)
    {
        aliases.put((category + "." + alias).toLowerCase(), permissionNode);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "fesettings";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
    {
        return new String[] { "feconfig" };
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("set")
                        .then(Commands.argument("perm", StringArgumentType.string()).suggests(SUGGEST_SETTINGS)
                                .then(Commands.argument("type", StringArgumentType.string()).suggests(SUGGEST_VALUE)
                                        .executes(CommandContext -> execute(CommandContext, "settingSET")))))
                .then(Commands.literal("get")
                        .then(Commands.argument("perm", StringArgumentType.string()).suggests(SUGGEST_SETTINGS)
                                .executes(CommandContext -> execute(CommandContext, "settingGET"))))
                .then(Commands.literal("help").executes(CommandContext -> execute(CommandContext, "help")))
                .executes(CommandContext -> execute(CommandContext, "help"));
    }

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_SETTINGS = (ctx, builder) -> {
        List<String> listArgs = new ArrayList<>(aliases.keySet());
        return SharedSuggestionProvider.suggest(listArgs, builder);
    };
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_VALUE = (ctx, builder) -> {
        List<String> listArgs = new ArrayList<>();
        listArgs.add(Zone.PERMISSION_TRUE);
        listArgs.add(Zone.PERMISSION_FALSE);
        return SharedSuggestionProvider.suggest(listArgs, builder);
    };

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(),
                    "Available settings: " + StringUtils.join(aliases.keySet(), ", "));
            return Command.SINGLE_SUCCESS;
        }
        String key = StringArgumentType.getString(ctx, "perm");
        String perm = aliases.get(key);
        if (perm == null)
        {
            ChatOutputHandler.chatWarning(ctx.getSource(), "Unknown FE setting %s", key);
            return Command.SINGLE_SUCCESS;
        }

        if (params.equals("settingGET"))
        {
            String rootValue = APIRegistry.perms.getServerZone().getRootZone().getGroupPermission(Zone.GROUP_DEFAULT,
                    perm);
            String globalValue = APIRegistry.perms.getServerZone().getGroupPermission(Zone.GROUP_DEFAULT, perm);
            if (globalValue != null && !globalValue.equals(rootValue))
                ChatOutputHandler.chatWarning(ctx.getSource(), "%s = %s, but global permission value is set to %s", key,
                        rootValue, globalValue);
            else
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "%s = %s", key, rootValue);
            return Command.SINGLE_SUCCESS;
        }

        String value = StringArgumentType.getString(ctx, "type");

        // String[] aliasParts = key.split("\\.", 2);
        // config.get(aliasParts[0], aliasParts[1], "").set(value);
        printMap.put(key, value);
        saveConfig();

        APIRegistry.perms.registerPermissionProperty(perm, value);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Changed setting \"%s\" to \"%s\"", key, value);

        return Command.SINGLE_SUCCESS;
    }

    public void loadSettings()
    {
        File configFile = new File(ForgeEssentials.getFEDirectory() + "/Settings.cfg");
        try
        {
            FileReader reader = new FileReader(configFile);
            props.load(reader);
            for (String key : props.stringPropertyNames())
            {
                String value = props.getProperty(key);
                printMap.put(key, value);
            }
            reader.close();
        }
        catch (FileNotFoundException ex)
        {
            // file does not exist
        }
        catch (IOException ex)
        {
            // I/O error
        }
        RootZone root = APIRegistry.perms.getServerZone().getRootZone();
        for (Entry<String, String> setting : aliases.entrySet())
        {
            String defaultValue = root.getGroupPermission(Zone.GROUP_DEFAULT, setting.getValue());
            if (defaultValue == null)
                defaultValue = "";
            String desc = APIRegistry.perms.getPermissionDescription(setting.getValue());
            if (desc != null)
            {
                APIRegistry.perms.registerPermissionProperty(setting.getValue(), defaultValue, desc);
            }
            else
            {
                APIRegistry.perms.registerPermissionProperty(setting.getValue(), defaultValue);
            }

            String value = "";
            if (printMap.containsKey(setting.getKey()))
            {
                value = printMap.get(setting.getKey());
            }
            else
            {
                printMap.put(setting.getKey(), defaultValue);
                value = defaultValue;
            }

            if (!value.isEmpty())
                APIRegistry.perms.registerPermissionProperty(setting.getValue(), value);
        }
        saveConfig();
    }

    private void saveConfig()
    {
        File configFile = new File(ForgeEssentials.getFEDirectory() + "/Settings.cfg");

        try
        {
            for (Map.Entry<String, String> entry : printMap.entrySet())
            {
                props.setProperty(entry.getKey(), entry.getValue());
                // String help = String.format("%s = %s\n%s", setting.getValue(), defaultValue,
                // desc);
                // String[] aliasParts = setting.getKey().split("\\.", 2);
                // config.get(aliasParts[0], aliasParts[1], "").set(value);
            }
            FileWriter writer = new FileWriter(configFile);
            props.store(writer, "Tweek default settings here.");
            writer.close();
        }
        catch (FileNotFoundException ex)
        {
            // file does not exist
        }
        catch (IOException ex)
        {
            // I/O error
        }
        catch (NullPointerException ex)
        {
            // Empty settings Map
        }
    }

	@Override
	public void loadData() {
		loadSettings();
	}
}
