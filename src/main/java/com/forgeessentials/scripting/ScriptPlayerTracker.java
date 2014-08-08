package com.forgeessentials.scripting;

import com.forgeessentials.util.events.PlayerChangedZone;
import cpw.mods.fml.common.IPlayerTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.ForgeSubscribe;

public class ScriptPlayerTracker implements IPlayerTracker {
    @Override
    public void onPlayerLogin(EntityPlayer player)
    {
        EventType.run(player, EventType.LOGIN);
    }

    @Override
    public void onPlayerLogout(EntityPlayer player)
    {

    }

    @Override
    public void onPlayerChangedDimension(EntityPlayer player)
    {
        // do nothing

    }

    @Override
    public void onPlayerRespawn(EntityPlayer player)
    {
        EventType.run(player, EventType.RESPAWN);
    }

    @ForgeSubscribe
    public void onPlayerChangeZone(PlayerChangedZone e) { EventType.run(e.entityPlayer, EventType.ZONECHANGE);}

}