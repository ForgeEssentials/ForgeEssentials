package com.forgeessentials.core.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permission.PermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RootZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader;
import com.forgeessentials.util.CommandParserArgs;

public class CommandFeSettings extends ParserCommandBase implements ConfigLoader
{

    public static final String CONFIG_FILE = "Settings";

    public static Map<String, String> aliases = new HashMap<>();

    public static Map<String, String> values = new HashMap<>();

    private Configuration config;

    private static CommandFeSettings instache;

    public CommandFeSettings()
    {
        instache = this;
        APIRegistry.getFEEventBus().register(this);
        ForgeEssentials.getConfigManager().registerLoader(CONFIG_FILE, this);
    }

    public static CommandFeSettings getInstance()
    {
        return instache;
    }

    public static void addAlias(String category, String alias, String permission)
    {
        aliases.put((category + "." + alias).toLowerCase(), permission);
    }

    @Override
    public String getCommandName()
    {
        return "fesettings";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "feconfig" };
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/fesettings [id] [value]: Change FE settings";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.settings";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("Available settings: " + StringUtils.join(aliases.keySet(), ", "));
            return;
        }

        arguments.tabComplete(aliases.keySet());
        String key = arguments.remove().toLowerCase();
        String perm = aliases.get(key);
        if (perm == null)
            throw new TranslatedCommandException("Unknown FE setting %s", key);

        if (arguments.isEmpty())
        {
            String rootValue = APIRegistry.perms.getServerZone().getRootZone().getGroupPermission(Zone.GROUP_DEFAULT, perm);
            String globalValue = APIRegistry.perms.getServerZone().getGroupPermission(Zone.GROUP_DEFAULT, perm);
            if (globalValue != null && !globalValue.equals(rootValue))
                arguments.warn(Translator.format("%s = %s, but global permission value is set to %s", key, rootValue, globalValue));
            else
                arguments.confirm("%s = %s", key, rootValue);
            return;
        }

        arguments.tabComplete(Zone.PERMISSION_TRUE, Zone.PERMISSION_FALSE);
        String value = arguments.remove();
        if (arguments.isTabCompletion)
            return;

        String[] aliasParts = key.split("\\.", 2);
        config.get(aliasParts[0], aliasParts[1], "").set(value);
        config.save();

        APIRegistry.perms.registerPermissionProperty(perm, value);
        arguments.confirm("Changed setting \"%s\" to \"%s\"", key, value);
    }

    public void loadSettings()
    {
        if (config == null)
            return;
        RootZone root = APIRegistry.perms.getServerZone().getRootZone();
        for (Entry<String, String> setting : aliases.entrySet())
        {
            String defaultValue = root.getGroupPermission(Zone.GROUP_DEFAULT, setting.getValue());
            if (defaultValue == null)
                defaultValue = "";
            String desc = APIRegistry.perms.getPermissionDescription(setting.getValue());
            String help = String.format("%s = %s\n%s", setting.getValue(), defaultValue, desc);
            String[] aliasParts = setting.getKey().split("\\.", 2);
            String value = config.get(aliasParts[0], aliasParts[1], "", help).getString();
            if (!value.isEmpty())
                APIRegistry.perms.registerPermissionProperty(setting.getValue(), value);
        }
        config.save();
    }

    @Override
    public void load(Configuration config, boolean isReload)
    {
        this.config = config;
        if (isReload)
            loadSettings();
    }

    @Override
    public boolean supportsCanonicalConfig()
    {
        return false;
    }

}
