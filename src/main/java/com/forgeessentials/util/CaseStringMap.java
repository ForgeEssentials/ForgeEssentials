package com.forgeessentials.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A Map implementation backed by a HashMap.
 * The only difference between this and a normal HashMap is that it can only use Strings as Keys.
 * All Strings used in this map are converted to lowercase.
 *
 * @param <V> Value type
 * @author AbrarSyed
 */
public class CaseStringMap<V> implements Map<String, V> {
    HashMap<String, V> map;

    public CaseStringMap()
    {
        map = new HashMap<String, V>();
    }

    @Override
    public void clear()
    {
        map.clear();
    }

    @Override
    public boolean containsKey(Object key)
    {
        return map.containsKey(key);
    }

    public boolean containsKey(String key)
    {
        key = key.toLowerCase();
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
        return map.containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<String, V>> entrySet()
    {
        return map.entrySet();
    }

    @Override
    public V get(Object key)
    {
        return map.get(key);
    }

    public V get(String key)
    {
        key = key.toLowerCase();
        return map.get(key);
    }

    @Override
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    @Override
    public Set<String> keySet()
    {
        return map.keySet();
    }

    @Override
    public V put(String key, V value)
    {
        key = key.toLowerCase();
        return map.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> col)
    {
        String temp;
        for (Entry<? extends String, ? extends V> entry : col.entrySet())
        {
            temp = entry.getKey().toLowerCase();
            map.put(temp, entry.getValue());
        }

    }

    @Override
    public V remove(Object key)
    {
        return map.remove(key);
    }

    public V remove(String key)
    {
        key = key.toLowerCase();
        return map.remove(key);
    }

    @Override
    public int size()
    {
        return map.size();
    }

    @Override
    public Collection<V> values()
    {
        return map.values();
    }

    @Override
    public boolean equals(Object obj)
    {
        return map.equals(obj);
    }

    @Override
    public int hashCode()
    {
        return map.hashCode();
    }

}
