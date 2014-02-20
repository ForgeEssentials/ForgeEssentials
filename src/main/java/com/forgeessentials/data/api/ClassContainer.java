package com.forgeessentials.data.api;

import java.util.Arrays;

public class ClassContainer
{
	private final Class<?>	heldClass;
	Class<?>[]				parameters;

	public ClassContainer(Class<?> type, Class<?>... parameters)
	{
		heldClass = type;
		this.parameters = parameters;
		
		if (type.isArray())
			return;
	}

	public ClassContainer(Class<?> type)
	{
		heldClass = type;
		parameters = new Class[] {};
	}

	public Class<?> getType()
	{
		return heldClass;
	}

	public boolean isAssignableFrom(Class<?> type)
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
			return heldClass.getName().equals(((Class<?>) obj).getName());
		else if (obj instanceof ClassContainer)
			return heldClass.getName().equals(((ClassContainer) obj).heldClass.getName()) && Arrays.equals(parameters, ((ClassContainer) obj).parameters);
		else
			return false;
	}

	public Class<?>[] getParameters()
	{
		return parameters;
	}

	public boolean hasParameters()
	{
		return parameters.length > 0;
	}

	public String getName()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getType().getCanonicalName());
		builder.append('<');

		for (int i = 0; i < parameters.length; i++)
		{
			builder.append(parameters[i].getCanonicalName());
			if (i < parameters.length - 1)
			{
				builder.append(", ");
			}
		}

		builder.append(">");

		return builder.toString();
	}

	public String getSimpleName()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getType().getSimpleName());

		if (parameters.length > 0)
		{
			builder.append('<');

			for (int i = 0; i < parameters.length; i++)
			{
				builder.append(parameters[i].getSimpleName());
				if (i < parameters.length - 1)
				{
					builder.append(", ");
				}
			}

			builder.append(">");
		}

		return builder.toString();
	}

	public String getFileSafeName()
	{
		String temp = getSimpleName();
		temp = temp.replace('<', '$');
		temp = temp.replace('>', '$');
		temp = temp.replaceAll("\\, ", "_H_");
		temp = temp.replaceAll("\\[\\]", "_ARR_");
		return temp;
	}

	@Override
	public String toString()
	{
		return getName();
	}

}
