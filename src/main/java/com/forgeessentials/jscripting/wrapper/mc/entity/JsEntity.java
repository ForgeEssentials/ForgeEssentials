package com.forgeessentials.jscripting.wrapper.mc.entity;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.jscripting.wrapper.mc.world.JsWorld;
import com.forgeessentials.util.ServerUtil;

import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;

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
                    Class<?> clazz = Class.forName(
                            "com.forgeessentials.jscripting.wrapper.mc.entity.Js" + entityClazz.getSimpleName());
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
            throw new RuntimeException(e);
        }
        return new JsEntity<>(entity);
    }

    private JsWorld<?> world;

    private JsEntity<?> ridingEntity;

    private JsEntityList riddenByEntity;

    protected JsEntity(T that)
    {
        super(that);
    }

    public String getName()
    {
        return that.getDisplayName().getString();
    }

    public String getId()
    {
        return that.getUUID().toString();
    }

    public UUID getUuid()
    {
        return that.getUUID();
    }

    public int getEntityId()
    {
        return that.getId();
    }

    public String getDimension()
    {
        return that.level.dimension().location().toString();
    }

    public double getX()
    {
        return that.position().x;
    }

    public double getY()
    {
        return that.position().y;
    }

    public double getZ()
    {
        return that.position().z;
    }

    public double getMotionX()
    {
        return that.getDeltaMovement().x;
    }

    public double getMotionY()
    {
        return that.getDeltaMovement().y;
    }

    public double getMotionZ()
    {
        return that.getDeltaMovement().z;
    }

    public int getChunkCoordX()
    {
        return that.chunkPosition().x;
    }

    public int getChunkCoordZ()
    {
        return that.chunkPosition().z;
    }

    public float getWidth()
    {
        return that.getBbWidth();
    }

    public float getHeight()
    {
        return that.getBbHeight();
    }

    public float getStepHeight()
    {
        return that.maxUpStep;
    }

    public boolean isOnGround()
    {
        return that.isOnGround();
    }

    public JsEntity<?> getRidingEntity()
    {
        if (ridingEntity == null)
            ridingEntity = get(that.getVehicle());
        return ridingEntity;
    }

    public JsEntityList getRiddenByEntity()
    {
        if (riddenByEntity == null)
            riddenByEntity = new JsEntityList(that.getPassengers());
        return riddenByEntity;
    }

    public JsWorld<?> getWorld()
    {
        if (world == null)
            world = JsWorld.get(that.level);
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
        ServerUtil.copyNbt(that.getPersistentData(), DataManager.fromJson(value, CompoundTag.class));
    }

    public String getEntityType()
    {
        return that.getClass().getSimpleName();
    }

}
