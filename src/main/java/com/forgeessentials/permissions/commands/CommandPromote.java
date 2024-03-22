package com.forgeessentials.permissions.commands;

import java.util.ArrayList;
import java.util.List;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.GroupEntry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
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
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandPromote extends ForgeEssentialsCommandBuilder
{

    public CommandPromote(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "promote";
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
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("group", StringArgumentType.string()).suggests(SUGGEST_GROUPS)
                                .executes(context -> execute(context, "group"))))
                .executes(CommandContext -> execute(CommandContext, "help"));
    }

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_GROUPS = (ctx, builder) -> {
        List<String> completeList = new ArrayList<>(APIRegistry.perms.getServerZone().getGroups());
        return SharedSuggestionProvider.suggest(completeList, builder);
    };

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/promote <player> <group>");
            return Command.SINGLE_SUCCESS;
        }

        UserIdent ident = getIdent(EntityArgument.getPlayer(ctx, "player"));

        String groupName = StringArgumentType.getString(ctx, "group");

        if (!APIRegistry.perms.groupExists(groupName))
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Group %s does not exist", groupName);
            return Command.SINGLE_SUCCESS;
        }

        if (!Zone.PERMISSION_TRUE.equals(
                APIRegistry.perms.getServerZone().getGroupPermission(groupName, FEPermissions.GROUP_PROMOTION)))
        {
            ChatOutputHandler.chatError(ctx.getSource(),
                    "Group %s is not available for promotion. Allow %s on the group first.", groupName,
                    FEPermissions.GROUP_PROMOTION);
            return Command.SINGLE_SUCCESS;
        }

        for (GroupEntry group : APIRegistry.perms.getServerZone().getStoredPlayerGroupEntries(ident))
            if (!Zone.PERMISSION_TRUE.equals(APIRegistry.perms.getServerZone().getGroupPermission(group.getGroup(),
                    FEPermissions.GROUP_PROMOTION)))
            {
                APIRegistry.perms.removePlayerFromGroup(ident, group.getGroup());
                ChatOutputHandler.chatConfirmation(ctx.getSource(),
                        Translator.format("Removed %s from group %s", ident.getUsernameOrUuid(), group));
                if (ident.hasPlayer())
                    ChatOutputHandler.chatConfirmation(ident.getPlayer().createCommandSourceStack(),
                            Translator.format("You have been removed from the %s group", group));
            }
        APIRegistry.perms.addPlayerToGroup(ident, groupName);
        ChatOutputHandler.chatConfirmation(ctx.getSource(),
                Translator.format("Added %s to group %s", ident.getUsernameOrUuid(), groupName));
        if (ident.hasPlayer())
            ChatOutputHandler.chatConfirmation(ident.getPlayer().createCommandSourceStack(),
                    Translator.format("You have been added to the %s group", groupName));
        return Command.SINGLE_SUCCESS;
    }

}
