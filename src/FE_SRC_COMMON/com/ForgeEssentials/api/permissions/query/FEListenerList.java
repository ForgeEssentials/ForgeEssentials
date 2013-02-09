package com.ForgeEssentials.api.permissions.query;

import java.util.ArrayList;

import net.minecraftforge.event.EventPriority;

public class FEListenerList
{
	private static ArrayList<FEListenerList>	allLists	= new ArrayList<FEListenerList>();
	private static int							maxSize		= 0;

	private FEListenerList						parent;
	private FEListenerListInst[]				lists		= new FEListenerListInst[0];

	public FEListenerList()
	{
		allLists.add(this);
		resizeLists(maxSize);
	}

	public FEListenerList(FEListenerList parent)
	{
		allLists.add(this);
		this.parent = parent;
		resizeLists(maxSize);
	}

	public static void resize(int max)
	{
		if (max <= maxSize)
		{
			return;
		}
		for (FEListenerList list : allLists)
		{
			list.resizeLists(max);
		}
		maxSize = max;
	}

	public void resizeLists(int max)
	{
		if (parent != null)
		{
			parent.resizeLists(max);
		}

		if (lists.length >= max)
		{
			return;
		}

		FEListenerListInst[] newList = new FEListenerListInst[max];
		int x = 0;
		for (; x < lists.length; x++)
		{
			newList[x] = lists[x];
		}
		for (; x < max; x++)
		{
			if (parent != null)
			{
				newList[x] = new FEListenerListInst(parent.getInstance(x));
			}
			else
			{
				newList[x] = new FEListenerListInst();
			}
		}
		lists = newList;
	}

	public static void clearBusID(int id)
	{
		for (FEListenerList list : allLists)
		{
			list.lists[id].dispose();
		}
	}

	protected FEListenerListInst getInstance(int id)
	{
		return lists[id];
	}

	public IQueryListener[] getListeners(int id)
	{
		return lists[id].getListeners();
	}

	public void register(int id, EventPriority priority, IQueryListener listener)
	{
		lists[id].register(priority, listener);
	}

	public void unregister(int id, IQueryListener listener)
	{
		lists[id].unregister(listener);
	}

	public static void unregisterAll(int id, IQueryListener listener)
	{
		for (FEListenerList list : allLists)
		{
			list.unregister(id, listener);
		}
	}

	private class FEListenerListInst
	{
		private boolean									rebuild	= true;
		private IQueryListener[]						listeners;
		private ArrayList<ArrayList<IQueryListener>>	priorities;
		private FEListenerListInst						parent;

		private FEListenerListInst()
		{
			int count = EventPriority.values().length;
			priorities = new ArrayList<ArrayList<IQueryListener>>(count);

			for (int x = 0; x < count; x++)
			{
				priorities.add(new ArrayList<IQueryListener>());
			}
		}

		public void dispose()
		{
			for (ArrayList<IQueryListener> listeners : priorities)
			{
				listeners.clear();
			}
			priorities.clear();
			parent = null;
			listeners = null;
		}

		private FEListenerListInst(FEListenerListInst parent)
		{
			this();
			this.parent = parent;
		}

		/**
		 * Returns a ArrayList containing all listeners for this event, and all
		 * parent events for the specified priority.
		 * 
		 * The list is returned with the listeners for the children events
		 * first.
		 * 
		 * @param priority
		 * The Priority to get
		 * @return ArrayList containing listeners
		 */
		public ArrayList<IQueryListener> getListeners(EventPriority priority)
		{
			ArrayList<IQueryListener> ret = new ArrayList<IQueryListener>(priorities.get(priority.ordinal()));
			if (parent != null)
			{
				ret.addAll(parent.getListeners(priority));
			}
			return ret;
		}

		/**
		 * Returns a full list of all listeners for all priority levels.
		 * Including all parent listeners.
		 * 
		 * List is returned in proper priority order.
		 * 
		 * Automatically rebuilds the internal Array cache if its information is
		 * out of date.
		 * 
		 * @return Array containing listeners
		 */
		public IQueryListener[] getListeners()
		{
			if (shouldRebuild())
			{
				buildCache();
			}
			return listeners;
		}

		protected boolean shouldRebuild()
		{
			return rebuild || parent != null && parent.shouldRebuild();
		}

		/**
		 * Rebuild the local Array of listeners, returns early if there is no
		 * work to do.
		 */
		private void buildCache()
		{
			ArrayList<IQueryListener> ret = new ArrayList<IQueryListener>();
			for (EventPriority value : EventPriority.values())
			{
				ret.addAll(getListeners(value));
			}
			listeners = ret.toArray(new IQueryListener[0]);
			rebuild = false;
		}

		public void register(EventPriority priority, IQueryListener listener)
		{
			priorities.get(priority.ordinal()).add(listener);
			rebuild = true;
		}

		public void unregister(IQueryListener listener)
		{
			for (ArrayList<IQueryListener> list : priorities)
			{
				list.remove(listener);
			}
		}
	}
}
