package com.forgeessentials.worldborder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.AreaShape;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.data.v2.Loadable;
import com.google.gson.annotations.Expose;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class WorldBorder implements Loadable
{

    private boolean enabled = false;

    private Point center;

    private Point size;

    private AreaShape shape = AreaShape.BOX;

    private List<WorldBorderEffect> effects = new ArrayList<>();

    String dimID;

    @Expose(serialize = false)
    private AreaBase area;

    @Expose(serialize = false)
    private Map<PlayerEntity, Set<WorldBorderEffect>> activeEffects = new WeakHashMap<>();

    public WorldBorder(Point center, int xSize, int zSize, String registryKey)
    {
        this.center = center;
        this.size = new Point(xSize, 0, zSize);
        this.dimID = registryKey;
        updateArea();
    }

    @Override
    public void afterLoad()
    {
        if (effects == null)
            effects = new ArrayList<>();
        for (Iterator<WorldBorderEffect> iterator = effects.iterator(); iterator.hasNext();)
            if (iterator.next() == null)
                iterator.remove();
        activeEffects = new WeakHashMap<>();
        updateArea();
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public Point getCenter()
    {
        return center;
    }

    public void setCenter(Point center)
    {
        this.center = center;
        updateArea();
    }

    public Point getSize()
    {
        return size;
    }

    public void setSize(Point size)
    {
        this.size = size;
        updateArea();
    }

    public void addEffect(WorldBorderEffect effect)
    {
        effects.add(effect);
    }

    public List<WorldBorderEffect> getEffects()
    {
        return effects;
    }

    public AreaShape getShape()
    {
        return shape;
    }

    public void setShape(AreaShape shape)
    {
        this.shape = shape;
    }

    public AreaBase getArea()
    {
        return area;
    }

    public void updateArea()
    {
        Point minP = new Point(center.getX() - size.getX(), center.getY() - size.getY(), center.getZ() - size.getZ());
        Point maxP = new Point(center.getX() + size.getX(), center.getY() + size.getY(), center.getZ() + size.getZ());
        area = new AreaBase(minP, maxP);
    }

    public Set<WorldBorderEffect> getOrCreateActiveEffects(PlayerEntity player)
    {
        return activeEffects.computeIfAbsent(player, k -> new HashSet<>());
    }

    public Set<WorldBorderEffect> getActiveEffects(PlayerEntity player)
    {
        return activeEffects.get(player);
    }

    public void save()
    {
        String key = dimID;
        DataManager.getInstance().save(this, key.replace(":", "-"));
    }

    public static WorldBorder load(World world)
    {
        String key = world.dimension().location().toString();
        return DataManager.getInstance().load(WorldBorder.class, key.replace(":", "-"));
    }

}
