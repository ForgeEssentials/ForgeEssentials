package com.forgeessentials.worldborder.effect;

import com.forgeessentials.core.misc.FECommandParsingException;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

/**
 * Expected syntax: <interval>
 */
public class EffectSmite extends WorldBorderEffect
{

    public int interval;

    @Override
    public void provideArguments(CommandContext<CommandSource> ctx) throws FECommandParsingException
    {
        interval = IntegerArgumentType.getInteger(ctx, "interval");
    }

    @Override
    public void activate(WorldBorder border, ServerPlayerEntity player)
    {
        if (interval <= 0)
            doEffect(player);
    }

    @Override
    public void tick(WorldBorder border, ServerPlayerEntity player)
    {
        if (interval <= 0)
            return;
        PlayerInfo pi = PlayerInfo.get(player);
        if (pi.checkTimeout(this.getClass().getName()))
        {
            doEffect(player);
            pi.startTimeout(this.getClass().getName(), interval * 1000L);
        }
    }

    public void doEffect(ServerPlayerEntity player)
    {

        LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(player.level);
        lightningboltentity.moveTo(
                Vector3d.atBottomCenterOf(new BlockPos(player.position().x, player.position().y, player.position().z)));
        lightningboltentity.setVisualOnly(true);
        player.getLevel().addFreshEntity(lightningboltentity);
    }

    public String toString()
    {
        return "smite interval: " + interval + " smite";
    }

    public String getSyntax()
    {
        return "<interval>";
    }

}
