package com.forgeessentials.util.selections;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.Selection;
import com.forgeessentials.util.PlayerInfo;
import net.minecraft.entity.player.EntityPlayerMP;

public class PlayerInfoSelectionProvider implements ISelectionProvider {

    @Override
    public Point getPoint1(EntityPlayerMP player)
    {
        PlayerInfo pi = PlayerInfo.getPlayerInfo(player);
        return pi.getSel1();
    }

    @Override
    public Point getPoint2(EntityPlayerMP player)
    {
        PlayerInfo pi = PlayerInfo.getPlayerInfo(player);
        return pi.getSel2();
    }

    @Override
    public Selection getSelection(EntityPlayerMP player)
    {
        PlayerInfo pi = PlayerInfo.getPlayerInfo(player);
        if (pi.getSel1() == null || pi.getSel2() == null)
        {
            return null;
        }
        return new Selection(pi.getSel1(), pi.getSel2());
    }

    @Override
    public void setPoint1(EntityPlayerMP player, Point sel1)
    {
        PlayerInfo pi = PlayerInfo.getPlayerInfo(player);
        pi.setSel1(sel1);
        pi.sendSelectionUpdate();
    }

    @Override
    public void setPoint2(EntityPlayerMP player, Point sel2)
    {
        PlayerInfo pi = PlayerInfo.getPlayerInfo(player);
        pi.setSel2(sel2);
        pi.sendSelectionUpdate();
    }

}
