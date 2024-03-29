package com.forgeessentials.commands.player;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.PlayerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandSmite extends ForgeEssentialsCommandBuilder
{

    public CommandSmite(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "smite";
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
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(ModuleCommands.PERM + ".smite.others", DefaultPermissionLevel.OP,
                "Smite others");
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder
                .then(Commands.literal("player")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(CommandContext -> execute(CommandContext, "player"))))
                .then(Commands.literal("location")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(CommandContext -> execute(CommandContext, "location")))
                        .executes(CommandContext -> execute(CommandContext, "looking")));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        if (params.equals("player"))
        {
            ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
            if (player == getServerPlayer(ctx.getSource()))
            {
                LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(player.level);
                lightningboltentity.moveTo(Vector3d
                        .atBottomCenterOf(new BlockPos(player.position().x, player.position().y, player.position().z)));
                lightningboltentity.setVisualOnly(false);
                player.level.addFreshEntity(lightningboltentity);
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "Was that really a good idea?");
                return Command.SINGLE_SUCCESS;
            }
            else
            {
                if (hasPermission(ctx.getSource(), ModuleCommands.PERM + ".smite.others"))
                {
                    LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(player.level);
                    lightningboltentity.moveTo(Vector3d.atBottomCenterOf(
                            new BlockPos(player.position().x, player.position().y, player.position().z)));
                    lightningboltentity.setVisualOnly(false);
                    player.level.addFreshEntity(lightningboltentity);
                    ChatOutputHandler.chatConfirmation(ctx.getSource(), "You should feel bad about doing that.");
                }
                else
                {
                    ChatOutputHandler.chatError(ctx.getSource(), "You don't have permission to smite other players");
                    return Command.SINGLE_SUCCESS;
                }
                return Command.SINGLE_SUCCESS;
            }

        }

        if (params.equals("location"))
        {
            BlockPos pos = BlockPosArgument.getLoadedBlockPos(ctx, "pos");
            LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(ctx.getSource().getLevel());
            lightningboltentity.moveTo(Vector3d.atBottomCenterOf(pos));
            lightningboltentity.setVisualOnly(false);
            ctx.getSource().getLevel().addFreshEntity(lightningboltentity);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "I hope that didn't start a fire.");
            return Command.SINGLE_SUCCESS;
        }
        if (params.equals("looking"))
        {
            RayTraceResult mop = PlayerUtil.getPlayerLookingSpot(getServerPlayer(ctx.getSource()), 500);
            if (mop.getType() == RayTraceResult.Type.MISS)
            {
                ChatOutputHandler.chatError(ctx.getSource(), "You must first look at the ground!");
            }
            else
            {
                BlockPos pos = new BlockPos(mop.getLocation().x, mop.getLocation().y, mop.getLocation().z);
                LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(ctx.getSource().getLevel());
                lightningboltentity.moveTo(Vector3d.atBottomCenterOf(pos));
                lightningboltentity.setVisualOnly(false);
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "I hope that didn't start a fire.");
            }
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
        if (!player.hasDisconnected())
        {
            LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(player.level);
            lightningboltentity.moveTo(Vector3d
                    .atBottomCenterOf(new BlockPos(player.position().x, player.position().y, player.position().z)));
            lightningboltentity.setVisualOnly(false);
            player.level.addFreshEntity(lightningboltentity);
            ChatOutputHandler.chatConfirmation(ctx.getSource(), "You should feel bad about doing that.");
        }
        else
            ChatOutputHandler.chatError(ctx.getSource(), "Player %s does not exist, or is not online.",
                    player.getDisplayName().getString());
        return Command.SINGLE_SUCCESS;
    }

}
