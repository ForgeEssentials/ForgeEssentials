package com.ForgeEssentials.api.permissions.query;

import com.ForgeEssentials.permission.PermissionChecker;

public class PermQuery
{
	public enum PermResult
	{
		ALLOW, DENY, PARTIAL, UNKNOWN
	}

	private PermResult				result			= PermResult.UNKNOWN;

	public PermissionChecker		checker;
	public boolean					checkForward	= false;

	public PermQuery()
	{
		checkForward = false;
	}

	public PermResult getResult()
	{
		return result;
	}

	public void setResult(PermResult result)
	{
		this.result = result;
	}

	public boolean isAllowed()
	{
		return result.equals(PermResult.ALLOW);
	}
}
