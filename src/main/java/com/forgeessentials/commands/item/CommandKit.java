package com.forgeessentials.commands.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commands.util.Kit;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.events.FEPlayerEvent.NoPlayerInfoEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

/**
 * Kit command with cooldown. Should also put armor in armor slots.
 */

public class CommandKit extends ForgeEssentialsCommandBuilder implements ConfigurableCommand
{

    public CommandKit(boolean enabled)
    {
        super(enabled);
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

    public List<String> getAvailableKits(CommandSource source)
    {
        List<String> availableKits = new ArrayList<>();
        for (Kit kit : kits.values())
            if (hasPermission(source, PERM + "." + kit.getName()))
                availableKits.add(kit.getName());
        return availableKits;
    }

    public static final SuggestionProvider<CommandSource> SUGGEST_KITS = (ctx, builder) -> {
        List<String> availableKits = new ArrayList<>();
        for (Kit kit : CommandKit.kits.values())
            if (com.forgeessentials.util.CommandUtils.hasPermission(ctx.getSource(),CommandKit.PERM + "." + kit.getName()))
                availableKits.add(kit.getName());
        return ISuggestionProvider.suggest(availableKits, builder);
     };

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("select")
                        .then(Commands.argument("kit", StringArgumentType.string())
                                .suggests(SUGGEST_KITS)
                                .executes(context -> execute(context, "select")
                                        )
                                )
                        )
                .then(Commands.literal("listAvaiable")
                        .executes(CommandContext -> execute(CommandContext, "listAvaiable")
                                )
                        )
                .then(Commands.literal("modify")
                        .then(Commands.literal("set")
                                .then(Commands.argument("cooldown", IntegerArgumentType.integer(0))
                                        .executes(CommandContext -> execute(CommandContext, "set")
                                                )
                                        )
                                )
                        .then(Commands.literal("delete")
                                .executes(CommandContext -> execute(CommandContext, "listAvaiable")
                                        )
                                )
                        );
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("listAvaiable"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Available kits: %s", StringUtils.join(getAvailableKits(ctx.getSource()), ", ")));
            return Command.SINGLE_SUCCESS;
        }

        final String kitName = StringArgumentType.getString(ctx, "kit");
        Kit kit = kits.get(kitName);

        if (params.equals("select"))
        {
            if (kit == null)
                throw new TranslatedCommandException("Kit %s does not exist", kitName);
            if (APIRegistry.perms.checkPermission((PlayerEntity) ctx.getSource().getEntity(),PERM + "." + kit.getName()))
                throw new TranslatedCommandException("You are not allowed to use this kit");
            kit.giveKit((PlayerEntity) ctx.getSource().getEntity());
            return Command.SINGLE_SUCCESS;
        }

        APIRegistry.perms.checkPermission((PlayerEntity) ctx.getSource().getEntity(),PERM_ADMIN);

        switch (params)
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
                    if (true)//!arguments.isEmpty()) idk todo here
                        try
                        {
                            cooldown = IntegerArgumentType.getInteger(ctx, "cooldown");
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
        case "delete":
            removeKit(kit);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Deleted kit %s", kitName));
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, params);
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
