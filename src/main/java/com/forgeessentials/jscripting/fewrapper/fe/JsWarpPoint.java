package com.forgeessentials.jscripting.fewrapper.fe;

import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WorldPoint;

public class JsWarpPoint extends JsWrapper<WarpPoint>
{
    public JsWarpPoint(WarpPoint that)
    {
        super(that);
    }

    public JsPoint<Point> getPoint()
    {
        return new JsPoint(new Point(that.getBlockX(), that.getBlockY(), that.getBlockZ()));
    }

    public JsWorldPoint<WorldPoint> getWorldPoint()
    {
        return new JsWorldPoint(new WorldPoint(that.getDimension(), that.getBlockX(), that.getBlockY(), that.getBlockZ()));
    }
}
