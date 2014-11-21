package com.forgeessentials.core.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.forgeessentials.util.events.ServerEventHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class TickTaskHandler extends ServerEventHandler {

    public static interface TickTask {

        public void tick();

        public void onComplete();

        public boolean isComplete();

    }

    public static final int MAX_BLOCK_CHANGES = 128;

    private static int changedBlocks;

    private static List<TickTask> tasks = new ArrayList<>();

    @SubscribeEvent
    public void serverTickEvent(TickEvent.ServerTickEvent e)
    {
        changedBlocks = 0;
        for (Iterator<TickTask> it = tasks.iterator(); it.hasNext();)
        {
            TickTask task = it.next();
            task.tick();
            if (task.isComplete())
            {
                task.onComplete();
                it.remove();
            }
        }
    }

    public static boolean changeBlock()
    {
        return ++changedBlocks < MAX_BLOCK_CHANGES;
    }

    public static void schedule(TickTask task)
    {
        tasks.add(task);
    }

}
