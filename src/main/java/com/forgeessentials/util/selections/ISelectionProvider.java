package com.forgeessentials.util.selections;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;

public interface ISelectionProvider
{

    public Selection getSelection(EntityPlayerMP player);

    public void setDimension(EntityPlayerMP player, int dim);

    public void setStart(EntityPlayerMP player, Point start);

    public void setEnd(EntityPlayerMP player, Point end);

    public void select(EntityPlayerMP player, int dimension, AreaBase area);

}
