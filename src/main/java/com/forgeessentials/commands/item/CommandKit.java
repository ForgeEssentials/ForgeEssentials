package com.forgeessentials.commands.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commands.util.Kit;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.events.FEPlayerEvent.NoPlayerInfoEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;

/**
 * Kit command with cooldown. Should also put armor in armor slots.
 */

public class CommandKit extends ParserCommandBase implements ConfigurableCommand
{

    public static final String PERM = ModuleCommands.PERM + ".kit";
    public static final String PERM_ADMIN = ModuleCommands.PERM + ".admin";
    public static final String PERM_BYPASS_COOLDOWN = PERM + ".bypasscooldown";

    public static String kitForNewPlayers;

    public static Map<String, Kit> kits = new HashMap<>();

    public CommandKit()
    {
        APIRegistry.getFEEventBus().register(this);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "kit";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/kit [name] [set|del] [cooldown]: Use and modify item kits";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public String getPermissionNode()
    {
        return PERM;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(PERM_ADMIN, DefaultPermissionLevel.OP, "Administer kits");
        APIRegistry.perms.registerPermission(PERM_BYPASS_COOLDOWN, DefaultPermissionLevel.OP, "Bypass kit cooldown");
    }

    public List<String> getAvailableKits(CommandParserArgs arguments)
    {
        List<String> availableKits = new ArrayList<>();
        for (Kit kit : kits.values())
            if (arguments.hasPermission(PERM + "." + kit.getName()))
                availableKits.add(kit.getName());
        return availableKits;
    }

    @Override
    public void parse(final CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            if (!arguments.isTabCompletion)
                arguments.confirm("Available kits: %s", StringUtils.join(getAvailableKits(arguments), ", "));
            return;
        }

        arguments.tabComplete(getAvailableKits(arguments));
        final String kitName = arguments.remove().toLowerCase();
        Kit kit = kits.get(kitName);

        if (arguments.isEmpty())
        {
            if (kit == null)
                throw new TranslatedCommandException("Kit %s does not exist", kitName);
            if (!arguments.hasPermission(PERM + "." + kit.getName()))
                throw new TranslatedCommandException("You are not allowed to use this kit");
            kit.giveKit(arguments.senderPlayer);
            return;
        }

        arguments.checkPermission(PERM_ADMIN);

        arguments.tabComplete("set", "del");
        String subCommand = arguments.remove().toLowerCase();
        switch (subCommand)
        {
        case "set":
            QuestionerCallback callback = new QuestionerCallback() {
                @Override
                public void respond(Boolean response)
                {
                    if (response == null)
                        arguments.error("Question timed out");
                    else if (!response)
                        return;
                    int cooldown = -1;
                    if (!arguments.isEmpty())
                        try
                        {
                            cooldown = arguments.parseInt();
                        }
                        catch (CommandException e)
                        {
                            arguments.error(e.getMessage());
                        }
                    addKit(new Kit(arguments.senderPlayer, kitName, cooldown));
                    if (cooldown < 0)
                        arguments.confirm("Kit %s saved for one-time-use", kitName);
                    else
                        arguments.confirm("Kit %s saved with cooldown %s", kitName, ChatOutputHandler.formatTimeDurationReadable(cooldown, true));
                }
            };
            if (kit == null)
                callback.respond(true);
            else
                Questioner.addChecked(arguments.sender, Translator.format("Overwrite kit %s?", kitName), callback);
            break;
        case "del":
        case "delete":
            removeKit(kit);
            arguments.confirm("Deleted kit %s", kitName);
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCommand);
        }
    }

    @SubscribeEvent
    public void newPlayerEvent(NoPlayerInfoEvent event)
    {
        Kit kit = kits.get(kitForNewPlayers);
        if (kit != null)
            kit.giveKit(event.getEntityPlayer());
    }

    @Override
    public void loadConfig(Configuration config, String category)
    {
        kitForNewPlayers = config.get(category, "kitForNewPlayers", "", "Name of kit to issue to new players. If this is left blank, it will be ignored.")
                .getString();
    }

    @Override
    public void loadData()
    {
        kits = DataManager.getInstance().loadAll(Kit.class);
    }

    public static void removeKit(Kit kit)
    {
        kits.remove(kit.getName());
        DataManager.getInstance().delete(Kit.class, kit.getName());
    }

    public static void addKit(Kit kit)
    {
        kits.put(kit.getName(), kit);
        DataManager.getInstance().save(kit, kit.getName());
    }

}
