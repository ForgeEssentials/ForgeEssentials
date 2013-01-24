package com.ForgeEssentials.util;

/**
 * Represents a pair of values. Convenience class more than anything.
 * 
 * @author MysteriousAges
 * 
 * @param <A> First type
 * @param <B> Second type
 */
public class Pair<A, B>
{
	private A	left;
	private B	right;

	public Pair(A first, B second)
	{
		this.left = first;
		this.right = second;
	}

	public void setFirst(A first)
	{
		this.left = first;
	}

	public A getFirst()
	{
		return this.left;
	}

	public void setSecond(B second)
	{
		this.right = second;
	}

	public B getSecond()
	{
		return this.right;
	}

	@Override
	public String toString()
	{
		return this.left + "=>" + this.right;
	}

	@Override
	public int hashCode()
	{
		// Hash is an exclusive-OR of both hashes.
		return left.hashCode() ^ right.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		boolean flag = false;
		if (o instanceof Pair)
		{
			flag = (left.equals(((Pair) o).getFirst()) && right.equals(((Pair) o).getSecond()));
		}
		return flag;
	}
}
