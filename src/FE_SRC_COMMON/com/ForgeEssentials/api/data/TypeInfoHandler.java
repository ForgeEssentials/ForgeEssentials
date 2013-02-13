package com.ForgeEssentials.api.data;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.ForgeEssentials.util.OutputHandler;

/**
 * Responsible for transforming a particular type (class) into a TaggedClass for
 * easier storage via DataDrivers, and eventually converts a TaggedClass into
 * the original Object (More or less.)
 * @author MysteriousAges
 */
public final class TypeInfoHandler
{
	public final ITypeInfo info;
	HashMap<String, Class> fieldToType;
	
	public TypeInfoHandler(ITypeInfo info)
	{
		this.info = info;
		fieldToType = new HashMap<String, Class>();
	}
	
	public void build()
	{
		info.build(fieldToType);
	}
	
	public Class getTypeOfField(String field)
	{
		return fieldToType.get(field);
	}


	/**
	 * Reconstructs the object from its TaggedClass.
	 * This recursively ensures that no TypeInfo will ever have to reconstruct its own fields.
	 * @param data
	 * @return
	 */
	public Object createFromFields(TypeData data)
	{
		Object val;
		// loops through all fields of this class.
		for (Entry<String, Object> entry : data.getAllFields())
		{
			// if it needs reconstructing before this class...
			if (entry.getValue() instanceof TypeData)
			{
				// reconstruct the class...
				val = DataStorageManager.getHandlerForType(getTypeOfField(entry.getKey())).createFromFields((TypeData) entry.getValue());
				
				// re-add it to the map.
				data.putField(entry.getKey(), val);
			}
		}

		// actually reconstruct this class
		val = info.reconstruct(data);

		// return the reconstructed value.
		return val;
	}
}
