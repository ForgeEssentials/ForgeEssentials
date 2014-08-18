package com.forgeessentials.protection;

import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.data.api.IReconstructData;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.Reconstructor;
import com.forgeessentials.data.api.SaveableObject.SaveableField;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumGameType;

import java.util.ArrayList;
import java.util.List;

@SaveableObject
public class AdditionalZoneData {

    @SaveableField
    private String zoneName;

    @SaveableField
    private int gameMode;

    @SaveableField
    private List<String> bannedItems = new ArrayList<String>();

    public AdditionalZoneData(Zone zone)
    {
        this.zoneName = zone.getZoneName();
        this.gameMode = MinecraftServer.getServer().getGameType().getID();
    }

    private AdditionalZoneData(Object zoneName, Object bannedItems, Object gamemode)
    {
        this.zoneName = (String) zoneName;
        this.bannedItems = (List<String>) bannedItems;
        this.gameMode = (int) gamemode;
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

    public EnumGameType getGameMode(){return EnumGameType.getByID(gameMode);}

    public void setGameMode(int gameMode){this.gameMode = gameMode;}

    @Reconstructor
    private static AdditionalZoneData reconstruct(IReconstructData tag)
    {
        return new AdditionalZoneData(tag.getFieldValue("zoneName"), tag.getFieldValue("bannedItems"), tag.getFieldValue("gameMode"));
    }

}
