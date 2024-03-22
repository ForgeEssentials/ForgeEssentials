package com.forgeessentials.util.selections;

import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;

import net.minecraft.server.level.ServerPlayer;

public interface ISelectionProvider
{

    public Selection getSelection(ServerPlayer player);

    public void setDimension(ServerPlayer player, String dim);

    public void setStart(ServerPlayer player, Point start);

    public void setEnd(ServerPlayer player, Point end);

    public void select(ServerPlayer player, String dimension, AreaBase area);

}
