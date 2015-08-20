package com.forgeessentials.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;

public class RingBuffer<T> implements List<T>, Queue<T>
{

    private ArrayList<T> data;

    private int position;

    public RingBuffer(int size)
    {
        if (size < 1)
            throw new IllegalArgumentException();
        data = new ArrayList<T>(size);
        for (int i = 0; i < size; i++)
            data.add(null);
    }

    private int mapIndex(int index)
    {
        int realIndex = position - index - 1;
        while (realIndex < 0)
            realIndex += data.size();
        while (realIndex >= data.size())
            realIndex -= data.size();
        return realIndex;
    }

    private int mapIndexReverse(int index)
    {
        int realIndex = position - index + 1;
        while (realIndex < 0)
            realIndex += data.size();
        while (realIndex >= data.size())
            realIndex -= data.size();
        return realIndex;
    }

    public ArrayList<T> getOrderedList(int maxElements)
    {
        ArrayList<T> list = new ArrayList<T>(data.size());
        int index = position;
        for (int i = 0; i < data.size(); i++)
        {
            if (list.size() >= maxElements)
                break;
            if (--index < 0)
                index = data.size() - 1;
            T element = data.get(index);
            if (element == null)
                break;
            list.add(element);
        }
        return list;
    }

    public ArrayList<T> getOrderedList()
    {
        return getOrderedList(Integer.MAX_VALUE);
    }

    @Override
    public T peek()
    {
        return data.get(mapIndex(0));
    }

    @Override
    public T element()
    {
        return peek();
    }

    @Override
    public boolean offer(T element)
    {
        data.set(position, element);
        if (++position >= data.size())
            position = 0;
        return true;
    }

    @Override
    public boolean add(T element)
    {
        return offer(element);
    }

    @Override
    public T poll()
    {
        if (--position < 0)
            position = data.size() - 1;
        return data.get(position);
    }

    @Override
    public T remove()
    {
        return poll();
    }

    @Override
    public void clear()
    {
        data = new ArrayList<>(data.size());
    }

    @Override
    public T get(int index)
    {
        return data.get(mapIndex(index));
    }

    @Override
    public int indexOf(Object o)
    {
        int index = data.indexOf(o);
        if (index < 0)
            return index;
        return mapIndexReverse(index);
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public Iterator<T> iterator()
    {
        return listIterator();
    }

    @Override
    public ListIterator<T> listIterator()
    {
        return listIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(final int startIndex)
    {
        return new ListIterator<T>() {

            private int index = startIndex;

            @Override
            public boolean hasNext()
            {
                return index < data.size();
            }

            @Override
            public T next()
            {
                return get(index++);
            }

            @Override
            public boolean hasPrevious()
            {
                return index > 0;
            }

            @Override
            public T previous()
            {
                return get(--index);
            }

            @Override
            public int nextIndex()
            {
                return index;
            }

            @Override
            public int previousIndex()
            {
                return index - 1;
            }

            @Override
            public void remove()
            {
                RingBuffer.this.set(index, null);
            }

            @Override
            public void set(T element)
            {
                RingBuffer.this.set(index, element);
            }

            @Override
            public void add(T element)
            {
                RingBuffer.this.set(index++, element);
            }

        };
    }

    @Override
    public boolean remove(Object o)
    {
        int index = indexOf(o);
        if (index < 0)
            return false;
        remove(index);
        return true;
    }

    @Override
    public T remove(int index)
    {
        int realIndex = mapIndex(index);
        T element = data.get(realIndex);
        data.set(realIndex, null);
        return element;
    }

    @Override
    public T set(int index, T element)
    {
        return data.set(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection)
    {
        for (T element : collection)
            add(element);
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> collection)
    {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection)
    {
        return data.containsAll(collection);
    }

    @Override
    public boolean removeAll(Collection<?> collection)
    {
        for (Object element : collection)
            remove(element);
        return true;
    }

    @Override
    public int size()
    {
        return data.size();
    }

    @Override
    public boolean contains(Object o)
    {
        return data.contains(o);
    }

    @Override
    public void add(int index, T element)
    {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public int lastIndexOf(Object o)
    {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public boolean retainAll(Collection<?> collection)
    {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex)
    {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public Object[] toArray()
    {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public <X> X[] toArray(X[] a)
    {
        throw new RuntimeException("Operation not supported");
    }

}
