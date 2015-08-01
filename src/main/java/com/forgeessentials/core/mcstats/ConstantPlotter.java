package com.forgeessentials.core.mcstats;

import com.forgeessentials.core.mcstats.Metrics.Plotter;

public class ConstantPlotter extends Plotter
{

    private int value;

    public ConstantPlotter(String name, int value)
    {
        super(name);
        this.value = value;
    }

    @Override
    public int getValue()
    {
        return value;
    }

    public void setValue(int value)
    {
        this.value = value;
    }

}
