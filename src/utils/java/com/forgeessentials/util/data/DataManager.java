package com.forgeessentials.util.data;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.util.IChatComponent;

import org.apache.commons.io.FileUtils;

import com.forgeessentials.util.data.types.BlockType;
import com.forgeessentials.util.data.types.ItemStackType;
import com.forgeessentials.util.data.types.NBTTagCompoundType;
import com.forgeessentials.util.data.types.UserIdentType;

public class DataManager
{

    private static DataManager instance;

    private File basePath;

    static
    {
        DataUtils.addDataType(new UserIdentType());
        DataUtils.addDataType(new ItemStackType());
        DataUtils.addDataType(new NBTTagCompoundType());
        DataUtils.addDataType(new BlockType());
        DataUtils.addDataType(IChatComponent.class, new IChatComponent.Serializer());
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

    public void save(Object src, String key)
    {
        DataUtils.save(src, getTypeFile(src.getClass(), key));
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
        return DataUtils.loadAll(clazz, getTypePath(clazz));
    }

    public <T> T load(Class<T> clazz, String key)
    {
        return DataUtils.load(clazz, getTypeFile(clazz, key));
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
