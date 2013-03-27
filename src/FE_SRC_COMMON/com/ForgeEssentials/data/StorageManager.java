package com.ForgeEssentials.data;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.api.data.ClassContainer;
import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.data.EnumDriverType;
import com.ForgeEssentials.api.data.IDataDriver;
import com.ForgeEssentials.api.data.IStorageManager;
import com.ForgeEssentials.api.data.ITypeInfo;
import com.ForgeEssentials.api.data.SaveableObject;
import com.ForgeEssentials.api.data.TypeData;
import com.ForgeEssentials.data.typeInfo.TypeInfoArray;
import com.ForgeEssentials.data.typeInfo.TypeInfoList;
import com.ForgeEssentials.data.typeInfo.TypeInfoMap;
import com.ForgeEssentials.data.typeInfo.TypeInfoSerialize;
import com.ForgeEssentials.data.typeInfo.TypeInfoSet;
import com.ForgeEssentials.data.typeInfo.TypeInfoStandard;
import com.ForgeEssentials.util.DBConnector;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.OutputHandler;
import com.google.common.base.Throwables;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

@SuppressWarnings({"rawtypes", "unchecked"})
public class StorageManager implements IStorageManager
{
	// just keeps an instance of the config for future use.
	private Configuration													config;
	public static final EnumDriverType										defaultDriver	= EnumDriverType.TEXT;
	private EnumDriverType													chosen			= defaultDriver;
	private ConcurrentHashMap<EnumDriverType, String>						typeChosens;													// the defaults...
	private ConcurrentHashMap<String, Class<? extends AbstractDataDriver>>	classMap;														// registered ones...
	private ConcurrentHashMap<String, AbstractDataDriver>					instanceMap;													// instantiated ones
	private static StorageManager											instance;
	private ConcurrentHashMap<String, ITypeInfo>							taggerList		= new ConcurrentHashMap<String, ITypeInfo>();

	public StorageManager(Configuration config)
	{
		classMap = new ConcurrentHashMap<String, Class<? extends AbstractDataDriver>>();
		instanceMap = new ConcurrentHashMap<String, AbstractDataDriver>();
		typeChosens = new ConcurrentHashMap<EnumDriverType, String>();

		this.config = config;

		config.addCustomCategoryComment("Data", "Configuration options for how ForgeEssentials will save its data for persistence between sessions.");

		// generates the configs...
		Property prop = config.get("Data", "storageType", defaultDriver.toString());
		prop.comment = "Specifies the variety of data storage FE will use. Options: " + FunctionHelper.niceJoin(EnumDriverType.values());
		chosen = EnumDriverType.valueOf(prop.value);

		typeChosens.put(EnumDriverType.TEXT, "ForgeConfig");
		typeChosens.put(EnumDriverType.BINARY, "NBT");
		typeChosens.put(EnumDriverType.SQL, "SQL_DB");

		String cat;
		for (EnumDriverType type : EnumDriverType.values())
		{
			if (type == EnumDriverType.SQL)
			{
				continue;
			}
			cat = "Data." + type;
			prop = config.get(cat, "chosenDriver", typeChosens.get(type));
			typeChosens.put(type, prop.value);
		}

		instance = this;
	}

	/**
	 * Parses the ForgeEssentials config file and determines which Driver to
	 * use. This will be loaded up with the lazy method. only the chosen ones
	 * will be loaded...
	 * @param Config
	 */
	public void setupManager()
	{
		// verify default driver...
		if (classMap.get(typeChosens.get(defaultDriver)) == null)
			throw new RuntimeException("{ForgeEssentials} Default DataDriver is invalid! Valid types: " + Arrays.toString(classMap.values().toArray()));

		for (Entry<String, AbstractDataDriver> entry : instanceMap.entrySet())
		{
			try
			{
				// things MAY error here as well...
				entry.getValue().parseConfigs(config, "Data." + entry.getValue().getType() + "." + entry.getValue().getName());
			}
			catch (Exception e)
			{
				OutputHandler.info("Problem loading DataDriver " + entry.getKey());
				OutputHandler.info("ForgeEssentials will not be able to save any data through this driver");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Passes the ServerStart event to the dataDrivers
	 */
	public void serverStart(FMLServerStartingEvent event)
	{
		for (IDataDriver driver : instanceMap.values())
		{
			// things MAY error here as well...
			driver.serverStart(event);
		}
	}

	/**
	 * Should only be done before the server starts. May override existing
	 * Driver types.
	 * @param name Name to be used in configs
	 * @param c
	 */
	@Override
	public void registerDriver(String name, Class<? extends AbstractDataDriver> c)
	{
		try
		{
			// If there is a problem constructing the driver, this line will
			// fail and we will enter the catch block.
			AbstractDataDriver driver = c.newInstance();
			instance.classMap.put(name, c);
			instance.instanceMap.put(name, driver);
		}
		catch (Exception e)
		{
			OutputHandler.info("Problem initializing DataDriver " + name);
			OutputHandler.info("ForgeEssentials will not be able to save any data through this driver");
			e.printStackTrace();
		}
	}

	@Override
	public AbstractDataDriver getReccomendedDriver()
	{
		return getDriverOfType(chosen);
	}

	@Override
	public AbstractDataDriver getDriverOfType(EnumDriverType type)
	{
		return getDriverOfName(instance.typeChosens.get(type));
	}

	/**
	 * @param name
	 * @return default DataDriver if the requested one is unavailable.
	 */
	private AbstractDataDriver getDriverOfName(String name)
	{
		AbstractDataDriver d = instance.instanceMap.get(name);
		if (d == null)
		{
			d = instance.instanceMap.get(defaultDriver);
		}
		return d;
	}

	@Override
	public void registerSaveableClass(ClassContainer type)
	{
		ITypeInfo info = null;

		if (type.isArray() && !type.getType().getComponentType().isPrimitive() && !String.class.isAssignableFrom(type.getType().getComponentType()))
		{
			info = new TypeInfoArray(new ClassContainer(type.getType(), type.getType().getComponentType()));
		}
		else if (Map.class.isAssignableFrom(type.getType()))
		{
			info = new TypeInfoMap(type);
		}
		else if (Set.class.isAssignableFrom(type.getType()))
		{
			info = new TypeInfoSet(type);
		}
		else if (List.class.isAssignableFrom(type.getType()))
		{
			info = new TypeInfoList(type);
		}
		else if (type.getType().isAnnotationPresent(SaveableObject.class))
		{
			info = new TypeInfoStandard(type.getType());
		}
		else if (Serializable.class.isAssignableFrom(type.getType()))
		{
			info = new TypeInfoSerialize(type);
		}

		if (info == null)
			return;

		info.build();
		taggerList.put(type.toString(), info);
	}

	@Override
	public void registerSaveableClass(Class<? extends ITypeInfo> infoType, ClassContainer type)
	{
		if (infoType.equals(TypeInfoStandard.class))
		{
			registerSaveableClass(type);
		}
		else
		{
			try
			{
				Constructor[] constructors = infoType.getConstructors();
				Constructor<? extends ITypeInfo> con = null;

				int arg = -1;

				for (Constructor c : constructors)
				{
					if (c.getParameterTypes().length == 0)
					{
						con = c;
						arg = 0;
						break;
					}

					if (c.getParameterTypes().length == 1)
					{
						if (c.getParameterTypes()[0].equals(ClassContainer.class))
						{
							con = c;
							arg = 2;
							break;
						}

						if (c.getParameterTypes()[0].equals(Class.class))
						{
							con = c;
							arg = 1;
							break;
						}
					}
				}

				ITypeInfo created = null;

				if (con == null)
					throw new IllegalArgumentException(infoType.getCanonicalName() + " must have useable constructors! See the ITypeInfo documentation!");

				switch (arg)
					{
						case 0:
							created = con.newInstance();
							break;
						case 1:
							created = con.newInstance(type.getType());
							break;
						case 2:
							created = con.newInstance(type);
							break;
					}

				created.build();
				taggerList.put(type.toString(), created);
			}
			catch (SecurityException e)
			{
				OutputHandler.severe(infoType.getCanonicalName() + " must have useable constructors! See the ITypeInfo documentation!");
				Throwables.propagate(e);
			}
			catch (InstantiationException e)
			{
				OutputHandler.severe(infoType.getCanonicalName() + " must have useable constructors! See the ITypeInfo documentation!");
				Throwables.propagate(e);
			}
			catch (IllegalAccessException e)
			{
				OutputHandler.severe(infoType.getCanonicalName() + " must have useable constructors! See the ITypeInfo documentation!");
				Throwables.propagate(e);
			}
			catch (IllegalArgumentException e)
			{
				OutputHandler.severe(infoType.getCanonicalName() + " must have useable constructors! See the ITypeInfo documentation!");
				Throwables.propagate(e);
			}
			catch (InvocationTargetException e)
			{
				OutputHandler.severe(infoType.getCanonicalName() + " must have useable constructors! See the ITypeInfo documentation!");
				Throwables.propagate(e);
			}
		}
	}

	public boolean isClassRegisterred(ClassContainer type)
	{
		if (type == null)
			return false;
		return taggerList.containsKey(type.toString());
	}

	@Override
	public ITypeInfo getInfoForType(ClassContainer type)
	{
		ITypeInfo tagged = taggerList.get(type.toString());

		if (tagged != null)
			return tagged;

		ClassContainer tempType = type;

		while (!isClassRegisterred(tempType))
		{
			if (tempType == null)
			{
				break;
			}
			else if (tempType.hasParameters())
			{
				tempType = new ClassContainer(tempType.getType());
			}
			else if (tempType.getType().getSuperclass() != null)
			{
				tempType = new ClassContainer(tempType.getType().getSuperclass(), type.getParameters());
			}
			else if (tempType.getType().getSuperclass() == null)
			{
				tempType = null;
			}
		}

		if (tempType == null)
		{
			for (Class inter : type.getType().getInterfaces())
			{
				tempType = new ClassContainer(inter, type.getParameters());

				if (isClassRegisterred(tempType))
				{
					break;
				}

				tempType = new ClassContainer(inter);

				if (isClassRegisterred(tempType))
				{
					break;
				}

				tempType = null;
			}
		}

		if (tempType == null)
		{
			registerSaveableClass(type);
			tagged = taggerList.get(type.toString());
			return tagged;
		}

		return taggerList.get(tempType);
	}

	@Override
	public DBConnector getCoreDBConnector()
	{
		return ((SQLDataDriver) instance.getDriverOfType(EnumDriverType.SQL)).connector;
	}

	@Override
	public TypeData getDataForType(ClassContainer type)
	{
		return new TypeData(type);
	}

	@Override
	public TypeData getDataForObject(ClassContainer container, Object obj)
	{
		ITypeInfo info = getInfoForType(container);
		TypeData data = info.getTypeDataFromObject(obj);

		ITypeInfo tempInfo;
		for (Entry<String, Object> e : data.getAllFields())
		{
			if (e.getValue() != null && !(e.getValue() instanceof TypeData) && StorageManager.isTypeComplex(e.getValue().getClass()))
			{
				tempInfo = info.getInfoForField(e.getKey());
				data.putField(e.getKey(), DataStorageManager.getDataForObject(tempInfo.getTypeOfField(e.getKey()), e.getValue()));
			}
		}

		return data;
	}

	/**
	 * @param t class check
	 * @return True if TypeTagger must create a nested TaggedClass to allow DataDrivers to correctly save this type of object.
	 */
	public static boolean isTypeComplex(Class type)
	{
		boolean flag = true;
		if (type.isPrimitive() || type.equals(Integer.class) || type.equals(Float.class) ||
				type.equals(Double.class) || type.equals(Boolean.class) || type.equals(String.class) ||
				type.equals(Byte.class) || type.equals(Long.class))
		{
			flag = false;
		}
		else if (type.isArray())
			return !(type.getComponentType().isPrimitive() || type.getComponentType().equals(Integer.class) || type.getComponentType().equals(Float.class) ||
					type.getComponentType().equals(Double.class) || type.getComponentType().equals(Boolean.class) || type.getComponentType().equals(String.class) || type.equals(Byte.class));

		return flag;
	}

	/**
	 * @param t class check
	 * @return True if TypeTagger must create a nested TaggedClass to allow DataDrivers to correctly save this type of object.
	 */
	public static boolean isTypeComplex(ClassContainer type)
	{
		if (type.hasParameters())
			return true;
		else
			return isTypeComplex(type.getType());
	}
}
