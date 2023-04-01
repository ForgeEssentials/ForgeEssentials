package com.forgeessentials.teleport.commands;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
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

public class CommandWarp extends ForgeEssentialsCommandBuilder
{

    public CommandWarp(boolean enabled)
    {
        super(enabled);
    }

    public static class Warp extends WarpPoint
    {
        public Warp(Entity entity)
        {
            super(entity);
        }
    }

    private static final String PERM = "fe.teleport.warp";
    private static final String PERM_SET = PERM + ".set";
    private static final String PERM_DELETE = PERM + ".delete";
    private static final String PERM_LIMIT = PERM + ".max";
    private static final String PERM_WARP = PERM + ".warp";

    @Override
    public String getPrimaryAlias()
    {
        return "warp";
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
        APIRegistry.perms.registerPermission(PERM_SET, DefaultPermissionLevel.OP, "Allow setting warps");
        APIRegistry.perms.registerPermission(PERM_DELETE, DefaultPermissionLevel.OP, "Allow deleting warps");
        APIRegistry.perms.registerPermissionProperty(PERM_LIMIT, "10", "Maximal warp count");
        APIRegistry.perms.registerPermissionPropertyOp(PERM_LIMIT, "false");
        APIRegistry.perms.registerPermission(PERM_WARP + ".*", DefaultPermissionLevel.OP, "Allows permission to use all warps");
    }

    public static Map<String, Warp> getWarps()
    {
        return DataManager.getInstance().loadAll(Warp.class);
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.literal("warp")
                        .then(Commands.argument("warp", StringArgumentType.word())
                                .executes(CommandContext -> execute(CommandContext, "warp")
                                        )
                                )
                        )
                .then(Commands.literal("set")
                        .then(Commands.argument("warp", StringArgumentType.word())
                                .executes(CommandContext -> execute(CommandContext, "set")
                                        )
                                )
                        )
                .then(Commands.literal("delete")
                        .then(Commands.argument("warp", StringArgumentType.word())
                                .executes(CommandContext -> execute(CommandContext, "delete")
                                        )
                                )
                        )
                .then(Commands.literal("list")
                        .executes(CommandContext -> execute(CommandContext, "list")
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "help")
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (params.toString().equals("help"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "/warp list: List warps");
            return Command.SINGLE_SUCCESS;
        }

        Map<String, Warp> warps = getWarps();

        Set<String> completeList = new HashSet<>();
        completeList.addAll(warps.keySet());


        if (params.toString().equals("list"))
        {
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Warps: " + StringUtils.join(warps.keySet(), ", "));
            return Command.SINGLE_SUCCESS;
        }
        String warpName = StringArgumentType.getString(ctx, "warp");
        if (params.toString().equals("warp"))
        {

            WarpPoint point = warps.get(warpName);
            if (point == null)
                throw new TranslatedCommandException("Warp by this name does not exist");
            if (!hasPermission(ctx.getSource(),PERM_WARP + "." + warpName))
                throw new TranslatedCommandException("You don't have permission to use this warp");
            TeleportHelper.teleport(getServerPlayer(ctx.getSource()), point);
            return Command.SINGLE_SUCCESS;
        }
        if (params.toString().equals("set"))
        {
            checkPermission(ctx.getSource(),PERM_SET);

            // Check limit
            int limit = ServerUtil.parseIntDefault(APIRegistry.perms.getUserPermissionProperty(getIdent(ctx.getSource()), PERM_LIMIT), Integer.MAX_VALUE);
            if (warps.size() >= limit)
                throw new TranslatedCommandException("You reached the warp limit");

            DataManager.getInstance().save(new Warp(getServerPlayer(ctx.getSource())), warpName);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Set warp \"%s\" to current location", warpName);
            return Command.SINGLE_SUCCESS;
        }
        if (params.toString().equals("delete"))
        {
            checkPermission(ctx.getSource(),PERM_DELETE);
            DataManager.getInstance().delete(Warp.class, warpName);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "Deleted warp \"%s\"", warpName);
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }

}
