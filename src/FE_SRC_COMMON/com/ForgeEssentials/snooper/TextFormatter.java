package com.ForgeEssentials.snooper;

import java.util.HashMap;
import java.util.Map.Entry;

public class TextFormatter 
{
	public static String toJSON(HashMap<String, String> data)
	{
		if(data.isEmpty())
		{
			return "{}";
		}
		String toSend = "{";
		
        for(Entry<String, String> set : data.entrySet())
    	{
        	
        	if(set.getValue().contains("{"))
        	{
        		toSend += "\"" + set.getKey() + "\": " + set.getValue() + ", ";
        	}
        	else
        	{
        		toSend += "\"" + set.getKey() + "\": \"" + set.getValue() + "\", ";
        	}
    	}
        
        toSend = toSend.substring(0, toSend.length() - 2);
        toSend += "}";
		
		return toSend;
	}
}
