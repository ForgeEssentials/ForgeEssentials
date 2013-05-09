package com.ForgeEssentials.playerLogger;

import java.sql.Blob;

public class blockChange
{
    public int X, Y, Z, dim;
    public boolean place;
    public String block;
    public Blob te;
    
    public blockChange(int X, int Y, int Z, int dim, boolean place, String block, Blob te)
    {
        this.X = X;
        this.Y = Y;
        this.Z = Z;
        this.dim = dim;
        this.place = place;
        this.block = block;
        this.te = te;
    }
    
    @Override
    public String toString()
    {
        return "["+ dim + "; " + X + "; " + Y + "; " + "Z" + "; " + place + "; " + block + "; " + te + "]";
    }
}
