package com.ForgeEssentials.util.tasks;

public interface IThreadTask extends Runnable
{
	/**
	 * This will be called once every run loop.
	 */
	@Override
	public void run();
	
	/**
	 * Should return some name unique to this
	 * @return Some name unique to this ThreadTask. This name will be used to cancel the task when required.
	 */
	public String getName();
	
	/**
	 * this will be called if this thread is cancelled or the ThreadHandler is shutting down.
	 * DO NOT call the gc.
	 * @return
	 */
	public void die();
}
