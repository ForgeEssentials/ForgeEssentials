package com.forgeessentials.economy;

import com.forgeessentials.data.api.IReconstructData;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.Reconstructor;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
import com.forgeessentials.data.api.SaveableObject.UniqueLoadingKey;
import net.minecraft.entity.player.EntityPlayer;

@SaveableObject
public class Wallet {
    @SaveableField
    public int amount;
    @UniqueLoadingKey
    @SaveableField
    private String username;

    public Wallet(EntityPlayer player, int startAmount)
    {
        if (player.getEntityData().hasKey("FE-Economy"))
        {
            startAmount = player.getEntityData().getInteger("FE-Economy");
        }
        username = player.getPersistentID().toString();
        amount = startAmount;
    }

    private Wallet(Object username, Object amount)
    {
        this.username = (String) username;
        this.amount = (Integer) amount;
    }

    @Reconstructor
    private static Wallet reconstruct(IReconstructData tag)
    {
        return new Wallet(tag.getFieldValue("username"), tag.getFieldValue("amount"));
    }

    public String getUsername()
    {
        return username;
    }
}
