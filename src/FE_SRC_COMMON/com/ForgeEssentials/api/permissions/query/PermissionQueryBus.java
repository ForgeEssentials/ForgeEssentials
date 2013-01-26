package com.ForgeEssentials.api.permissions.query;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.reflect.TypeToken;

public class PermissionQueryBus
{
	private static int maxID = 0;

	private ConcurrentHashMap<Object, ArrayList<IQueryListener>> listeners = new ConcurrentHashMap<Object, ArrayList<IQueryListener>>();
	private final int busID = maxID++;

	public PermissionQueryBus()
	{
		FEListenerList.resize(busID + 1);
	}

	public void register(Object target)
	{
		Set<? extends Class<?>> supers = TypeToken.of(target.getClass()).getTypes().rawTypes();
		for (Method method : target.getClass().getMethods())
		{
			for (Class<?> cls : supers)
			{
				try
				{
					Method real = cls.getDeclaredMethod(method.getName(), method.getParameterTypes());
					if (real.isAnnotationPresent(PermSubscribe.class))
					{
						Class<?>[] parameterTypes = method.getParameterTypes();
						if (parameterTypes.length != 1)
						{
							throw new IllegalArgumentException("Method " + method + " has @PermSubscribe annotation, but requires " + parameterTypes.length
									+ " arguments.  PermQuery handler methods must require a single argument.");
						}

						Class<?> eventType = parameterTypes[0];

						if (!PermQuery.class.isAssignableFrom(eventType))
						{
							throw new IllegalArgumentException("Method " + method
									+ " has @PermSubscribe annotation, but takes a argument that is not a PermQuery " + eventType);
						}

						register(eventType, target, method);
						break;
					}
				}
				catch (NoSuchMethodException e)
				{
					;
				}
			}
		}
	}

	private void register(Class<?> eventType, Object target, Method method)
	{
		try
		{
			Constructor<?> ctr = eventType.getConstructor();
			ctr.setAccessible(true);
			PermQuery query = (PermQuery) ctr.newInstance();
			ASMQueryHandler listener = new ASMQueryHandler(target, method);
			query.getListenerList().register(busID, listener.getPriority(), listener);

			ArrayList<IQueryListener> others = listeners.get(target);
			if (others == null)
			{
				others = new ArrayList<IQueryListener>();
				listeners.put(target, others);
			}
			others.add(listener);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void unregister(Object object)
	{
		ArrayList<IQueryListener> list = listeners.remove(object);
		for (IQueryListener listener : list)
		{
			FEListenerList.unregiterAll(busID, listener);
		}
	}

	public boolean post(PermQuery query)
	{
		IQueryListener[] listeners = query.getListenerList().getListeners(busID);
		for (IQueryListener listener : listeners)
		{
			listener.invoke(query);
		}
		return query.isAllowed();
	}
}
