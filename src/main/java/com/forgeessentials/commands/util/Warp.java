package com.forgeessentials.commands.util;

import com.forgeessentials.data.api.IReconstructData;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.Reconstructor;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
import com.forgeessentials.data.api.SaveableObject.UniqueLoadingKey;
import com.forgeessentials.util.AreaSelector.WarpPoint;

@SaveableObject
public class Warp
{
	@UniqueLoadingKey
	@SaveableField
	private String		name;

	@SaveableField
	private WarpPoint	point;

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
	private static Warp reconstruct(IReconstructData tag)
	{
		return new Warp((String) tag.getFieldValue("name"), (WarpPoint) tag.getFieldValue("point"));
	}
}
