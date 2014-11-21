package com.forgeessentials.data.api;

import com.forgeessentials.commons.IReconstructData;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class TypeEntryInfo implements ITypeInfo {
    private ClassContainer parent;
    private HashMap<String, ClassContainer> types;

    public TypeEntryInfo(HashMap<String, ClassContainer> types, ClassContainer parent)
    {
        this.parent = parent;
        this.types = types;
    }

    @Override
    public boolean canSaveInline()
    {
        return true;
    }

    @Override
    public void build()
    {
        // unnecessary
    }

    @Override
    public ClassContainer getTypeOfField(String field)
    {
        return types.get(field);
    }

    @Override
    public String[] getFieldList()
    {
        return types.keySet().toArray(new String[types.size()]);
    }

    @Override
    public TypeData getTypeDataFromObject(Object obj)
    {
        // no.. just no.
        // this will be handled internally by each MutiVal object
        return null;
    }

    @Override
    public Object reconstruct(IReconstructData data)
    {
        // do nothing.. this is a dummy class
        return data;
    }

    @Override
    public ClassContainer getType()
    {
        // why not? :) better than void.class .
        return new ClassContainer(Map.Entry.class);
    }

    public ClassContainer getParentType()
    {
        return parent;
    }

    @Override
    public Class[] getGenericTypes()
    {
        // prolly never will be used.
        return types.values().toArray(new Class[types.size()]);
    }

    @Override
    public ITypeInfo getInfoForField(String field)
    {
        return DataStorageManager.getInfoForType(getTypeOfField(field));
    }

}
