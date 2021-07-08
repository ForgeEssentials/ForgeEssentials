package com.forgeessentials.signtools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import com.forgeessentials.util.StringUtil;

public class SignInfo {
    int x,y,z, dim;
    String[] text;

    public SignInfo(EntityPlayer sender, BlockPos pos, String[] text) {
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
        this.text = text;
        dim = sender.dimension;
    }

    @Override public String toString()
    {
        return "{" +
                "\"x\":" + x +
                ", \"y\":" + y +
                ", \"z\":" + z +
                ", \"dim\":" + dim +
                ", \"text\":" + StringUtil.toJsonString(text) +
                "}";
    }
}
