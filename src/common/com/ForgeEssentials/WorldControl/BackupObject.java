package com.ForgeEssentials.WorldControl;

public class BackupObject {
	public int X;
	public int Y;
	public int Z;
	public int metadata;
	public int blockID;
	
	public BackupObject(int x, int y, int z, int bid, int meta) {
		X=x;
		Y=y;
		Z=z;
		blockID=bid;
		metadata=meta;
	}
}
