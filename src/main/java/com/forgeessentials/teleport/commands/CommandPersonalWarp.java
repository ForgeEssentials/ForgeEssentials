package com.forgeessentials.teleport.commands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

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
    public String getPrimaryAlias()
    {
        return "pwarp";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
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
    public String getPermissionNode()
    {
        return PERM;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(PERM_SET, DefaultPermissionLevel.OP, "Allow setting personal warps");
        APIRegistry.perms.registerPermission(PERM_DELETE, DefaultPermissionLevel.OP, "Allow deleting personal warps");
        APIRegistry.perms.registerPermissionProperty(PERM_LIMIT, "10", "Maximal personal warp count");
        APIRegistry.perms.registerPermissionPropertyOp(PERM_LIMIT, "false");
    }

    public static PersonalWarp getWarps(ServerPlayerEntity player)
    {
        PersonalWarp warps = DataManager.getInstance().load(PersonalWarp.class, player.getUUID().toString());
        if (warps == null)
            warps = new PersonalWarp();
        return warps;
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("list")
                        .executes(context -> execute(context, "login")
                                )
                        )
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .suggests(SUGGEST_WARPS)
                        .then(Commands.literal("set")
                                .executes(context -> execute(context, "set")
                                        )
                                )
                        .then(Commands.literal("delete")
                                .executes(context -> execute(context, "delete")
                                        )
                                )
                        )
                .executes(context -> execute(context, "blank")
                        );
    }

    public static final SuggestionProvider<CommandSource> SUGGEST_WARPS = (ctx, builder) -> {
        PersonalWarp warps = getWarps(getServerPlayer(ctx.getSource()));

        Set<String> completeList = new HashSet<>();
        completeList.add("list");
        completeList.addAll(warps.keySet());
        return ISuggestionProvider.suggest(completeList, builder);
     };

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("blank"))
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

            checkPermission(ctx.getSource(), PERM_SET);

            // Check limit
            int limit = ServerUtil.parseIntDefault(APIRegistry.perms.getUserPermissionProperty(getIdent(ctx.getSource()), PERM_LIMIT), Integer.MAX_VALUE);
            if (warps.size() >= limit)
                throw new TranslatedCommandException("You reached your personal warp limit");

            warps.put(warpName, new WarpPoint(getServerPlayer(ctx.getSource())));
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Set personal warp \"%s\" to current location", warpName);
            DataManager.getInstance().save(warps, getServerPlayer(ctx.getSource()).getStringUUID());
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("delete"))
        {
            checkPermission(ctx.getSource(), PERM_DELETE);
            warps.remove(warpName);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Deleted personal warp \"%s\"", warpName);
            DataManager.getInstance().save(warps, getServerPlayer(ctx.getSource()).getStringUUID());
            return Command.SINGLE_SUCCESS;
        }
        
        WarpPoint point = warps.get(warpName);
        if (point == null)
            throw new TranslatedCommandException("Warp by this name does not exist");
        TeleportHelper.teleport(getServerPlayer(ctx.getSource()), point);
        return Command.SINGLE_SUCCESS;
    }

}
