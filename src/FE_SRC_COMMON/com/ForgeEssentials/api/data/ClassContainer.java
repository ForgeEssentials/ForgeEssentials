package com.ForgeEssentials.api.data;

import java.util.Arrays;

public class ClassContainer
{
	public final Class type;
	Class[] parameters;

	public ClassContainer(Class type, Class... parameters)
	{
		this.type = type;
		this.parameters = parameters;
	}
	
	public ClassContainer(Class type)
	{
		this.type = type;
		this.parameters = new Class[] {};
	}
	
	public boolean isAssignableFrom(Class type)
	{
		return type.isAssignableFrom(this.type);
	}
	
	public boolean isInterface()
	{
		return type.isInterface();
	}
	
	public boolean isEnum()
	{
		return type.isEnum();
	}
	
	public boolean isArray()
	{
		return type.isArray();
	}
	
	public boolean isEqual(Object obj)
	{
		if (obj instanceof Class)
		{
			return type.getCanonicalName().equals(((Class) obj).getCanonicalName());
		}
		else if (obj instanceof ClassContainer)
		{
			return type.getCanonicalName().equals(((Class) obj).getCanonicalName()) && Arrays.equals(parameters, ((ClassContainer)obj).parameters);
		}
		else
			return false;
	}
	
	public Class[] getParameters()
	{
		return parameters;
	}
	
	public boolean hasParameters()
	{
		return parameters.length == 0;
	}

}
