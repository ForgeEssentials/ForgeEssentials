package com.forgeessentials.signtools;

import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.Event;

import com.forgeessentials.util.StringUtil;

public class SignInfo
{
    int x, y, z, dim;
    String[] text;
    String event;
    EnumHand hand;
    Vec3d hitVec;

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
                        "\"x\":" + hitVec.x +
                        ", \"y\":" + hitVec.y +
                        ", \"z\":" + hitVec.z +
                        "}" : "null") +
                "}";
    }
}
