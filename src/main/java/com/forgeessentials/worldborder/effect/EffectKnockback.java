package com.forgeessentials.worldborder.effect;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.WorldUtil;
import com.forgeessentials.util.events.PlayerMoveEvent;
import com.forgeessentials.util.questioner.QuestionerStillActiveException.CommandException;
import com.forgeessentials.worldborder.WorldBorder;
import com.forgeessentials.worldborder.WorldBorderEffect;

public class EffectKnockback extends WorldBorderEffect
{

    @Override
    public void provideArguments(CommandParserArgs args) throws CommandException
    {
    }

    @Override
    public void playerMove(WorldBorder border, PlayerMoveEvent event)
    {
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        if (event.before.getDimension() != event.after.getDimension())
        {
            // Cancel event if player was teleported
            event.setCanceled(true);
            return;
        }

        double dx = event.after.getX() - border.getCenter().getX();
        double dz = event.after.getZ() - border.getCenter().getZ();
        double len = Math.sqrt(dx * dx + dz * dz);

        WarpPoint p = new WarpPoint(event.after);
        p.setX(p.getX() - dx / len);
        p.setZ(p.getZ() - dz / len);
        if (!WorldUtil.isFree(p.getWorld(), p.getBlockX(), p.getBlockY(), p.getBlockZ(), 2))
            p.setY(WorldUtil.placeInWorld(p.getWorld(), p.getBlockX(), p.getBlockY(), p.getBlockZ()));

        if (player.getRidingEntity() != null)
            player.getRidingEntity().setLocationAndAngles(p.getX(), p.getY(), p.getZ(), player.getRidingEntity().rotationYaw, player.getRidingEntity().rotationPitch);
        player.connection.setPlayerLocation(p.getX(), p.getY(), p.getZ(), player.rotationYaw, player.rotationPitch);
    }

    public String toString()
    {
        return "knockback trigger: " + triggerDistance;
    }

    public String getSyntax()
    {
        return "";
    }
    
}
