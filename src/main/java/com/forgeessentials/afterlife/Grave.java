package com.forgeessentials.afterlife;

import java.util.ArrayList;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.permissions.PermissionsManager;

import com.forgeessentials.commons.selections.WorldPoint;
import com.google.gson.annotations.Expose;

public class Grave {
    
    public String key;

    public WorldPoint point;

    public String owner;

    public ItemStack[] inv;

    public int xp;

    public int protTime;

    public boolean protEnable = true;

    @Expose(serialize = false)
    private boolean opened;

    @Expose(serialize = false)
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
        if (!opened)
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
