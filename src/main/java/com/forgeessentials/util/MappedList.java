package com.forgeessentials.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class MappedList<TIn, TOut> implements List<TOut>
{

    protected List<TIn> list;

    protected List<TOut> mapped;

    protected abstract TOut map(TIn in);

    protected abstract TIn unmap(TOut in);

    public MappedList(List<TIn> list)
    {
        this.list = list;
        this.mapped = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++)
            this.mapped.add(null);
    }

    public void mapAll()
    {
        for (int i = 0; i < list.size(); i++)
        {
            if (mapped.get(i) == null)
            {
                mapped.set(i, map(list.get(i)));
            }
        }
    }

    @Override
    public int size()
    {
        return list.size();
    }

    @Override
    public boolean isEmpty()
    {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
        mapAll();
        return mapped.contains(o);
    }

    @Override
    public Iterator<TOut> iterator()
    {
        mapAll();
        return mapped.iterator();
    }

    @Override
    public Object[] toArray()
    {
        mapAll();
        return mapped.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        mapAll();
        return mapped.toArray(a);
    }

    @Override
    public void clear()
    {
        list.clear();
        mapped.clear();
    }

    @Override
    public TOut get(int index)
    {
        TOut value = mapped.get(index);
        if (value == null)
        {
            value = map(list.get(index));
            mapped.set(index, value);
        }
        return value;
    }

    @Override
    public TOut set(int index, TOut element)
    {
        TIn m = unmap(element);
        list.set(index, m);
        return mapped.set(index, element);
    }

    @Override
    public boolean add(TOut e)
    {
        TIn m = unmap(e);
        mapped.add(e);
        return list.add(m);
    }

    @Override
    public void add(int index, TOut element)
    {
        TIn m = unmap(element);
        mapped.add(index, element);
        list.add(index, m);
    }

    @Override
    public TOut remove(int index)
    {
        list.remove(index);
        return mapped.remove(index);
    }

    @Override
    public boolean remove(Object o)
    {
        mapAll();
        int index = mapped.indexOf(o);
        // if (index < 0)
        // index = list.indexOf(o);
        if (index < 0)
            return false;
        list.remove(index);
        mapped.remove(index);
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        throw new IllegalStateException();
    }

    @Override
    public boolean addAll(Collection<? extends TOut> c)
    {
        throw new IllegalStateException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends TOut> c)
    {
        throw new IllegalStateException();
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        throw new IllegalStateException();
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        throw new IllegalStateException();
    }

    @Override
    public int indexOf(Object o)
    {
        mapAll();
        return mapped.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o)
    {
        mapAll();
        return mapped.lastIndexOf(o);
    }

    @Override
    public ListIterator<TOut> listIterator()
    {
        mapAll();
        return mapped.listIterator();
    }

    @Override
    public ListIterator<TOut> listIterator(int index)
    {
        mapAll();
        return mapped.listIterator(index);
    }

    @Override
    public List<TOut> subList(int fromIndex, int toIndex)
    {
        mapAll();
        return mapped.subList(fromIndex, toIndex);
    }

}
