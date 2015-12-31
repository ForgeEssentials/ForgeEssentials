package com.forgeessentials.core;

import java.util.List;

public class TestClass
{

    protected int intField;

    protected List<String> stringList;

    public int bar()
    {
        return 1;
    }

    public void test()
    {
        System.out.println("test() start");

        int foo = bar();
        System.out.println("test() value = " + foo);

        System.out.println("test() end");
    }

}
