package com.ForgeEssentials.util;

import java.util.HashMap;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.data.SaveableObject;
import com.ForgeEssentials.data.TaggedClass;
import com.ForgeEssentials.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.data.SaveableObject.SaveableField;
import com.ForgeEssentials.data.SaveableObject.UniqueLoadingKey;
import com.ForgeEssentials.permission.Zone;
import com.ForgeEssentials.util.AreaSelector.Point;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

@SaveableObject
public class Warp 
{
	@UniqueLoadingKey
	@SaveableField
	private String name;
	
	@SaveableField
	private WarpPoint point;
	
	public Warp(String name, WarpPoint point)
	{
		this.name = name;
		this.point = point;
	}
	
	public String getName() 
	{
		return name;
	}
	
	public WarpPoint getPoint()
	{
		return point;
	}
	
	@Reconstructor
	private void reconstruct(TaggedClass tag)
	{
		System.out.println("Called");
		name = (String) tag.getFieldValue("name");
		point = (WarpPoint) tag.getFieldValue("point");
	}
}
