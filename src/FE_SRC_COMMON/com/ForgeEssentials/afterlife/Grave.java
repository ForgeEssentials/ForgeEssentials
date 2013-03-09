package com.ForgeEssentials.afterlife;

import java.util.ArrayList;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.ForgeEssentials.api.data.IReconstructData;
import com.ForgeEssentials.api.data.SaveableObject;
import com.ForgeEssentials.api.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.api.data.SaveableObject.SaveableField;
import com.ForgeEssentials.api.data.SaveableObject.UniqueLoadingKey;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.query.PermQueryPlayer;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

@SaveableObject
public class Grave
{
	@UniqueLoadingKey
	public String		key;

	@SaveableField
	public WorldPoint	point;

	@SaveableField
	public String		owner;

	@SaveableField
	public ItemStack[]	inv;

	@SaveableField
	public int			xp;

	@SaveableField
	public int			protTime;

	@SaveableField
	public boolean		protEnable	= true;

	public Grave(WorldPoint point, EntityPlayer player, ArrayList<EntityItem> drops, Deathchest deathchest)
	{
		this.key = point.toString();
		this.point = point;
		this.owner = player.username;
		if (Deathchest.enableXP)
		{
			this.xp = player.experienceTotal;

			player.experienceLevel = 0;
			player.experienceTotal = 0;
			player.experienceValue = 0;
		}
		inv = new ItemStack[drops.size()];
		for (int i = 0; i < drops.size(); i++)
		{
			inv[i] = drops.get(i).getEntityItem().copy();
		}

		this.protTime = Deathchest.protectionTime;

		deathchest.gravemap.put(point.toString(), this);
	}

	@Reconstructor
	private static Grave reconstruct(IReconstructData tag)
	{
		return new Grave(tag.getUniqueKey(), tag.getFieldValue("point"), tag.getFieldValue("owner"), tag.getFieldValue("inv"), tag.getFieldValue("xp"), tag.getFieldValue("protTime"), tag.getFieldValue("protEnable"));
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
			return 0;
		return (inv.length % 9 == 0) ? inv.length : (((int) inv.length / 9) + 1) * 9;
	}

	public void tick()
	{
		if (protTime != 0)
		{
			protTime--;
		}
		else
		{
			protEnable = false;
		}
	}

	public boolean canOpen(EntityPlayer player)
	{
		if (!this.protEnable)
			return true;
		if (player.username.equals(point))
			return true;
		if (PermissionsAPI.checkPermAllowed(new PermQueryPlayer(player, Deathchest.PERMISSION_BYPASS)))
			return true;

		return false;
	}
}
