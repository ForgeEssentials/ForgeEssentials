package com.forgeessentials.commands.player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandVanish extends ForgeEssentialsCommandBuilder
{

    public CommandVanish(String name, int permissionLevel, boolean enabled)
    {
        super(enabled);
    }

    public static final String PERM = "fe.commands.vanish";

    public static final String PERM_OTHERS = PERM + ".others";

    private static Set<UserIdent> vanishedPlayers = new HashSet<>();

    @Override
    public String getPrimaryAlias()
    {
        return "vanish";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
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
        APIRegistry.perms.registerPermission(PERM_OTHERS, DefaultPermissionLevel.OP, "Allow to vanish other players");
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return builder
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(CommandContext -> execute(CommandContext, "setPass")
                                )
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
        if (EntityArgument.getPlayer(ctx, "player")!=getServerPlayer(ctx.getSource()))
        {
            if (!hasPermission(ctx.getSource(),PERM_OTHERS))
                throw new TranslatedCommandException("You don't have permission to vanish other players");
            player = EntityArgument.getPlayer(ctx, "player");
        }

        vanishToggle(UserIdent.get(player));
        if (isVanished(UserIdent.get(player)))
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"You are vanished now");
        else
            ChatOutputHandler.chatConfirmation(ctx.getSource(),"You are visible now");
        return Command.SINGLE_SUCCESS;
    }

    public static void vanishToggle(UserIdent ident)
    {
        vanish(ident, !isVanished(ident));
    }

    public static boolean isVanished(UserIdent ident)
    {
        return vanishedPlayers.contains(ident);
    }

    public static void vanish(UserIdent ident, boolean vanish)
    {
        ServerPlayerEntity player = ident.getPlayerMP();
        ServerWorld world = (ServerWorld) player.getLevel();
        List<ServerPlayerEntity> players = world.players();
        if (vanish)
        {
            vanishedPlayers.add(ident);
            EntityTrackerEntry tracker = world.getEntityTracker().trackedEntityHashTable.lookup(player.getId());

            Set<ServerPlayerEntity> tracked = new HashSet<>(tracker.trackingPlayers);
            world.getEntityTracker().untrack(player);
            tracked.forEach(tP -> {
                player.connection.sendPacket(new SPacketSpawnPlayer(tP));
            });
        }
        else
        {
            vanishedPlayers.remove(ident);
            world.getEntityTracker().track(player);
        }
    }
}
