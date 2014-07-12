package com.forgeessentials.util.AreaSelector;

import net.minecraft.entity.player.EntityPlayerMP;

public class SelectionHandler
{
    public static ISelectionProvider selectionProvider;

    public Point getPoint1(EntityPlayerMP player){ return selectionProvider.getPoint1(player);}

    public Point getPoint2(EntityPlayerMP player){ return selectionProvider.getPoint2(player);}

    public Selection getSelection(EntityPlayerMP player){ return selectionProvider.getSelection(player);}

    public interface ISelectionProvider
    {
        public Point getPoint1(EntityPlayerMP player);

        public Point getPoint2(EntityPlayerMP player);

        public Selection getSelection(EntityPlayerMP player);
    }
}
