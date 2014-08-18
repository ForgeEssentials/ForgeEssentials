package com.forgeessentials.protection;

import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.data.api.IReconstructData;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.Reconstructor;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
import com.forgeessentials.data.api.SaveableObject.UniqueLoadingKey;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

@SaveableObject
public class AdditionalZoneData {

    @UniqueLoadingKey
    @SaveableField
    private String zoneName;

    @SaveableField
    private Integer gameMode;

    @SaveableField
    private List<String> bannedItems = new ArrayList<String>();

    public AdditionalZoneData(Zone zone)
    {
        this.zoneName = zone.getZoneName();
        this.gameMode = MinecraftServer.getServer().getGameType().getID();
        System.out.println(this.zoneName + ":" + this.gameMode);
    }

    private AdditionalZoneData(Object zoneName, Object bannedItems, Object gamemode)
    {
        this.zoneName = (String) zoneName;
        this.bannedItems = (List<String>) bannedItems;
        this.gameMode = (Integer) gamemode;
        System.out.println(this.zoneName + ":" + this.gameMode);
    }

    public void addBannedItem(String bannedItemID)
    {
        bannedItems.add(bannedItemID);
    }

    public void removeBannedItem(String bannedItemID)
    {
        bannedItems.remove(bannedItemID);
    }

    public List<String> getBannedItems()
    {
        return bannedItems;
    }

    public String getName()
    {
        return zoneName;
    }

    public Integer getGameMode(){return gameMode;}

    public void setGameMode(Integer gamemodeN){this.gameMode = new Integer(gamemodeN);}

    @Reconstructor
    private static AdditionalZoneData reconstruct(IReconstructData tag)
    {
        return new AdditionalZoneData(tag.getFieldValue("zoneName"), tag.getFieldValue("bannedItems"), tag.getFieldValue("gameMode"));
    }

}
