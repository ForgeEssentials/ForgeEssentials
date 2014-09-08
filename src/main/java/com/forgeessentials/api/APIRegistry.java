package com.forgeessentials.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.api.IGroup;

import com.forgeessentials.api.permissions.Group;
import com.forgeessentials.api.permissions.IPermissionsManager;
import com.forgeessentials.api.snooper.Response;

import cpw.mods.fml.common.FMLLog;

/**
 * This is the central access point for all FE API functions
 *
 * @author luacs1998
 */
public class APIRegistry {

    // Use this to call API functions available in the economy module.
    public static IEconManager wallet;

    // Use to call API functions from the permissions module.
    public static IPermissionsManager permissionManager;

    private static Method ResponseRegistry_regsisterResponce;

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

    public static Group getAsFEGroup(String name)
    {
        IGroup g = PermissionsManager.getGroup(name);
        if (g instanceof Group)
            return (Group)g;

        else
        {
            FMLLog.warning("[FE API] FEPermissions is not set as permissions handler - bad things could happen!");
            return null;
        }
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
