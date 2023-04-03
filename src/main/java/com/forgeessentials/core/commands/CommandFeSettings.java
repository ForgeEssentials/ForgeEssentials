package com.forgeessentials.core.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.command.CommandSource;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RootZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandFeSettings extends ForgeEssentialsCommandBuilder //implements ConfigLoader
{

    public CommandFeSettings(boolean enabled)
    {
        super(enabled);
    }

    public static final String CONFIG_FILE = "Settings";

    public static Map<String, String> aliases = new HashMap<>();

    public static Map<String, String> values = new HashMap<>();

    private static CommandFeSettings instache;

    public static CommandFeSettings getInstance()
    {
        return instache;
    }

    public static void addAlias(String category, String alias, String permission)
    {
        aliases.put((category + "." + alias).toLowerCase(), permission);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "fesettings";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "feconfig" };
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.settings";
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .executes(CommandContext -> execute(CommandContext, "help")
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params.toString().equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Available settings: " + StringUtils.join(aliases.keySet(), ", "));
            return Command.SINGLE_SUCCESS;
        }
        /*
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
                ChatOutputHandler.chatWarning(ctx.getSource(), Translator.format("%s = %s, but global permission value is set to %s", key, rootValue, globalValue));
            else
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "%s = %s", key, rootValue);
            return Command.SINGLE_SUCCESS;
        }

        arguments.tabComplete(Zone.PERMISSION_TRUE, Zone.PERMISSION_FALSE);
        String value = arguments.remove();


        String[] aliasParts = key.split("\\.", 2);
        config.get(aliasParts[0], aliasParts[1], "").set(value);
        config.save();

        APIRegistry.perms.registerPermissionProperty(perm, value);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Changed setting \"%s\" to \"%s\"", key, value);
        */
        return Command.SINGLE_SUCCESS;
    }

    public void loadSettings()
    {
        RootZone root = APIRegistry.perms.getServerZone().getRootZone();
        for (Entry<String, String> setting : aliases.entrySet())
        {
            String defaultValue = root.getGroupPermission(Zone.GROUP_DEFAULT, setting.getValue());
            if (defaultValue == null)
                defaultValue = "";
            String desc = APIRegistry.perms.getPermissionDescription(setting.getValue());
            if(desc!=null) {
                APIRegistry.perms.registerPermissionProperty(setting.getValue(), defaultValue, desc);
            }
            else {
                APIRegistry.perms.registerPermissionProperty(setting.getValue(), defaultValue);
            }
            //String help = String.format("%s = %s\n%s", setting.getValue(), defaultValue, desc);
            //String[] aliasParts = setting.getKey().split("\\.", 2);
            //String value = config.get(aliasParts[0], aliasParts[1], "", help).getString();
            //if (!value.isEmpty())
                //APIRegistry.perms.registerPermissionProperty(setting.getValue(), defaultValue);
        }
        //config.save();
    }
/*
	@Override
	public void load(Builder BUILDER, boolean isReload)
    {
        //this.config = config;
        if (isReload)
            loadSettings();
    }


	@Override
	public void bakeConfig(boolean reload) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ConfigData returnData() {
		// TODO Auto-generated method stub
		return null;
	}
*/
}
