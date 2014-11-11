package com.forgeessentials.data.typeInfo;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.commons.IReconstructData;
import com.forgeessentials.data.api.TypeData;
import com.forgeessentials.data.api.TypeMultiValInfo;
import com.forgeessentials.util.OutputHandler;

@SuppressWarnings("rawtypes")
public class TypeInfoSet extends TypeMultiValInfo {
    public static final String POS = "ElementPos";
    public static final String ELEMENT = "Element";

    public TypeInfoSet(ClassContainer container)
    {
        super(container);
    }

    @Override
    public void buildEntry(HashMap<String, ClassContainer> fields)
    {
        fields.put(POS, new ClassContainer(int.class));
        fields.put(ELEMENT, new ClassContainer(container.getParameters()[0]));
    }

    @Override
    public Set<TypeData> getTypeDatasFromObject(Object obj)
    {
        HashSet<TypeData> datas = new HashSet<TypeData>();

        Set set = (Set) obj;

        Iterator itt = set.iterator();

        TypeData data;
        int i = 0;
        Object temp;
        while (itt.hasNext())
        {
            temp = itt.next();
            data = getEntryData();
            data.putField(POS, i);
            data.putField(ELEMENT, temp);
            datas.add(data);
            i++;
        }

        return datas;
    }

    @Override
    public Object reconstruct(TypeData[] data, IReconstructData rawType)
    {
        Object array = Array.newInstance(container.getType(), data.length);

        for (TypeData dat : data)
        {
            Array.set(array, (Integer) dat.getFieldValue(POS), dat.getFieldValue(ELEMENT));
        }

        Set set = new HashSet(data.length);
        try
        {
            set = (Set) container.getType().newInstance();
        }
        catch (Exception e)
        {
            OutputHandler.exception(Level.SEVERE, "Error instantiating " + container.getType().getCanonicalName() + "!", e);
            return null;
        }

        for (int i = 0; i < data.length; i++)
        {
            set.add(Array.get(array, i));
        }

        return set;
    }

}
