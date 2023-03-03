package com.forgeessentials.permissions.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.GroupEntry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

public class CommandPromote extends ForgeEssentialsCommandBuilder
{

    public CommandPromote(boolean enabled)
    {
        super(enabled);
    }

    public static final String PERM_NODE = "fe.perm.promote";

    @Override
    public String getPrimaryAlias()
    {
        return "promote";
    }

    @Override
    public String getPermissionNode()
    {
        return PERM_NODE;
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
        return builder
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("group", StringArgumentType.greedyString())
                                .suggests(SUGGEST_WARPS)
                                .executes(context -> execute(context, "group")
                                        )
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "help")
                        );
    }

    public static final SuggestionProvider<CommandSource> SUGGEST_WARPS = (ctx, builder) -> {
        List<String> completeList = new ArrayList<String>();
        for (String group : APIRegistry.perms.getServerZone().getGroups())
            completeList.add(group);
        return ISuggestionProvider.suggest(completeList, builder);
     };

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params.toString().equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/promote <player> <group>");
            return Command.SINGLE_SUCCESS;
        }

        UserIdent ident = getIdent(EntityArgument.getPlayer(ctx, "player"));

        String groupName = StringArgumentType.getString(ctx, "group");

        if (!APIRegistry.perms.groupExists(groupName))
            throw new TranslatedCommandException("Group %s does not exist", groupName);

        if (!Zone.PERMISSION_TRUE.equals(APIRegistry.perms.getServerZone().getGroupPermission(groupName, FEPermissions.GROUP_PROMOTION)))
            throw new TranslatedCommandException("Group %s is not available for promotion. Allow %s on the group first.", groupName,
                    FEPermissions.GROUP_PROMOTION);

        for (GroupEntry group : APIRegistry.perms.getServerZone().getStoredPlayerGroupEntries(ident))
            if (!Zone.PERMISSION_TRUE.equals(APIRegistry.perms.getServerZone().getGroupPermission(group.getGroup(), FEPermissions.GROUP_PROMOTION)))
            {
                APIRegistry.perms.removePlayerFromGroup(ident, group.getGroup());
                ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Removed %s from group %s", ident.getUsernameOrUuid(), group));
                if (ident.hasPlayer())
                    ChatOutputHandler.chatConfirmation(ident.getPlayer().createCommandSourceStack(),
                            Translator.format("You have been removed from the %s group", group));
            }
        APIRegistry.perms.addPlayerToGroup(ident, groupName);
        ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Added %s to group %s", ident.getUsernameOrUuid(), groupName));
        if (ident.hasPlayer())
            ChatOutputHandler.chatConfirmation(ident.getPlayer().createCommandSourceStack(),
                    Translator.format("You have been added to the %s group", groupName));
        return Command.SINGLE_SUCCESS;
    }

}
