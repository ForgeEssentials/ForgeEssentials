package com.forgeessentials.data.typeInfo;

import com.forgeessentials.commons.IReconstructData;
import com.forgeessentials.commons.SaveableObject.UniqueLoadingKey;
import com.forgeessentials.data.StorageManager;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.data.api.ITypeInfo;
import com.forgeessentials.data.api.TypeData;
import com.forgeessentials.util.OutputHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.logging.Level;

@SuppressWarnings("rawtypes")
public class TypeInfoSerialize<T> implements ITypeInfo<T> {
    private final ClassContainer container;
    private HashMap<String, ClassContainer> fields;
    String uniqueKey;
    boolean hasUniqueKey = false;
    boolean isUniqueKeyField;

    public TypeInfoSerialize(ClassContainer container)
    {
        this.container = container;
        fields = new HashMap<String, ClassContainer>();

    }

    @Override
    public boolean canSaveInline()
    {
        return true;
    }

    @Override
    public void build()
    {
        Class<?> currentType = container.getType();
        Class<?> tempType;
        Type aTempType;
        ClassContainer tempContainer;

        do
        {
            // Locate all members that are saveable.
            for (Field f : currentType.getDeclaredFields())
            {
                // if its a saveable field
                if (!Modifier.isTransient(f.getModifiers()) && !Modifier.isStatic(f.getModifiers()))
                {
                    tempType = f.getType();
                    aTempType = f.getGenericType();
                    if (aTempType instanceof ParameterizedType)
                    {
                        Type[] types = ((ParameterizedType) aTempType).getActualTypeArguments();
                        Class[] params = new Class[types.length];
                        for (int i = 0; i < types.length; i++)
                        {
                            if (types[i] instanceof Class)
                            {
                                params[i] = (Class<?>) types[i];
                            }
                            else if (types[i] instanceof ParameterizedType)
                            {
                                params[i] = (Class<?>) ((ParameterizedType) types[i]).getRawType();
                            }
                        }

                        tempContainer = new ClassContainer(tempType, params);
                        fields.put(f.getName(), tempContainer);
                    }
                    else
                    {
                        tempContainer = new ClassContainer(tempType);
                        fields.put(f.getName(), tempContainer);
                    }
                }

                // check for UniqueKey
                if (f.isAnnotationPresent(UniqueLoadingKey.class))
                {
                    if (uniqueKey != null)
                    {
                        throw new RuntimeException("Each class may only have 1 UniqueLoadingKey");
                    }
                    if (!f.getType().isPrimitive() && !f.getType().equals(String.class))
                    {
                        throw new RuntimeException("The UniqueLoadingKey must be a primitive or a string");
                    }

                    isUniqueKeyField = true;
                    uniqueKey = f.getName();
                    hasUniqueKey = true;
                }

            }
        }
        while ((currentType = currentType.getSuperclass()) != null);

        // find reconstructor method
        for (Method m : container.getType().getDeclaredMethods())
        {
            // catches the UniqueLoadingKey method variant
            if (m.isAnnotationPresent(UniqueLoadingKey.class))
            {
                if (uniqueKey != null)
                {
                    throw new RuntimeException("Each class may only have 1 UniqueLoadingKey");
                }

                if (m.getParameterTypes().length > 0)
                {
                    new RuntimeException("The reconstructor method must have no paremeters");
                }

                if (!m.getReturnType().isPrimitive() && !m.getReturnType().equals(String.class))
                {
                    throw new RuntimeException("The UniqueLoadingKey method must return a primitive or a string");
                }

                uniqueKey = m.getName();
                isUniqueKeyField = false;
                hasUniqueKey = true;
            }
        }
    }

    @Override
    public ClassContainer getTypeOfField(String field)
    {
        if (field == null)
        {
            return null;
        }

        return fields.get(field);
    }

    @Override
    public String[] getFieldList()
    {
        return fields.keySet().toArray(new String[fields.size()]);
    }

    @Override
    public TypeData getTypeDataFromObject(T obj)
    {
        Class<?> c = obj.getClass();
        TypeData data = DataStorageManager.getDataForType(container);
        Field f;
        Object temp;

        // do Unique key stuff
        try
        {
            if (isUniqueKeyField && hasUniqueKey)
            {
                f = c.getDeclaredField(uniqueKey);
                f.setAccessible(true);
                data.setUniqueKey(f.get(obj).toString());
            }
            else if (hasUniqueKey)
            {
                Method m;
                m = c.getDeclaredMethod(uniqueKey, new Class[] { });
                m.setAccessible(true);
                Object val = m.invoke(obj, new Object[] { });
                data.setUniqueKey(val.toString());

            }
            else
            {
                data.setUniqueKey(obj.toString());
            }
        }
        catch (Exception e)
        {
            OutputHandler.exception(Level.SEVERE,
                    "Reflection error trying to get UniqueLoadingKey from " + obj.getClass() + ". FE will continue without saving this.", e);
        }

        String[] keys = fields.keySet().toArray(new String[fields.size()]);
        Class<?> currentClass = c;
        // Iterate over the object grabbing the fields we want to examine.
        for (int i = 0; i < keys.length; ++i)
        {
            try
            {
                f = currentClass.getDeclaredField(keys[i]);
                f.setAccessible(true);
                temp = f.get(obj);

                if (temp != null)
                {
                    if (StorageManager.isTypeComplex(temp.getClass()))
                    {
                        // This object is not a primitive. Call this function on the appropriate TypeTagger.
                        temp = DataStorageManager.getDataForObject(fields.get(keys[i]), temp);
                    }
                    data.putField(keys[i], temp);
                }
                // Ensure we reset the currentClass after trying this. It may have been altered by a previous attempt.
                currentClass = c;
            }
            catch (NoSuchFieldException e)
            {
                // Try again with a parent class.
                currentClass = currentClass.getSuperclass();
                if (currentClass == null)
                {
                    // Unless this happens. (Note: This shouldn't happen.)
                    OutputHandler.exception(Level.SEVERE, "Reflection error trying to save " + obj.getClass() + ". FE will continue without saving this.", e);
                }
                --i;
            }
            catch (Exception e)
            {
                // This... Should not happen. Unless something stupid.
                OutputHandler.exception(Level.SEVERE, "Reflection error trying to save " + obj.getClass() + ". FE will continue without saving this.", e);
            }
        }

        return data;
    }

    @Override
    public T reconstruct(IReconstructData data)
    {
        try
        {
            Object obj = container.getType().newInstance();
            Class<?> currentType = data.getType();

            do
            {
                // Locate all members that are saveable.
                for (Field f : currentType.getDeclaredFields())
                {
                    // if its a saveable field
                    if (!Modifier.isTransient(f.getModifiers()) && !Modifier.isStatic(f.getModifiers()))
                    {
                        f.set(obj, data.getFieldValue(f.getName()));
                    }

                }
            }
            while ((currentType = currentType.getSuperclass()) != null);

        }
        catch (Throwable thrown)
        {
            OutputHandler.exception(Level.SEVERE, "Error loading " + data.getType() + " with name " + data.getUniqueKey(), thrown);
        }

        return null;
    }

    @Override
    public ClassContainer getType()
    {
        return container;
    }

    @Override
    public Class<?>[] getGenericTypes()
    {
        return container.getParameters();
    }

    @Override
    public ITypeInfo<?> getInfoForField(String field)
    {
        return DataStorageManager.getInfoForType(getTypeOfField(field));
    }

}
