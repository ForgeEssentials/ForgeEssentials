package com.forgeessentials.data.v2;

import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
import com.forgeessentials.data.api.SaveableObject.UniqueLoadingKey;
import com.forgeessentials.data.v2.types.ItemStackType;
import com.forgeessentials.data.v2.types.NBTTagCompoundType;
import com.forgeessentials.util.OutputHandler;
import com.google.common.base.Throwables;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataManager implements ExclusionStrategy {

    public static interface DataType<T> extends JsonSerializer<T>, JsonDeserializer<T> {
        Class<T> getType();
    }

    private static DataManager instance;

    private Gson gson;

    private File basePath;

    private List<DataType> dataTypes = new ArrayList<>();

    private boolean formatsChanged;

    public DataManager(File basePath)
    {
        this.basePath = basePath;
        addDataType(new ItemStackType());
        addDataType(new NBTTagCompoundType());
    }

    public static DataManager getInstance()
    {
        if (instance == null)
            throw new NullPointerException();
        return instance;
    }

    public static void setInstance(DataManager instance)
    {
        DataManager.instance = instance;
    }

    public void addDataType(DataType type)
    {
        dataTypes.add(type);
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

    public boolean delete(Class<?> clazz, String key)
    {
        File file = getTypeFile(clazz, key);
        return file.delete();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> loadAll(Class<?> clazz)
    {
        File[] files = getTypePath(clazz).listFiles();
        List<T> objects = new ArrayList<>();
        if (files != null)
            for (File file : files)
                if (!file.isDirectory() && file.getName().endsWith(".json"))
                {
                    String key = file.getName().replace(".json", "");
                    T o = (T) load(clazz, key);
                    if (o != null)
                        objects.add(o);
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
            return getGson().fromJson(br, clazz);
        }
        catch (JsonParseException e)
        {
            OutputHandler.felog.severe(String.format("Error parsing data file \"%s\"", file.getAbsolutePath()));
            e.printStackTrace();
        }
        catch (IOException e)
        {
            OutputHandler.felog.severe(String.format("Error loading data file \"%s\"", file.getAbsolutePath()));
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f)
    {
        if (f.getDeclaringClass().getAnnotation(SaveableObject.class) != null && f.getAnnotation(SaveableField.class) == null
                && f.getAnnotation(UniqueLoadingKey.class) == null)
            return true;
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

    private Gson getGson()
    {
        if (gson == null || formatsChanged)
        {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            builder.setExclusionStrategies(this);

            for (DataType format : dataTypes)
                builder.registerTypeAdapter(format.getType(), format);

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
