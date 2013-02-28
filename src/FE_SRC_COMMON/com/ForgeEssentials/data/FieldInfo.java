package com.ForgeEssentials.data;

/**
 * Internal class for managing @SaveableField instances within a DataAdapter.
 * @author MysteriousAges
 */
class FieldInfo
{
	public Class	Type;
	public String	Name;

	// Default constructor for simples!
	public FieldInfo()
	{
	}

	public FieldInfo(Class type, String name)
	{
		Type = type;
		Name = name;
	}

	public boolean isPrimitive()
	{
		return Type.isPrimitive();
	}
}
