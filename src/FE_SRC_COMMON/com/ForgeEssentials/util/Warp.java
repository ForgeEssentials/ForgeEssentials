package com.ForgeEssentials.util;

import com.ForgeEssentials.api.data.ITaggedClass;
import com.ForgeEssentials.api.data.SaveableObject;
import com.ForgeEssentials.api.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.api.data.SaveableObject.SaveableField;
import com.ForgeEssentials.api.data.SaveableObject.UniqueLoadingKey;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

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
	private static Warp reconstruct(ITaggedClass tag)
	{
		return new Warp((String) tag.getFieldValue("name"), (WarpPoint) tag.getFieldValue("point"));
	}
}
