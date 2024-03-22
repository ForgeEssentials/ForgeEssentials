package com.forgeessentials.commands.player;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundAddMobPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandVanish extends ForgeEssentialsCommandBuilder
{

    public CommandVanish(boolean enabled)
    {
        super(enabled);
    }

    public static final String PERM = "fe.commands.vanish";

    public static final String PERM_OTHERS = PERM + ".others";

    private static Set<UserIdent> vanishedPlayers = new HashSet<>();

    @Override
    public @NotNull String getPrimaryAlias()
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
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(PERM_OTHERS, DefaultPermissionLevel.OP, "Allow to vanish other players");
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.argument("player", EntityArgument.player())
                .executes(CommandContext -> execute(CommandContext, "blank")));
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
    {
    	ChatOutputHandler.chatWarning(ctx.getSource(), "This command has not been fully ported");
//        ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
//        if (EntityArgument.getPlayer(ctx, "player") != getServerPlayer(ctx.getSource()))
//        {
//            if (!hasPermission(ctx.getSource(), PERM_OTHERS))
//            {
//                ChatOutputHandler.chatError(ctx.getSource(), "You don't have permission to vanish other players");
//                return Command.SINGLE_SUCCESS;
//            }
//            player = EntityArgument.getPlayer(ctx, "player");
//        }
//
//        vanishToggle(UserIdent.get(player));
//        if (isVanished(UserIdent.get(player)))
//            ChatOutputHandler.chatConfirmation(ctx.getSource(), "You are vanished now");
//        else
//            ChatOutputHandler.chatConfirmation(ctx.getSource(), "You are visible now");
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
        ServerPlayer player = ident.getPlayerMP();
        ServerLevel world = (ServerLevel) player.getLevel();
        List<ServerPlayer> players = world.players();
        if (vanish)
        {
            vanishedPlayers.add(ident);
            for (ServerPlayer playerO : players)
            {
                player.stopSeenByPlayer(playerO);
                //playerO.sendRemoveEntity(player);
            }
        }
        else
        {

            vanishedPlayers.remove(ident);
            for (ServerPlayer playerO : players)
            {
                sendPairingData(player, playerO.connection::send);
                player.startSeenByPlayer(playerO);
                //playerO.cancelRemoveEntity(player);
            }
        }
    }

    public static void sendPairingData(ServerPlayer player, Consumer<Packet<?>> p_219452_1_)
    {
        if (player.isAlive())
        {
            LoggingHandler.felog.warn("Fetching packet for removed entity " + player);
        }

        Packet<?> ipacket = player.getAddEntityPacket();
        player.yHeadRot = Mth.floor(player.getYHeadRot() * 256.0F / 360.0F);
        p_219452_1_.accept(ipacket);
        if (!player.getEntityData().isEmpty())
        {
            p_219452_1_.accept(new ClientboundSetEntityDataPacket(player.getId(), player.getEntityData(), true));
        }

        boolean flag = false;
        if (player instanceof LivingEntity)
        {
            Collection<AttributeInstance> collection = ((LivingEntity) player).getAttributes()
                    .getSyncableAttributes();
            if (!collection.isEmpty())
            {
                p_219452_1_.accept(new ClientboundUpdateAttributesPacket(player.getId(), collection));
            }

            if (((LivingEntity) player).isFallFlying())
            {
                flag = true;
            }
        }

        if (flag && !(ipacket instanceof ClientboundAddMobPacket))
        {
            p_219452_1_.accept(new ClientboundSetEntityMotionPacket(player.getId(), player.getDeltaMovement()));
        }

        if (player instanceof LivingEntity)
        {
            List<Pair<EquipmentSlot, ItemStack>> list = Lists.newArrayList();

            for (EquipmentSlot equipmentslottype : EquipmentSlot.values())
            {
                ItemStack itemstack = ((LivingEntity) player).getItemBySlot(equipmentslottype);
                if (!itemstack.isEmpty())
                {
                    list.add(Pair.of(equipmentslottype, itemstack.copy()));
                }
            }

            if (!list.isEmpty())
            {
                p_219452_1_.accept(new ClientboundSetEquipmentPacket(player.getId(), list));
            }
        }

        if (player instanceof LivingEntity)
        {

            for (MobEffectInstance effectinstance : ((LivingEntity) player).getActiveEffects())
            {
                p_219452_1_.accept(new ClientboundUpdateMobEffectPacket(player.getId(), effectinstance));
            }
        }

        if (!player.getPassengers().isEmpty())
        {
            p_219452_1_.accept(new ClientboundSetPassengersPacket(player));
        }

        if (player.isPassenger())
        {
            p_219452_1_.accept(new ClientboundSetPassengersPacket(player.getVehicle()));
        }

    }
}
