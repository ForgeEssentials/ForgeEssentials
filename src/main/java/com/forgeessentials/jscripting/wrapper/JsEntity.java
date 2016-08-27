package com.forgeessentials.jscripting.wrapper;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class JsEntity<T extends Entity> extends JsWrapper<T>
{
    
    public JsEntity(T that)
    {
        super(that);
    }

    public String getName()
    {
        return that.getCommandSenderName();
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

    public JsEntity<Entity> getRidingEntity()
    {
        return new JsEntity<>(that.ridingEntity);
    }

    public JsEntity<Entity> getRiddenByEntity()
    {
        return new JsEntity<>(that.riddenByEntity);
    }

    public JsWorld<World> getWorld()
    {
        return new JsWorld<>(that.worldObj);
    }

}
