package com.ForgeEssentials.api.data;

public abstract class IMultiValInfo<T> implements ITypeInfo<T>
{
	@Override
	public final boolean canSaveInline()
	{
		return false;
	}

	/**
	 * This shouldn't be any longer than 2 for Maps, and 1 for anything else.
	 * @return
	 */
	@Override
	public abstract Class<?>[] getGenericTypes();
}
