package com.forgeessentials.playerLogger;

import java.sql.Blob;
/**
 * This is a data class, used for easy list making.
 * Don't use for anything else.
 * @author Dries007
 *
 */
public class blockChange
{
    public int X, Y, Z, dim;
    public int type;
    public String block;
    public Blob te;
    
    
    public blockChange(int X, int Y, int Z, int dim, int type, String block, Blob te)
    {
        this.X = X;
        this.Y = Y;
        this.Z = Z;
        this.dim = dim;
        this.type = type;
        this.block = block;
        this.te = te;
    }
    
    @Override
    public String toString()
    {
        return "["+ dim + "; " + X + "; " + Y + "; " + "Z" + "; " + type + "; " + block + "; " + te + "]";
    }
}
