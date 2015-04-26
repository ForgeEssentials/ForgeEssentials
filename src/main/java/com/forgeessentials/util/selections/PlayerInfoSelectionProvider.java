package com.forgeessentials.util.selections;

import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.util.PlayerInfo;

public class PlayerInfoSelectionProvider implements ISelectionProvider {

    @Override
    public Selection getSelection(EntityPlayerMP player)
    {
        PlayerInfo pi = PlayerInfo.getPlayerInfo(player);
        return new Selection(pi.getSelDim(), pi.getSel1(), pi.getSel2());
    }

    @Override
    public void setDimension(EntityPlayerMP player, int dim)
    {
        PlayerInfo.getPlayerInfo(player).setSelDim(dim);
    }

    @Override
    public void setStart(EntityPlayerMP player, Point start)
    {
        PlayerInfo.getPlayerInfo(player).setSel1(start);
    }

    @Override
    public void setEnd(EntityPlayerMP player, Point end)
    {
        PlayerInfo.getPlayerInfo(player).setSel2(end);
    }

    @Override
    public void select(EntityPlayerMP player, int dimension, AreaBase area)
    {
        PlayerInfo pi = PlayerInfo.getPlayerInfo(player);
        pi.setSelDim(dimension);
        pi.setSel1(area.getLowPoint());
        pi.setSel2(area.getHighPoint());
        SelectionHandler.sendUpdate(player);
    }
    
}
