package com.forgeessentials.backup;

import java.util.TimerTask;

import net.minecraftforge.common.DimensionManager;

public class AutoWorldSave extends TimerTask {

    public AutoWorldSave(){}

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
                break;
            }
        }

        for (int i : DimensionManager.getIDs())
        {
            WorldSaver.addWorldNeedsSave(i);
        }
    }
}
