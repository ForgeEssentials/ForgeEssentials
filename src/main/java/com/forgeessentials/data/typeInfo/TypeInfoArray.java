package com.forgeessentials.data.typeInfo;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.IReconstructData;
import com.forgeessentials.data.api.TypeData;
import com.forgeessentials.data.api.TypeMultiValInfo;

public class TypeInfoArray extends TypeMultiValInfo {
    public static final String POS = "ElementPos";
    public static final String ELEMENT = "Element";
    public static final String LENGTH = "length";

    public TypeInfoArray(ClassContainer container)
    {
        super(container);
    }

    @Override
    public void buildEntry(HashMap<String, ClassContainer> entryFields)
    {
        entryFields.put(POS, new ClassContainer(int.class));
        entryFields.put(ELEMENT, new ClassContainer(container.getType().getComponentType()));
    }

    @Override
    public void build(HashMap<String, ClassContainer> fields)
    {
        fields.put(LENGTH, new ClassContainer(int.class));
    }

    @Override
    public Set<TypeData> getTypeDatasFromObject(Object obj)
    {
        HashSet<TypeData> datas = new HashSet<TypeData>();

        Object[] array = (Object[]) obj;

        int i = 0;
        TypeData data;
        for (Object element : array)
        {
            if (element == null)
            {
                continue;
            }

            data = getEntryData();
            data.putField(POS, i);
            data.putField(ELEMENT, element);
            datas.add(data);
        }

        return datas;
    }

    @Override
    public void addExtraDataForObject(TypeData data, Object obj)
    {
        Object[] array = (Object[]) obj;
        data.putField(LENGTH, array.length);
    }

    @Override
    public Object reconstruct(TypeData[] data, IReconstructData rawData)
    {
        int pos = 0;
        int size = (Integer) rawData.getFieldValue(LENGTH);
        Object array = Array.newInstance(container.getType().getComponentType(), size);

        for (TypeData dat : data)
        {
            Array.set(array, pos++, dat.getFieldValue(ELEMENT));
        }

        return array;
    }
}
