package com.forgeessentials.util.selections;

import net.minecraft.entity.player.EntityPlayerMP;

public interface ISelectionProvider
    {
        public Point getPoint1(EntityPlayerMP player);

        public Point getPoint2(EntityPlayerMP player);

        public Selection getSelection(EntityPlayerMP player);

}
