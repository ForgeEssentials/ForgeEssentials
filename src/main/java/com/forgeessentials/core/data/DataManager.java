package com.forgeessentials.core.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.forgeessentials.core.data.types.ItemStackType;
import com.forgeessentials.core.data.types.NBTTagCompoundType;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
import com.forgeessentials.data.api.SaveableObject.UniqueLoadingKey;
import com.google.common.base.Throwables;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DataManager implements ExclusionStrategy {

    private static DataManager instance;

    private Gson gson;

    private File basePath;

    public DataManager(File basePath)
    {
        this.basePath = basePath;
        createGson();
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

    private void createGson()
    {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.setExclusionStrategies(this);

        builder.registerTypeAdapter(ItemStack.class, new ItemStackType());
        builder.registerTypeAdapter(NBTTagCompound.class, new NBTTagCompoundType());

        gson = builder.create();
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

    public void save(Object src, String key)
    {
        try (FileWriter out = new FileWriter(getTypeFile(src.getClass(), key)))
        {
            gson.toJson(src, out);
        }
        catch (IOException e)
        {
            Throwables.propagate(e);
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
                    objects.add((T) load(clazz, key));
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
            return gson.fromJson(br, clazz);
        }
        catch (IOException e)
        {
            Throwables.propagate(e);
        }
        catch (Throwable e)
        {
            Throwables.propagate(e);
        }
        return null;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f)
    {
        return f.getDeclaringClass().getAnnotation(SaveableObject.class) != null && f.getAnnotation(SaveableField.class) == null
                && f.getAnnotation(UniqueLoadingKey.class) == null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz)
    {
        return false;
    }

}
