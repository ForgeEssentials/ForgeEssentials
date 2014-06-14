package com.forgeessentials.client.util;

public class ClientSelection extends ClientAreaBase {
    // only really used for copying.. the points it was defined from.
    private ClientPoint start;    // start selection
    private ClientPoint end;    // end selection

    public ClientSelection(ClientPoint point1, ClientPoint point2)
    {
        super(point1, point2);
        start = ClientPoint.validate(point1);
        end = ClientPoint.validate(point2);
    }

    public ClientPoint getStart()
    {
        return start;
    }

    public ClientPoint getEnd()
    {
        return end;
    }

    public void setStart(ClientPoint start)
    {
        this.start = start;
        redefine(this.start, end);
    }

    public void setEnd(ClientPoint end)
    {
        this.end = end;
        redefine(start, this.end);
    }
}