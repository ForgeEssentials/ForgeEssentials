package com.forgeessentials.afterlife;

import java.util.ArrayList;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.permissions.PermissionsManager;

import com.forgeessentials.data.api.IReconstructData;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.Reconstructor;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
import com.forgeessentials.data.api.SaveableObject.UniqueLoadingKey;
import com.forgeessentials.util.selections.WorldPoint;

@SaveableObject
public class Grave {
    
    @UniqueLoadingKey
    @SaveableField
    public String key;

    @SaveableField
    public WorldPoint point;

    @SaveableField
    public String owner;

    @SaveableField
    public ItemStack[] inv;

    @SaveableField
    public int xp;

    @SaveableField
    public int protTime;

    @SaveableField
    public boolean protEnable = true;

    private boolean opened;

	private long startTime;

    public Grave(WorldPoint point, EntityPlayer player, ArrayList<EntityItem> drops, Deathchest deathchest)
    {
        key = point.toString();
        this.point = point;
        owner = player.getPersistentID().toString();
        
        if (Deathchest.enableXP)
        {
            xp = player.experienceLevel;

            player.experienceLevel = 0;
            player.experienceTotal = 0;
        }
        inv = new ItemStack[drops.size()];
        for (int i = 0; i < drops.size(); i++)
        {
            inv[i] = drops.get(i).getEntityItem().copy();
        }

        protTime = Deathchest.protectionTime;

        deathchest.gravemap.put(point.toString(), this);
        startTime = System.currentTimeMillis();
    }

    private Grave(String key, Object point, Object owner, Object inv, Object xp, Object protTime, Object protEnable)
    {
        this.key = key;
        this.point = (WorldPoint) point;
        this.owner = (String) owner;
        this.inv = inv != null ? (ItemStack[]) inv : new ItemStack[0];
        this.xp = (Integer) xp;
        this.protTime = (Integer) protTime;
        this.protEnable = (Boolean) protEnable;
        startTime = System.currentTimeMillis();
    }

    @Reconstructor
    private static Grave reconstruct(IReconstructData tag)
    {
        return new Grave(tag.getUniqueKey(), tag.getFieldValue("point"), tag.getFieldValue("owner"), tag.getFieldValue("inv"), tag.getFieldValue("xp"),
                tag.getFieldValue("protTime"), tag.getFieldValue("protEnable"));
    }

    public void checkGrave()
    {
        if (inv.length == 0)
        {
            ModuleAfterlife.instance.deathchest.removeGrave(this, true);
        }
    }

    public int getSize()
    {
        if (inv == null)
        {
            return 0;
        }
        return inv.length % 9 == 0 ? inv.length : (inv.length / 9 + 1) * 9;
    }

    public void tick()
    {
        if ((System.currentTimeMillis() - startTime) / 1000L > protTime)
        {
            protEnable = false;
        }
    }

    public boolean canOpen(EntityPlayer player)
    {
        if (!protEnable)
        {
            return true;
        }
        if (player.getUniqueID().toString().equals(owner))
        {
            return true;
        }
        if (PermissionsManager.checkPermission(player, Deathchest.PERMISSION_BYPASS))
        {
            return true;
        }

        return false;
    }

    public void setOpen(boolean open)
    {
        opened = open;
    }

    public boolean isOpen()
    {
        return opened;
    }

	public void setSaveProtTime()
	{
		protTime -= (System.currentTimeMillis() - startTime) / 1000L;
	}
}
