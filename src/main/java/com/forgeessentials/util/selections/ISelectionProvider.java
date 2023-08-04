package com.forgeessentials.util.selections;

import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;

import net.minecraft.entity.player.ServerPlayerEntity;

public interface ISelectionProvider
{

    public Selection getSelection(ServerPlayerEntity player);

    public void setDimension(ServerPlayerEntity player, String dim);

    public void setStart(ServerPlayerEntity player, Point start);

    public void setEnd(ServerPlayerEntity player, Point end);

    public void select(ServerPlayerEntity player, String dimension, AreaBase area);

}
