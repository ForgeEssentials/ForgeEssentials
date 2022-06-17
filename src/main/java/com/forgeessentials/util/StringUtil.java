package com.forgeessentials.util;

public class StringUtil
{
    public static <T> String toJsonString(T[] strings)
    {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < strings.length; i++)
        {
            if (i > 0)
            {
                sb.append(", ");
            }
            sb.append("\"");
            sb.append(strings[i]);
            sb.append("\"");
        }
        sb.append("]");
        return sb.toString();
    }
}
