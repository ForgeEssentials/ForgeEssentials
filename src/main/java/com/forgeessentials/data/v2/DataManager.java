package com.forgeessentials.data.v2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import com.forgeessentials.core.FEConfig;
import com.forgeessentials.data.v2.types.BlockType;
import com.forgeessentials.data.v2.types.ItemStackType;
import com.forgeessentials.data.v2.types.NBTTagCompoundType;
import com.forgeessentials.data.v2.types.UserIdentType;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import net.minecraft.network.chat.BaseComponent;

public class DataManager
{

    public static final String DEFAULT_GROUP = "default";

    public static interface DataType<T> extends JsonSerializer<T>, JsonDeserializer<T>
    {
        Class<T> getType();
    }

    private static DataManager instance;

    private static Gson gson;

    private static Map<Class<?>, JsonSerializer<?>> serializers = new HashMap<>();

    private static Map<Class<?>, JsonDeserializer<?>> deserializers = new HashMap<>();

    private static boolean formatsChanged;

    private static Set<String> defaultSerializationGroups = new HashSet<>(Collections.singletonList(DEFAULT_GROUP));

    private static Set<String> serializationGroups = defaultSerializationGroups;

    private File basePath;

    static
    {
        addDataType(new UserIdentType());
        addDataType(new ItemStackType());
        addDataType(new NBTTagCompoundType());
        addDataType(new BlockType());
        addDataType(BaseComponent.class, new BaseComponent.Serializer());
    }

    public DataManager(File basePath)
    {
        this.basePath = basePath;
        LoggingHandler.felog.debug("ForgeEssentials: Created new Datamanager Instance");
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

    public void save(Object src, String key)
    {
        save(src, getTypeFile(src.getClass(), key));
    }

    public static void save(Object src, File file)
    {
        try (FileWriter out = new FileWriter(file))
        {
            toJson(src, out);
            out.flush(); // required in java 16 in order for file to be created
        }
        catch (RuntimeException | Error | IOException e)
        {
            LoggingHandler.felog.error(String.format("Error saving data to %s", file.getName()), e);
            throw new RuntimeException(e);
        }
    }

    public void saveAll(Map<?, ?> dataMap)
    {
        for (Entry<?, ?> element : dataMap.entrySet())
            save(element.getValue(), element.getKey().toString());
    }

    public static void saveAll(Map<?, ?> dataMap, File path)
    {
        for (Entry<?, ?> element : dataMap.entrySet())
            save(element.getValue(), new File(path, element.getKey() + ".json"));
    }

    public boolean delete(Class<?> clazz, String key)
    {
        File file = getTypeFile(clazz, key);
        return file.delete();
    }

    public void deleteAll(Class<?> clazz)
    {
        try
        {
            FileUtils.deleteDirectory(getTypePath(clazz));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean exists(Class<?> clazz, String key)
    {
        File file = getTypeFile(clazz, key);
        return file.exists();
    }

    public <T> Map<String, T> loadAll(Class<T> clazz)
    {
        return loadAll(clazz, getTypePath(clazz));
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

    public <T> T load(Class<T> clazz, String key)
    {
        return load(clazz, getTypeFile(clazz, key));
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
            LoggingHandler.felog.error(String.format("Error parsing data file \"%s\"", file.getAbsolutePath()));
            e.printStackTrace();
            throw e;
        }
        catch (IOException e)
        {
            LoggingHandler.felog.error(String.format("Error loading data file \"%s\"", file.getAbsolutePath()));
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
            LoggingHandler.felog.error(String.format("Error parsing data file \"%s\"", file.getAbsolutePath()));
            e.printStackTrace();
        }
        catch (IOException e)
        {
            LoggingHandler.felog.error(String.format("Error loading data file \"%s\"", file.getAbsolutePath()));
            e.printStackTrace();
        }
        return null;
    }

    // Modified Copy of DateTypeAdapter
    static class FEDateAdapter extends TypeAdapter<Date>
    {
        private final java.text.DateFormat enUsFormat;
        private final DateFormat localFormat;
        private final DateFormat iso8601Format;

        public FEDateAdapter()
        {
            this.enUsFormat = java.text.DateFormat.getDateTimeInstance(2, 2, Locale.US);
            this.localFormat = java.text.DateFormat.getDateTimeInstance(2, 2);
            this.iso8601Format = buildIso8601Format();
        }

        private static DateFormat buildIso8601Format()
        {
            DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return iso8601Format;
        }

        public Date read(JsonReader in) throws IOException
        {
            if (in.peek() == JsonToken.NULL)
            {
                in.nextNull();
                return null;
            }
            else
            {
                return this.deserializeToDate(in.nextString());
            }
        }

        private synchronized Date deserializeToDate(String json)
        {
            try
            {
                return this.localFormat.parse(json);
            }
            catch (ParseException var5)
            {
                try
                {
                    return this.enUsFormat.parse(json);
                }
                catch (ParseException var4)
                {
                    try
                    {
                        return this.iso8601Format.parse(json);
                    }
                    catch (ParseException var3)
                    {
                        try
                        {
                            return FEConfig.FORMAT_GSON_COMPAT.parse(json);
                        }
                        catch (ParseException e)
                        {
                            throw new JsonSyntaxException(json, e);
                        }
                    }
                }
            }
        }

        public synchronized void write(JsonWriter out, Date value) throws IOException
        {
            if (value == null)
            {
                out.nullValue();
            }
            else
            {
                String dateFormatAsString = this.enUsFormat.format(value);
                out.value(dateFormatAsString);
            }
        }
    }

    public static Gson getGson()
    {
        if (gson == null || formatsChanged)
        {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            builder.registerTypeHierarchyAdapter(Date.class, new FEDateAdapter());
            builder.setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f)
                {
                    Expose expose = f.getAnnotation(Expose.class);
                    if (expose != null && (!expose.serialize() || !expose.deserialize()))
                        return true;

                    SerializationGroup groupAnnot = f.getAnnotation(SerializationGroup.class);
                    return groupAnnot != null && !serializationGroups.contains(groupAnnot.name());
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

    public File getBasePath()
    {
        return basePath;
    }

    public File getTypePath(Class<?> clazz)
    {
        File path = new File(basePath, clazz.getSimpleName());
        path.mkdirs();
        return path;
    }

    public File getTypeFile(Class<?> clazz, String key)
    {
        return new File(getTypePath(clazz), key + ".json");
    }

}
