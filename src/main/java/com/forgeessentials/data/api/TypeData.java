package com.forgeessentials.data.api;

import com.forgeessentials.commons.IReconstructData;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class TypeData implements IReconstructData, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -6666385032827386610L;
    private final ClassContainer type;
    private String uniqueKey;
    private HashMap<String, Object> members;

    public TypeData(ClassContainer type)
    {
        this.type = type;
        members = new HashMap<String, Object>();
    }

    public void putField(String name, Object value)
    {
        members.put(name, value);
    }

    @Override
    public Object getFieldValue(String name)
    {
        return members.get(name);
    }

    public boolean hasField(String field)
    {
        return members.keySet().contains(field);
    }

    public Set<Entry<String, Object>> getAllFields()
    {
        return members.entrySet();
    }

    @Override
    public Collection<?> getAllValues()
    {
        return members.values();
    }

    public void setUniqueKey(String key)
    {
        uniqueKey = key;
    }

    @Override
    public String getUniqueKey()
    {
        return uniqueKey;
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder("{");
        s.append("type=").append(getType().getCanonicalName()).append(", ");
        s.append("unique=").append(uniqueKey).append(", ");

        s.append("[");
        for (Entry<String, Object> field : getAllFields())
        {
            s.append(field.getKey()).append("=").append(field.getValue()).append(", ");
        }
        s.replace(s.length() - 2, s.length(), "]");

        s.append("}");

        return s.toString();
    }

    @Override
    public Class<?> getType()
    {
        return type.getType();
    }

    public ClassContainer getContainer()
    {
        return type;
    }
}
