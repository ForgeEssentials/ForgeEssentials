package com.forgeessentials.jscripting.wrapper;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.forgeessentials.data.v2.DataManager;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * @tsd.static localStorage
 */
public class JsLocalStorage
{

    private static Map<String, String> data = new HashMap<>();

    /**
     * @tsd.ignore
     */
    public static void load()
    {
        Type mapType = new TypeToken<HashMap<String, String>>()
        {
        }.getType();
        data = DataManager.load(mapType, new File(DataManager.getInstance().getBasePath(), "script_data.json"));
        if (data == null)
            data = new HashMap<>();
    }

    /**
     * @tsd.ignore
     */
    public static void save()
    {
        DataManager.save(data, new File(DataManager.getInstance().getBasePath(), "script_data.json"));
    }

    /**
     * Returns an integer representing the number of data items stored in the Storage object.
     */
    public static int length()
    {
        return data.size();
    }

    /**
     * When passed a number n, this method will return the name of the nth key in the storage.
     */
    public static String key(int n)
    {
        List<String> keys = new ArrayList<>(data.keySet());
        return keys.get(n);
    }

    /**
     * When passed a key name, will return that key's value.
     */
    public static Object getItem(String key)
    {
        return parseValue(data.get(key));
    }

    private static Object parseValue(String value) {
        try {
            return DataManager.fromJson(value, Map.class);
        } catch (JsonSyntaxException ignored) {}

        try
        {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException ignored) {}

        try
        {
            return Double.parseDouble(value);
        }
        catch (NumberFormatException ignored) {}

        try
        {
            return Boolean.parseBoolean(value);
        } catch (NumberFormatException ignored) {}

        return value;
    }
    /**
     * When passed a key name and value, will add that key to the storage, or update that key's value if it already exists.<br>
     * Returns the previous value for the passed key.
     */
    public static Object setItem(String key, Object value)
    {
        String valueStr = value instanceof String ? (String) value : DataManager.toJson(value);
        String oldData = data.put(key, valueStr);
        save(); // TODO: Always save?
        return parseValue(oldData);
    }

    /**
     * When passed a key name, will remove that key from the storage.<br>
     * Returns the previous value for the passed key.
     */
    public static Object removeItem(String key)
    {
        String oldData = data.remove(key);
        save(); // TODO: Always save?
        return parseValue(oldData);
    }

    /**
     * When invoked, will empty all keys out of the storage.
     */
    public static void clear()
    {
        data.clear();
        save(); // TODO: Always save?
    }

}
