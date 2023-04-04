package com.forgeessentials.commands.player;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandAFK extends ForgeEssentialsCommandBuilder
{

    public CommandAFK(boolean enabled)
    {
        super(enabled);
    }

    public static final String PERM = "fe.commands.afk";

    public static final String PERM_ANNOUNCE = PERM + ".announce";

    public static final String PERM_WARMUP = PERM + ".warmup";

    public static final String PERM_AUTOTIME = PERM + ".autotime";

    public static final String PERM_AUTOKICK = PERM + ".autokick";

    @Override
    public String getPrimaryAlias()
    {
        return "afk";
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
        APIRegistry.perms.registerPermission(PERM_ANNOUNCE, DefaultPermissionLevel.ALL, "Announce when a player goes AFK, or returns from AFK");
        APIRegistry.perms.registerPermissionProperty(PERM_WARMUP, "10", "Time a player needs to wait before he can go afk with /afk");
        APIRegistry.perms.registerPermissionProperty(PERM_AUTOTIME, "480", "Auto afk time in seconds. Set to 0 to disable.");
        APIRegistry.perms.registerPermission(PERM_AUTOKICK, DefaultPermissionLevel.NONE, "Automatically kick a player, when he is AFK");
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("timeout")
                        .then(Commands.literal("group")
                                .then(Commands.argument("group", StringArgumentType.word())
                                        .then(Commands.argument("timeout", IntegerArgumentType.integer())
                                                .executes(CommandContext -> execute(CommandContext, "timeout-G")
                                                        )
                                                )
                                        )
                                )
                        .then(Commands.literal("player")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("timeout", IntegerArgumentType.integer())
                                                .executes(CommandContext -> execute(CommandContext, "timeout-P")
                                                        )
                                                )
                                        )
                                )
                        )
                .then(Commands.literal("autokick")
                        .then(Commands.literal("group")
                                .then(Commands.argument("group", StringArgumentType.string())
                                        .then(Commands.argument("yn", BoolArgumentType.bool())
                                                .executes(CommandContext -> execute(CommandContext, "autokick-G")
                                                        )
                                                )
                                        )
                                )
                        .then(Commands.literal("player")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("yn", BoolArgumentType.bool())
                                                .executes(CommandContext -> execute(CommandContext, "autokick-P")
                                                        )
                                                )
                                        )
                                )
                        )
                .executes(CommandContext -> execute(CommandContext, "afk")
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        UserIdent ident = UserIdent.get(ctx.getSource());

     // expected syntax: /afk timeout <group|player> <timeout>
        // to set custom afk timeout for yourself, replace <player> with your own username
        String[] arg = params.toString().split("-");
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "{"+arg[0]+"}");
        if (arg[0].equals("timeout"))
        {
            Integer amount = IntegerArgumentType.getInteger(ctx, "timeout");
            if ((arg[0]).equals("P"))
            {
                UserIdent applyTo = UserIdent.get(EntityArgument.getPlayer(ctx, "player").getUUID().toString(), true);
                APIRegistry.perms.setPlayerPermissionProperty(applyTo, PERM_AUTOTIME, amount.toString());
            }
            else
            {
                APIRegistry.perms.setGroupPermissionProperty(StringArgumentType.getString(ctx, "group"), PERM_AUTOTIME, amount.toString());
            }
        }
        // expected syntax: /afk timeout <group|player> [true|false}
        else if (arg[0].equals("autokick"))
        {
            Boolean amount = BoolArgumentType.getBool(ctx, "yn");
            if (arg[0].equals("P"))
            {
                UserIdent applyTo = UserIdent.get(EntityArgument.getPlayer(ctx, "player").getUUID().toString(), true);
                APIRegistry.perms.setPlayerPermissionProperty(applyTo, PERM_AUTOKICK, amount.toString());
            }
            else
            {
                APIRegistry.perms.setGroupPermissionProperty(StringArgumentType.getString(ctx, "group"), PERM_AUTOKICK, amount.toString());
            }
        }
        else
        {
            int autoTime = ServerUtil.parseIntDefault(ident.getPermissionProperty(CommandAFK.PERM_AUTOTIME), 60 * 2);
            int warmup = ServerUtil.parseIntDefault(ident.getPermissionProperty(PERM_WARMUP), 0);
            PlayerInfo.get(getServerPlayer(ctx.getSource())).setActive(autoTime * 1000 - warmup * 1000);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), Translator.format("Stand still for %d seconds.", warmup));
        }
        return Command.SINGLE_SUCCESS;
    }
}