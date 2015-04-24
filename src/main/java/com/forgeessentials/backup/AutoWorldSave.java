package com.forgeessentials.backup;

import net.minecraftforge.common.DimensionManager;

import java.util.TimerTask;

public class AutoWorldSave extends TimerTask {

    @Override
    public void run()
    {
        while (AutoBackup.isBackingUp)
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                return;
            }
        }

        for (int i : DimensionManager.getIDs())
        {
            WorldSaver.addWorldNeedsSave(i);
        }
    }
}
