package com.forgeessentials.playerlogger;

import java.sql.Blob;

/**
 * This is a data class, used for easy list making. Don't use for anything else.
 *
 * @author Dries007
 */
public class BlockChange {
	
	private int X, Y, Z, dim;
	private int type;
	private String block;
	private Blob data;

	public BlockChange(int X, int Y, int Z, int dim, int type, String block, Blob data)
	{
		this.X = X;
		this.Y = Y;
		this.Z = Z;
		this.dim = dim;
		this.type = type;
		this.block = block;
		this.data = data;
	}

	@Override
	public String toString()
	{
		return "[" + dim + "; " + X + "; " + Y + "; " + "Z" + "; " + type + "; " + block + "; " + data + "]";
	}

	public int getX()
	{
		return X;
	}

	public int getY()
	{
		return Y;
	}

	public int getZ()
	{
		return Z;
	}

	public int getDimension()
	{
		return dim;
	}

	public int getType()
	{
		return type;
	}

	public String getBlock()
	{
		return block;
	}
	


	public Blob getData()
	{
		return data;
	}
}
