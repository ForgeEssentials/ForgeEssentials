package com.ForgeEssentials.commands.util;

import com.ForgeEssentials.api.data.IReconstructData;
import com.ForgeEssentials.api.data.SaveableObject;
import com.ForgeEssentials.api.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.api.data.SaveableObject.SaveableField;
import com.ForgeEssentials.api.data.SaveableObject.UniqueLoadingKey;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

@SaveableObject
public class PWarp
{
    public static final String SEPERATOR = "#";
    
	@UniqueLoadingKey
	@SaveableField
	private String		name;

	@SaveableField
	private WarpPoint	point;

	public PWarp(String username, String name, WarpPoint point)
	{
		this.name = username + SEPERATOR + name;
		this.point = point;
	}

	public String getFilename()
	{
		return name;
	}

	public String getUsername()
	{
		return name.split(SEPERATOR)[0];
	}

	public String getName()
	{
		return name.split(SEPERATOR)[1];
	}

	public WarpPoint getPoint()
	{
		return point;
	}

	@Reconstructor
	private static PWarp reconstruct(IReconstructData tag)
	{
		return new PWarp(((String) tag.getFieldValue("name")).split(SEPERATOR)[0], ((String) tag.getFieldValue("name")).split(SEPERATOR)[1], (WarpPoint) tag.getFieldValue("point"));
	}
}
