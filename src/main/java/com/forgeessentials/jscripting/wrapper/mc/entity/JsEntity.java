package com.forgeessentials.jscripting.wrapper.mc.entity;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.jscripting.wrapper.mc.world.JsWorld;
import com.forgeessentials.util.ServerUtil;
import com.google.common.base.Throwables;

public class JsEntity<T extends Entity> extends JsWrapper<T>
{

    private static Map<Class<?>, Constructor<?>> entityWrapperConstructors = new HashMap<>();

    /**
     * @tsd.ignore
     */
    public static JsEntity<?> get(Entity entity)
    {
        if (entity == null)
            return null;
        // Fancy reflection crap to get a specific entity type if it exists
        // TODO: Maybe use cache of existing wrappers from ScriptCompiler instead?
        try
        {
            Class<?> entityClazz = entity.getClass();
            Constructor<?> con = entityWrapperConstructors.get(entity.getClass());
            if (con != null)
                return (JsEntity<?>) con.newInstance(entity);

            for (; Entity.class.isAssignableFrom(entityClazz); entityClazz = entityClazz.getSuperclass())
            {
                try
                {
                    Class<?> clazz = Class.forName("com.forgeessentials.jscripting.wrapper.mc.entity.Js" + entityClazz.getSimpleName());
                    if (JsEntity.class.isAssignableFrom(clazz))
                    {
                        con = clazz.getDeclaredConstructor(entityClazz);
                        con.setAccessible(true);
                        entityWrapperConstructors.put(entity.getClass(), con);
                        return (JsEntity<?>) con.newInstance(entity);
                    }
                }
                catch (ClassNotFoundException e)
                {
                    /* do nothing */
                }
            }
        }
        catch (Exception e)
        {
            Throwables.propagate(e);
        }
        return new JsEntity<>(entity);
    }

    private JsWorld<?> world;

    private JsEntity<?> ridingEntity;

    private JsEntity<?> riddenByEntity;

    protected JsEntity(T that)
    {
        super(that);
    }

    public String getName()
    {
        return that.getName();
    }

    public String getId()
    {
        return that.getPersistentID().toString();
    }

    public UUID getUuid()
    {
        return that.getPersistentID();
    }

    public int getEntityId()
    {
        return that.getEntityId();
    }

    public int getDimension()
    {
        return that.dimension;
    }

    public double getX()
    {
        return that.posX;
    }

    public double getY()
    {
        return that.posY;
    }

    public double getZ()
    {
        return that.posZ;
    }

    public double getMotionX()
    {
        return that.motionX;
    }

    public double getMotionY()
    {
        return that.motionY;
    }

    public double getMotionZ()
    {
        return that.motionZ;
    }

    public int getChunkCoordX()
    {
        return that.chunkCoordX;
    }

    public int getChunkCoordY()
    {
        return that.chunkCoordY;
    }

    public int getChunkCoordZ()
    {
        return that.chunkCoordZ;
    }

    public float getWidth()
    {
        return that.width;
    }

    public float getHeight()
    {
        return that.height;
    }

    public float getStepHeight()
    {
        return that.stepHeight;
    }

    public boolean isOnGround()
    {
        return that.onGround;
    }

    public JsEntity<?> getRidingEntity()
    {
        if (ridingEntity == null)
            ridingEntity = get(that.ridingEntity);
        return ridingEntity;
    }

    public JsEntity<?> getRiddenByEntity()
    {
        if (riddenByEntity == null)
            riddenByEntity = get(that.riddenByEntity);
        return riddenByEntity;
    }

    public JsWorld<?> getWorld()
    {
        if (world == null)
            world = JsWorld.get(that.worldObj);
        return world;
    }

    /**
     * @tsd.ignore
     */
    public String _getNbt()
    {
        return DataManager.toJson(that.getEntityData());
    }

    /**
     * @tsd.ignore
     */
    public void _setNbt(String value)
    {
        ServerUtil.copyNbt(that.getEntityData(), DataManager.fromJson(value, NBTTagCompound.class));
    }

    public String getEntityType()
    {
        return that.getClass().getSimpleName();
    }

}
