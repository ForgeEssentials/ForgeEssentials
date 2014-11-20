package com.forgeessentials.commons.selections;

public enum AreaShape {

    BOX, ELLIPSOID, CYLINDER;

    public boolean contains(AreaBase area, Point point)
    {
        if (!area.contains(point))
            return false;
        if (this == BOX)
            return true;

        float dx = (float) (point.x - area.low.x) / (area.high.x - area.low.x) * 2 - 1;
        float dy = (float) (point.y - area.low.y) / (area.high.y - area.low.y) * 2 - 1;
        float dz = (float) (point.z - area.low.z) / (area.high.z - area.low.z) * 2 - 1;

        switch (this)
        {
        case ELLIPSOID:
            return dx * dx + dy * dy + dz * dz <= 1;
        case CYLINDER:
            return dx * dx + dz * dz <= 1;
        case BOX:
        default:
            return true;
        }
    }

    public boolean contains(AreaBase area, AreaBase otherArea)
    {
        if (this == BOX)
            return area.contains(otherArea);
        Point p1 = new Point(otherArea.low.x, otherArea.low.y, otherArea.low.z);
        Point p2 = new Point(otherArea.low.x, otherArea.low.y, otherArea.high.z);
        Point p3 = new Point(otherArea.low.x, otherArea.high.y, otherArea.low.z);
        Point p4 = new Point(otherArea.low.x, otherArea.high.y, otherArea.high.z);
        Point p5 = new Point(otherArea.high.x, otherArea.low.y, otherArea.low.z);
        Point p6 = new Point(otherArea.high.x, otherArea.low.y, otherArea.high.z);
        Point p7 = new Point(otherArea.high.x, otherArea.high.y, otherArea.low.z);
        Point p8 = new Point(otherArea.high.x, otherArea.high.y, otherArea.high.z);
        return (contains(area, p1) && contains(area, p2) && contains(area, p3) && contains(area, p4) && contains(area, p5) && contains(area, p6)
                && contains(area, p7) && contains(area, p8));
    }

    public static AreaShape getByName(String name)
    {
        if (name == null)
            return null;
        try
        {
            return valueOf(name.toUpperCase());
        }
        catch (IllegalArgumentException e)
        {
            return null;
        }
    }

    public static String[] valueNames()
    {
        AreaShape[] values = values();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++)
            names[i] = values[i].toString();
        return names;
    }

}