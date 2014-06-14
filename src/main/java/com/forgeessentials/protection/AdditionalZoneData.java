package com.forgeessentials.protection;

import com.forgeessentials.data.api.IReconstructData;
import com.forgeessentials.data.api.SaveableObject;
import com.forgeessentials.data.api.SaveableObject.Reconstructor;
import com.forgeessentials.data.api.SaveableObject.SaveableField;

import java.util.ArrayList;
import java.util.List;

@SaveableObject
public class AdditionalZoneData {

    @SaveableField
    private String zoneName;

    @SaveableField
    private List<String> bannedItems = new ArrayList<String>();

    public AdditionalZoneData(String zoneName)
    {
        this.zoneName = zoneName;
    }

    private AdditionalZoneData(Object zoneName, Object bannedItems)
    {
        this.zoneName = (String) zoneName;
        this.bannedItems = (List<String>) bannedItems;
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

    @Reconstructor
    private static AdditionalZoneData reconstruct(IReconstructData tag)
    {
        return new AdditionalZoneData(tag.getFieldValue("zoneName"), tag.getFieldValue("bannedItems"));
    }

}
