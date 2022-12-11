package com.forgeessentials.commands.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.auth.AuthEventHandler;
import com.forgeessentials.auth.EncryptionHelper;
import com.forgeessentials.auth.PasswordManager;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commands.util.Kit;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.events.FEPlayerEvent.NoPlayerInfoEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

/**
 * Kit command with cooldown. Should also put armor in armor slots.
 */

public class CommandKit extends BaseCommand implements ConfigurableCommand
{

    public CommandKit(String name, int permissionLevel, boolean enabled)
    {
        super(name, permissionLevel, enabled);
    }

    public static final String PERM = ModuleCommands.PERM + ".kit";
    public static final String PERM_ADMIN = ModuleCommands.PERM + ".admin";
    public static final String PERM_BYPASS_COOLDOWN = PERM + ".bypasscooldown";

    static ForgeConfigSpec.ConfigValue<String> FEkitForNewPlayers;
    public static String kitForNewPlayers;

    public static Map<String, Kit> kits = new HashMap<>();

    @Override
    public String getPrimaryAlias()
    {
        return "kit";
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (arguments.isEmpty())
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Available kits: %s", StringUtils.join(getAvailableKits(arguments), ", "));
            return Command.SINGLE_SUCCESS;
        }

        arguments.tabComplete(getAvailableKits(arguments));
        final String kitName = arguments.remove().toLowerCase();
        Kit kit = kits.get(kitName);

        if (arguments.isEmpty())
        {
            if (kit == null)
                throw new TranslatedCommandException("Kit %s does not exist", kitName);
            if (APIRegistry.perms.checkPermission((PlayerEntity) ctx.getSource().getEntity(),PERM + "." + kit.getName()))
                throw new TranslatedCommandException("You are not allowed to use this kit");
            kit.giveKit((PlayerEntity) ctx.getSource().getEntity());
            return Command.SINGLE_SUCCESS;
        }

        APIRegistry.perms.checkPermission((PlayerEntity) ctx.getSource().getEntity(),PERM_ADMIN);

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
                        ChatOutputHandler.chatError(ctx.getSource(), "Question timed out");
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
                            ChatOutputHandler.chatError(ctx.getSource(), e.getMessage());
                        }
                    addKit(new Kit((PlayerEntity) ctx.getSource().getEntity(), kitName, cooldown));
                    if (cooldown < 0)
                        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Kit %s saved for one-time-use", kitName));
                    else
                        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Kit %s saved with cooldown %s", kitName, ChatOutputHandler.formatTimeDurationReadable(cooldown, true)));
                }
            };
            if (kit == null)
                callback.respond(true);
            else
                Questioner.addChecked(ctx.getSource(), Translator.format("Overwrite kit %s?", kitName), callback);
            break;
        case "del":
        case "delete":
            removeKit(kit);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Deleted kit %s", kitName));
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCommand);
        }
        return Command.SINGLE_SUCCESS;
    }

    @SubscribeEvent
    public void newPlayerEvent(NoPlayerInfoEvent event)
    {
        Kit kit = kits.get(kitForNewPlayers);
        if (kit != null)
            kit.giveKit(event.getPlayer());
    }

    @Override
    public void loadConfig(ForgeConfigSpec.Builder BUILDER, String category)
    {
    	BUILDER.push(category);
    	FEkitForNewPlayers = BUILDER.comment("Name of kit to issue to new players. If this is left blank, it will be ignored.").define("kitForNewPlayers", "");
    	BUILDER.pop();
    }
    
    @Override
    public void bakeConfig(boolean reload)
    {
    	kitForNewPlayers = FEkitForNewPlayers.get();
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
