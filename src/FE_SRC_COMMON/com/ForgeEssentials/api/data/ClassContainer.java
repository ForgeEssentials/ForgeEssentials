package com.ForgeEssentials.api.data;

import java.util.Arrays;

public class ClassContainer
{
	private final String	className;
	Class[]					parameters;

	public ClassContainer(Class type, Class... parameters)
	{
		className = type.getName();
		this.parameters = parameters;
	}

	public ClassContainer(Class type)
	{
		className = type.getName();
		this.parameters = new Class[] {};
	}

	public Class getType()
	{
		try
		{
			return Class.forName(className);
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public boolean isAssignableFrom(Class type)
	{
		return getType().isAssignableFrom(type);
	}

	public boolean isInterface()
	{
		return getType().isInterface();
	}

	public boolean isEnum()
	{
		return getType().isEnum();
	}

	public boolean isArray()
	{
		return getType().isArray();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Class)
		{
			return className.equals(((Class)obj).getName());
		}
		else if (obj instanceof ClassContainer)
		{
			return className.equals(((ClassContainer) obj).className) && Arrays.equals(parameters, ((ClassContainer) obj).parameters);
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
		return parameters.length > 0;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getType().getCanonicalName());
		builder.append('<');

		for (int i = 0; i < parameters.length; i++)
		{
			builder.append(parameters[i].getCanonicalName());
			if (i < parameters.length - 1)
				builder.append(", ");
		}

		builder.append(">");

		return builder.toString();
	}

}
