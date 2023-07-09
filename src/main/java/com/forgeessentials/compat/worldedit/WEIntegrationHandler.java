package com.forgeessentials.compat.worldedit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.forgeessentials.util.selections.SelectionHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class WEIntegrationHandler
{

    private CUIComms cuiComms;

    public boolean postLoad()
    {
        if (WEIntegration.disable)
        {
            LoggingHandler.felog.error("Requested to force-disable WorldEdit.");
            if (ModList.get().isLoaded("worldedit"))
            {
                try
                {
                    Class<?> cls = Class.forName("com.sk89q.worldedit.forge.ForgeWorldEdit");
                    Field field = cls.getField("inst");
                    MinecraftForge.EVENT_BUS.unregister(field.get(cls)); // forces worldedit forge NOT to load
                }
                catch (ClassNotFoundException | SecurityException | NoSuchFieldException | IllegalArgumentException
                        | IllegalAccessException e1)
                {
                    LoggingHandler.felog.error("WorldEdit not found, unregistering WEIntegrationTools");
                    return true;
                }
            }
            return true;
        }
        else
        {
            if (ModList.get().isLoaded("worldedit"))
            {
                SelectionHandler.selectionProvider = new WESelectionHandler();
            }
            else
            {
                LoggingHandler.felog.error("WorldEdit not found, unregistering WEIntegrationTools");
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public void serverStart(FEModuleServerStartingEvent e)
    {
        if (!WEIntegration.stop)
        {
            try
            {
                Class<?> callingClass = Class.forName("com.sk89q.worldedit.forge.ForgeWorldEdit");
                Class<?> provider = Class.forName("com.sk89q.worldedit.forge.ForgePermissionsProvider");

                Field field = callingClass.getField("inst");
                Class<?> instance = field.getClass();

                Method instanceMethod = instance.getMethod("setPermissionsProvider", provider);
                instanceMethod.invoke(instance, new PermissionsHandler());
                cuiComms = new CUIComms();
            }
            catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException | NoSuchFieldException e1)
            {
                e1.printStackTrace();
            }
            APIRegistry.perms.registerNode("worldedit.*", DefaultPermissionLevel.OP, "WorldEdit");
        }

    }

}
