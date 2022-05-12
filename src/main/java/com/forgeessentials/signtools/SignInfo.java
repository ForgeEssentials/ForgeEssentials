package com.forgeessentials.signtools;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.Event;

import com.forgeessentials.util.StringUtil;

public class SignInfo
{
    int x, y, z, dim;
    String[] text;
    String event;
    Hand hand;
    BlockRayTraceResult hitVec;

    public SignInfo(int dim, BlockPos pos, String[] text, Event event)
    {
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
        this.text = text;
        this.dim = dim;
        if (event != null)
        {
            this.event = event.getClass().getSimpleName();
        }

        if (event instanceof PlayerInteractEvent)
        {
            this.hand = ((PlayerInteractEvent) event).getHand();
            if (event instanceof RightClickBlock)
            {
                this.hitVec = ((RightClickBlock) event).getHitVec();
            }
            else if (event instanceof LeftClickBlock)
            {
                this.hitVec = ((LeftClickBlock) event).getHitVec();
            }
        }
    }

    @Override public String toString()
    {
        return "{" +
                "\"x\":" + x +
                ", \"y\":" + y +
                ", \"z\":" + z +
                ", \"dim\":" + dim +
                ", \"text\":" + StringUtil.toJsonString(text) +
                ", \"hand\":\"" + hand + "\"" +
                ", \"hitVec\":" +
                (hitVec != null ? "{" +
                        "\"x\":" + hitVec.getBlockPos().getX() +
                        ", \"y\":" + hitVec.getBlockPos().getY() +
                        ", \"z\":" + hitVec.getBlockPos().getZ() +
                        "}" : "null") +
                "}";
    }
}
