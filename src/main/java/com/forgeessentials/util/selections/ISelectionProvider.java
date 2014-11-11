package com.forgeessentials.util.selections;

import com.forgeessentials.commons.Point;
import com.forgeessentials.commons.Selection;
import net.minecraft.entity.player.EntityPlayerMP;

public interface ISelectionProvider {

    public Point getPoint1(EntityPlayerMP player);

    public Point getPoint2(EntityPlayerMP player);

    public Selection getSelection(EntityPlayerMP player);

    public void setPoint1(EntityPlayerMP player, Point point);

    public void setPoint2(EntityPlayerMP player, Point point);

}
