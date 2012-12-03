package com.ForgeEssentials.permissions;

import java.util.ArrayList;

public class PromotionLadder
{
	public final String name;
	public final String zoneID;
	public ArrayList<String> ladder;
	
	public PromotionLadder(String name, String zoneID)
	{
		this.name = name;
		this.zoneID = zoneID;
		ladder = new ArrayList<String>();
	}
}
