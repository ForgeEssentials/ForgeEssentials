package com.forgeessentials.util.selections;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;

import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;

public interface ISelectionProvider
{

    public Selection getSelection(ServerPlayerEntity player);

    public void setDimension(ServerPlayerEntity player, World dim);

    public void setStart(ServerPlayerEntity player, Point start);

    public void setEnd(ServerPlayerEntity player, Point end);

    public void select(ServerPlayerEntity player, World dimension, AreaBase area);

}
