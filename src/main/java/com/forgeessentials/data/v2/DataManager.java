package com.forgeessentials.data.v2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.data.v2.types.ItemStackType;
import com.forgeessentials.data.v2.types.NBTTagCompoundType;
import com.forgeessentials.data.v2.types.UserIdentType;
import com.google.common.base.Throwables;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;

public class DataManager implements ExclusionStrategy
{

    public static interface DataType<T> extends JsonSerializer<T>, JsonDeserializer<T>
    {
        Class<T> getType();
    }

    private static DataManager instance;

    private static Gson gson;

    private static Map<Class<?>, JsonSerializer<?>> serializers = new HashMap<>();

    private static Map<Class<?>, JsonDeserializer<?>> deserializers = new HashMap<>();

    private static boolean formatsChanged;

    private File basePath;

    static
    {
        addDataType(new UserIdentType());
        addDataType(new ItemStackType());
        addDataType(new NBTTagCompoundType());
    }

    public DataManager(File basePath)
    {
        this.basePath = basePath;
    }

    public static DataManager getInstance()
    {
        if (instance == null)
            throw new RuntimeException("Tried to access DataManager before its initialization");
        return instance;
    }

    public static void setInstance(DataManager instance)
    {
        DataManager.instance = instance;
    }

    public static void addDataType(DataType<?> type)
    {
        serializers.put(type.getType(), type);
        deserializers.put(type.getType(), type);
        formatsChanged = true;
    }

    public static <T> void addSerializer(Class<T> clazz, JsonSerializer<T> type)
    {
        serializers.put(clazz, type);
        formatsChanged = true;
    }

    public static <T> void addDeserializer(Class<T> clazz, JsonDeserializer<T> type)
    {
        deserializers.put(clazz, type);
        formatsChanged = true;
    }

    public void save(Object src, String key)
    {
        try (FileWriter out = new FileWriter(getTypeFile(src.getClass(), key)))
        {
            getGson().toJson(src, out);
        }
        catch (Throwable e)
        {
            Throwables.propagate(e);
        }
    }

    public void saveAll(Map<?, ?> dataMap)
    {
        for (Entry<?, ?> element : dataMap.entrySet())
            save(element.getValue(), element.getKey().toString());
    }

    public boolean delete(Class<?> clazz, String key)
    {
        File file = getTypeFile(clazz, key);
        return file.delete();
    }

    public boolean exists(Class<?> clazz, String key)
    {
        File file = getTypeFile(clazz, key);
        return file.exists();
    }

    public <T> Map<String, T> loadAll(Class<T> clazz)
    {
        File[] files = getTypePath(clazz).listFiles();
        Map<String, T> objects = new HashMap<>();
        if (files != null)
            for (File file : files)
                if (!file.isDirectory() && file.getName().endsWith(".json"))
                {
                    String key = file.getName().replace(".json", "");
                    T o = load(clazz, key);
                    if (o != null)
                        objects.put(key, o);
                }
        return objects;
    }

    public <T> T load(Class<T> clazz, String key)
    {
        File file = getTypeFile(clazz, key);
        if (!file.exists())
            return null;
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            T obj = getGson().fromJson(br, clazz);
            if (obj instanceof Loadable)
                ((Loadable) obj).afterLoad();
            return obj;
        }
        catch (JsonParseException e)
        {
            ForgeEssentials.log.error(String.format("Error parsing data file \"%s\"", file.getAbsolutePath()));
            e.printStackTrace();
        }
        catch (IOException e)
        {
            ForgeEssentials.log.error(String.format("Error loading data file \"%s\"", file.getAbsolutePath()));
            e.printStackTrace();
        }
        return null;
    }

    public <T> T load(Type type, String key)
    {
        File file = getTypeFile(type.getClass(), key);
        if (!file.exists())
            return null;
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            return getGson().fromJson(br, type);
        }
        catch (JsonParseException e)
        {
            ForgeEssentials.log.error(String.format("Error parsing data file \"%s\"", file.getAbsolutePath()));
            e.printStackTrace();
        }
        catch (IOException e)
        {
            ForgeEssentials.log.error(String.format("Error loading data file \"%s\"", file.getAbsolutePath()));
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f)
    {
        Expose expose = f.getAnnotation(Expose.class);
        if (expose != null && (!expose.serialize() || !expose.deserialize()))
            return true;
        return false;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz)
    {
        return false;
    }

    public Gson getGson()
    {
        if (gson == null || formatsChanged)
        {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            builder.setExclusionStrategies(this);

            for (Entry<Class<?>, JsonSerializer<?>> format : serializers.entrySet())
                builder.registerTypeAdapter(format.getKey(), format.getValue());
            for (Entry<Class<?>, JsonDeserializer<?>> format : deserializers.entrySet())
                builder.registerTypeAdapter(format.getKey(), format.getValue());

            gson = builder.create();
        }
        return gson;
    }

    private File getTypePath(Class<?> clazz)
    {
        File path = new File(basePath, clazz.getSimpleName());
        path.mkdirs();
        return path;
    }

    private File getTypeFile(Class<?> clazz, String key)
    {
        return new File(getTypePath(clazz), key + ".json");
    }

}
