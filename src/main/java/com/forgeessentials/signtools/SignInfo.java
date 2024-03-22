package com.forgeessentials.signtools;

import com.forgeessentials.util.StringUtil;

import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.Event;

public class SignInfo
{
    int x, y, z;
    String dim;
    String[] text;
    InteractionHand hand;
    BlockHitResult hitVec;

    public SignInfo(String dim, BlockPos pos, String[] text, Event event)
    {
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
        this.text = text;
        this.dim = dim;

        if (event instanceof PlayerInteractEvent)
        {
            this.hand = ((PlayerInteractEvent) event).getHand();
            if (event instanceof RightClickBlock)
            {
                this.hitVec = ((RightClickBlock) event).getHitVec();
            }
            else if (event instanceof LeftClickBlock)
            {
                this.hitVec = null;
            }
        }
    }

    @Override
    public String toString()
    {
        return "{" + "\"x\":" + x + ", \"y\":" + y + ", \"z\":" + z + ", \"dim\":" + dim + ", \"text\":"
                + StringUtil.toJsonString(text) + ", \"hand\":\"" + hand + "\"" + ", \"hitVec\":"
                + (hitVec != null
                        ? "{" + "\"x\":" + hitVec.getBlockPos().getX() + ", \"y\":" + hitVec.getBlockPos().getY()
                                + ", \"z\":" + hitVec.getBlockPos().getZ() + "}"
                        : "null")
                + "}";
    }
}
