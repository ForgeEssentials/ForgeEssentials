package com.forgeessentials.api;

import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.snooper.Response;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.EventBus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * This is the central access point for all FE API functions
 *
 * @author luacs1998
 */
public class APIRegistry {

    // Use this to call API functions available in the economy module.
    public static IEconManager wallet;

    // Use to call API functions from the permissions module.
    public static IPermissionsHelper perms;

    private static Method ResponseRegistry_regsisterResponce;

    private static final EventBus FE_EVENTBUS = new EventBus();

    /**
     * Snooper method to register your responses.
     *
     * @param ID
     * @param response
     */
    public static void registerResponse(Integer ID, Response response)
    {
        try
        {
            if (ResponseRegistry_regsisterResponce == null)
            {
                ResponseRegistry_regsisterResponce = Class.forName("com.forgeessentials.snooper.ResponseRegistry")
                        .getMethod("registerResponse", Integer.class, Response.class);
            }
            ResponseRegistry_regsisterResponce.invoke(null, ID, response);
        }
        catch (Exception e)
        {
            FMLLog.warning("[FE API] Unable to register " + response.getName() + " with ID: " + ID);
            e.printStackTrace();
        }
    }

    public static EventBus getFEEventBus()
    {
        return FE_EVENTBUS;
    }

    /**
     * Use this annotation to mark classes where static methods with other FE annotations might be.
     *
     * @author AbrarSyed
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    public @interface ForgeEssentialsRegistrar {
        String ident();
    }

}
