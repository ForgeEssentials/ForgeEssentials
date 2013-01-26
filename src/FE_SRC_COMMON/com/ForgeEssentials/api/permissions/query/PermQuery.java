package com.ForgeEssentials.api.permissions.query;

import com.ForgeEssentials.permission.PermissionChecker;

public class PermQuery
{
	public enum PermResult
	{
		ALLOW, DENY, PARTIAL, UNKNOWN
	}

	private PermResult result = PermResult.UNKNOWN;
	private static FEListenerList listeners = new FEListenerList();

	public PermissionChecker checker;
	public boolean checkForward = false;
	public boolean dOverride = false;

	public PermQuery()
	{
		setup();
		checkForward = false;
	}

	/**
	 * Returns a FEListenerList object that contains all listeners that are registered to this event.
	 * 
	 * @return Listener List
	 */
	public FEListenerList getListenerList()
	{
		return listeners;
	}

	public PermResult getResult()
	{
		return result;
	}

	/**
	 * Called by the base constructor, this is used by ASM generated event classes to setup various functionality such as the listener's list.
	 */
	protected void setup()
	{
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
