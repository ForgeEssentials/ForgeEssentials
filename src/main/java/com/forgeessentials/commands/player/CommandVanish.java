package com.forgeessentials.commands.player;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SEntityEquipmentPacket;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.network.play.server.SEntityPropertiesPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.network.play.server.SSpawnMobPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

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
        return baseBuilder
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(CommandContext -> execute(CommandContext, "blank")
                                )
                        );
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        ChatOutputHandler.chatWarning(ctx.getSource(), "This command currently will break your player's movments, use at your own risk!");
        ServerPlayerEntity player = EntityArgument.getPlayer(ctx, "player");
        if (EntityArgument.getPlayer(ctx, "player")!=getServerPlayer(ctx.getSource()))
        {
            if (!hasPermission(ctx.getSource(),PERM_OTHERS)) {
            	ChatOutputHandler.chatError(ctx.getSource(), "You don't have permission to vanish other players");
                return Command.SINGLE_SUCCESS;
            }
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
            for (ServerPlayerEntity playerO : players) {
                player.stopSeenByPlayer(playerO);
                playerO.sendRemoveEntity(player);
            }
        }
        else
        {

            vanishedPlayers.remove(ident);
            for (ServerPlayerEntity playerO : players) {
                sendPairingData(player,playerO.connection::send);
                player.startSeenByPlayer(playerO);
                playerO.cancelRemoveEntity(player);
            }
        }
    }
    public static void sendPairingData(ServerPlayerEntity player, Consumer<IPacket<?>> p_219452_1_) {
        if (player.removed) {
            LoggingHandler.felog.warn("Fetching packet for removed entity " + player);
        }

        IPacket<?> ipacket = player.getAddEntityPacket();
        player.yHeadRot = MathHelper.floor(player.getYHeadRot() * 256.0F / 360.0F);
        p_219452_1_.accept(ipacket);
        if (!player.getEntityData().isEmpty()) {
           p_219452_1_.accept(new SEntityMetadataPacket(player.getId(), player.getEntityData(), true));
        }

        boolean flag = false;
        if (player instanceof LivingEntity) {
           Collection<ModifiableAttributeInstance> collection = ((LivingEntity)player).getAttributes().getSyncableAttributes();
           if (!collection.isEmpty()) {
              p_219452_1_.accept(new SEntityPropertiesPacket(player.getId(), collection));
           }

           if (((LivingEntity)player).isFallFlying()) {
              flag = true;
           }
        }

        if (flag && !(ipacket instanceof SSpawnMobPacket)) {
           p_219452_1_.accept(new SEntityVelocityPacket(player.getId(), player.getDeltaMovement()));
        }

        if (player instanceof LivingEntity) {
           List<Pair<EquipmentSlotType, ItemStack>> list = Lists.newArrayList();

           for(EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
              ItemStack itemstack = ((LivingEntity)player).getItemBySlot(equipmentslottype);
              if (!itemstack.isEmpty()) {
                 list.add(Pair.of(equipmentslottype, itemstack.copy()));
              }
           }

           if (!list.isEmpty()) {
              p_219452_1_.accept(new SEntityEquipmentPacket(player.getId(), list));
           }
        }

        if (player instanceof LivingEntity) {
           LivingEntity livingentity = (LivingEntity)player;

           for(EffectInstance effectinstance : livingentity.getActiveEffects()) {
              p_219452_1_.accept(new SPlayEntityEffectPacket(player.getId(), effectinstance));
           }
        }

        if (!player.getPassengers().isEmpty()) {
           p_219452_1_.accept(new SSetPassengersPacket(player));
        }

        if (player.isPassenger()) {
           p_219452_1_.accept(new SSetPassengersPacket(player.getVehicle()));
        }

     }
}
