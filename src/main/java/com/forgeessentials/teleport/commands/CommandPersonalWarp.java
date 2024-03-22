package com.forgeessentials.teleport.commands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.ServerUtil;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandPersonalWarp extends ForgeEssentialsCommandBuilder
{

    public CommandPersonalWarp(boolean enabled)
    {
        super(enabled);
    }

    public static class PersonalWarp extends HashMap<String, WarpPoint>
    {
    }

    private static final String PERM = "fe.teleport.personalwarp";
    private static final String PERM_SET = PERM + ".set";
    private static final String PERM_DELETE = PERM + ".delete";
    private static final String PERM_LIMIT = PERM + ".max";

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "pwarp";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
    {
        return new String[] { "pw", "personalwarp" };
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(PERM_SET, DefaultPermissionLevel.OP, "Allow setting personal warps");
        APIRegistry.perms.registerPermission(PERM_DELETE, DefaultPermissionLevel.OP, "Allow deleting personal warps");
        APIRegistry.perms.registerPermissionProperty(PERM_LIMIT, "10", "Maximal personal warp count");
        APIRegistry.perms.registerPermissionPropertyOp(PERM_LIMIT, "false");
    }

    public static PersonalWarp getWarps(ServerPlayer player)
    {
        PersonalWarp warps = DataManager.getInstance().load(PersonalWarp.class, player.getGameProfile().getId().toString());
        if (warps == null)
            warps = new PersonalWarp();
        return warps;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.literal("list").executes(context -> execute(context, "list")))
                .then(Commands.argument("name", StringArgumentType.word()).suggests(SUGGEST_WARPS)
                        .then(Commands.literal("set").executes(context -> execute(context, "set")))
                        .then(Commands.literal("delete").executes(context -> execute(context, "delete")))
                        .executes(context -> execute(context, "warp")))
                .then(Commands.literal("help").executes(context -> execute(context, "help")));
    }

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_WARPS = (ctx, builder) -> {
        PersonalWarp warps = getWarps(getServerPlayer(ctx.getSource()));

        Set<String> completeList = new HashSet<>(warps.keySet());
        return SharedSuggestionProvider.suggest(completeList, builder);
    };

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/pwarp list: List personal warps");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/pwarp <warpname>: Teleport to the warp");
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/pwarp <warpname> add|delete: Modify your warps");
            return Command.SINGLE_SUCCESS;
        }

        PersonalWarp warps = getWarps(getServerPlayer(ctx.getSource()));

        if (params.equals("list"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Warps: " + StringUtils.join(warps.keySet(), ", "));
            return Command.SINGLE_SUCCESS;
        }

        String warpName = StringArgumentType.getString(ctx, "name");

        if (params.equals("set"))
        {
            // Check limit
            int limit = ServerUtil.parseIntDefault(
                    APIRegistry.perms.getUserPermissionProperty(getIdent(ctx.getSource()), PERM_LIMIT),
                    Integer.MAX_VALUE);
            if (warps.size() >= limit)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "You reached your personal warp limit");
                return Command.SINGLE_SUCCESS;
            }

            warps.put(warpName, new WarpPoint(getServerPlayer(ctx.getSource())));
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Set personal warp \"%s\" to current location",
                    warpName);
            DataManager.getInstance().save(warps, getServerPlayer(ctx.getSource()).getStringUUID());
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("delete"))
        {
            warps.remove(warpName);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Deleted personal warp \"%s\"", warpName);
            DataManager.getInstance().save(warps, getServerPlayer(ctx.getSource()).getStringUUID());
            return Command.SINGLE_SUCCESS;
        }

        WarpPoint point = warps.get(warpName);
        if (point == null)
        {
            ChatOutputHandler.chatError(ctx.getSource(), "Warp by this name does not exist");
        }

        TeleportHelper.teleport(getServerPlayer(ctx.getSource()), point);
        return Command.SINGLE_SUCCESS;
    }

}
