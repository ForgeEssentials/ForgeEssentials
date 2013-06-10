package com.ForgeEssentials.permission.query;

import com.ForgeEssentials.permission.PermissionChecker;

public abstract class PermQuery
{
	public enum PermResult
	{
		ALLOW, DENY, PARTIAL, UNKNOWN
	}

	private PermResult			result			= PermResult.UNKNOWN;
	
	public PermissionChecker checker;


	public boolean				checkForward	= false;

	public PermQuery()
	{
		checkForward = false;
	}

	public PermQuery(boolean checkForward)
	{
		this.checkForward = checkForward;
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
