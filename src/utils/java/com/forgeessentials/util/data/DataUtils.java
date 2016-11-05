package com.forgeessentials.util.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.forgeessentials.util.Utils;
import com.google.common.base.Throwables;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;

public class DataUtils
{
    public static final String DEFAULT_GROUP = "default";

    private static Gson gson;
    private static boolean formatsChanged;
    private static Map<Class<?>, JsonSerializer<?>> serializers = new HashMap<>();
    private static Map<Class<?>, JsonDeserializer<?>> deserializers = new HashMap<>();

    private static Set<String> defaultSerializationGroups = new HashSet<>(Arrays.asList(DEFAULT_GROUP));
    private static Set<String> serializationGroups = defaultSerializationGroups;

    public static void addDataType(DataType<?> type)
    {
        serializers.put(type.getType(), type);
        deserializers.put(type.getType(), type);
        formatsChanged = true;
    }

    public static void addDataType(Class<?> clazz, Object serializer)
    {
        if (serializer instanceof JsonSerializer<?>)
            serializers.put(clazz, (JsonSerializer<?>) serializer);
        if (deserializers instanceof JsonDeserializer<?>)
            deserializers.put(clazz, (JsonDeserializer<?>) serializer);
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

    public static void save(Object src, File file)
    {
        try (FileWriter out = new FileWriter(file))
        {
            toJson(src, out);
        }
        catch (Throwable e)
        {
            Utils.felog.error(String.format("Error saving data to %s", file.getName()), e);
            Throwables.propagate(e);
        }
    }

    public static void saveAll(Map<?, ?> dataMap, File path)
    {
        for (Entry<?, ?> element : dataMap.entrySet())
            save(element.getValue(), new File(path, element.getKey() + ".json"));
    }

    public static <T> Map<String, T> loadAll(Class<T> clazz, File path)
    {
        File[] files = path.exists() ? path.listFiles() : new File[0];
        Map<String, T> objects = new HashMap<>();
        if (files != null)
            for (File file : files)
                if (!file.isDirectory() && file.getName().endsWith(".json"))
                {
                    T o = load(clazz, file);
                    if (o != null)
                    {
                        String key = file.getName().replace(".json", "");
                        objects.put(key, o);
                    }
                }
        return objects;
    }

    public static <T> T load(Class<T> clazz, File file)
    {
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
            Utils.felog.error(String.format("Error parsing data file \"%s\"", file.getAbsolutePath()));
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Utils.felog.error(String.format("Error loading data file \"%s\"", file.getAbsolutePath()));
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T load(Type t, File file)
    {
        if (!file.exists())
            return null;
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            T obj = getGson().fromJson(br, t);
            if (obj instanceof Loadable)
                ((Loadable) obj).afterLoad();
            return obj;
        }
        catch (JsonParseException e)
        {
            Utils.felog.error(String.format("Error parsing data file \"%s\"", file.getAbsolutePath()));
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Utils.felog.error(String.format("Error loading data file \"%s\"", file.getAbsolutePath()));
            e.printStackTrace();
        }
        return null;
    }

    public static Gson getGson()
    {
        if (gson == null || formatsChanged)
        {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            builder.setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f)
                {
                    Expose expose = f.getAnnotation(Expose.class);
                    if (expose != null && (!expose.serialize() || !expose.deserialize()))
                        return true;

                    SerializationGroup groupAnnot = f.getAnnotation(SerializationGroup.class);
                    if (groupAnnot != null && !serializationGroups.contains(groupAnnot.name()))
                        return true;

                    return false;
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz)
                {
                    return false;
                }
            });

            for (Entry<Class<?>, JsonSerializer<?>> format : serializers.entrySet())
                builder.registerTypeAdapter(format.getKey(), format.getValue());
            for (Entry<Class<?>, JsonDeserializer<?>> format : deserializers.entrySet())
                builder.registerTypeAdapter(format.getKey(), format.getValue());

            gson = builder.create();
        }
        return gson;
    }

    public static String toJson(Object src, String... groups)
    {
        try
        {
            if (groups.length > 0)
                serializationGroups = new HashSet<>(Arrays.asList(groups));
            return getGson().toJson(src);
        }
        finally
        {
            serializationGroups = defaultSerializationGroups;
        }
    }

    public static <T> T fromJson(String src, Class<T> clazz)
    {
        try
        {
            return getGson().fromJson(src, clazz);
        }
        finally
        {
            serializationGroups = defaultSerializationGroups;
        }
    }

    public static <T> T fromJson(String src, Type type)
    {
        try
        {
            return getGson().fromJson(src, type);
        }
        finally
        {
            serializationGroups = defaultSerializationGroups;
        }
    }

    public static void toJson(Object src, Appendable writer, String... groups) throws JsonIOException
    {
        try
        {
            if (groups.length > 0)
                serializationGroups = new HashSet<>(Arrays.asList(groups));
            getGson().toJson(src, writer);
        }
        finally
        {
            serializationGroups = defaultSerializationGroups;
        }
    }

    public static interface DataType<T> extends JsonSerializer<T>, JsonDeserializer<T>
    {
        Class<T> getType();
    }
}
