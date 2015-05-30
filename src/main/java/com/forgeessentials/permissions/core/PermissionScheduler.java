package com.forgeessentials.permissions.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import net.minecraftforge.common.DimensionManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.ServerEventHandler;
import com.google.gson.annotations.Expose;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class PermissionScheduler extends ServerEventHandler
{

    public static class PermissionEntry
    {

        public String on;

        public String off;

        public PermissionEntry(String on, String off)
        {
            this.on = on;
            this.off = off;
        }

    }

    public static class PermissionSchedule
    {

        @Expose(serialize = false)
        public boolean state;

        public boolean isRealTime = true;

        public boolean isDelay = false;

        public int zoneId = 1;

        public String group = Zone.GROUP_DEFAULT;

        public String onMessage;

        public String offMessage;

        public List<Integer> times = new ArrayList<Integer>();

        public Map<String, PermissionEntry> permissions = new HashMap<>();

    }

    protected Map<String, PermissionSchedule> schedules = new HashMap<>();

    protected long lastCheck;

    @SubscribeEvent
    public void serverTickEvent(TickEvent.ServerTickEvent e)
    {
        if (System.currentTimeMillis() - lastCheck >= 1000)
        {
            lastCheck = System.currentTimeMillis();
            checkSchedules(false);
        }
    }

    public void checkSchedules(boolean initialize)
    {
        for (PermissionSchedule schedule : schedules.values())
        {
            int timeFrame = 0;
            for (int t : schedule.times)
                timeFrame += t;

            long time;
            if (schedule.isRealTime)
            {
                int modulo = 24 * 60 * 60; // One day
                if (timeFrame > modulo)
                    modulo *= 7;
                time = ((Calendar.getInstance().getTimeInMillis() + TimeZone.getDefault().getRawOffset()) / 1000) % modulo;
            }
            else
            {
                if (schedule.isDelay)
                    time = DimensionManager.getWorld(0).getWorldInfo().getWorldTotalTime();
                else
                    time = DimensionManager.getWorld(0).getWorldInfo().getWorldTime();
            }

            if (schedule.isDelay)
                time %= timeFrame;

            boolean desiredState = true;
            for (int t : schedule.times)
            {
                if (time <= t)
                    break;
                if (schedule.isDelay)
                    time -= t;
                desiredState ^= true;
            }

            if (desiredState == schedule.state && !initialize)
                continue;

            Zone zone = APIRegistry.perms.getZoneById(schedule.zoneId);
            for (Entry<String, PermissionEntry> permission : schedule.permissions.entrySet())
                zone.setGroupPermissionProperty(schedule.group, permission.getKey(), desiredState ? permission.getValue().on : permission.getValue().off);

            schedule.state = desiredState;
            if (schedule.state && schedule.onMessage != null)
                OutputHandler.broadcast(OutputHandler.confirmation(schedule.onMessage));
            if (!schedule.state && schedule.offMessage != null)
                OutputHandler.broadcast(OutputHandler.confirmation(schedule.offMessage));
        }
    }

    public void loadAll()
    {
        schedules = DataManager.getInstance().loadAll(PermissionSchedule.class);

        if (schedules.isEmpty())
        {
            PermissionSchedule schedule;

            schedule = new PermissionSchedule();
            schedule.isRealTime = false;
            schedule.isDelay = false;
            // schedule.onMessage = "Turned night permissions ON";
            // schedule.offMessage = "Turned night permissions OFF";
            schedule.times.add(6000);
            schedule.times.add(18000);
            schedule.permissions.put("some.test.permission", new PermissionEntry("true", "false"));
            schedules.put("sample_mc", schedule);
        }
        // TODO: Remove this after beta7 release!!!!
        schedules.remove("sample_delay");
        DataManager.getInstance().delete(PermissionSchedule.class, "sample_delay");

        checkSchedules(true);
    }

    public void saveAll()
    {
        for (Entry<String, PermissionSchedule> task : schedules.entrySet())
            DataManager.getInstance().save(task.getValue(), task.getKey());
    }

}
