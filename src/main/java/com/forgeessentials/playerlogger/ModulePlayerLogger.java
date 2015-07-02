package com.forgeessentials.playerlogger;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.FEModule.Preconditions;
import com.forgeessentials.playerlogger.command.CommandPlayerlogger;
import com.forgeessentials.playerlogger.command.CommandRollback;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStoppedEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@FEModule(name = "PlayerLogger", parentMod = ForgeEssentials.class)
public class ModulePlayerLogger
{

    public static final String PERM = "fe.pl";
    public static final String PERM_WAND = PERM + ".wand";
    public static final String PERM_COMMAND = PERM + ".cmd";

    private static PlayerLogger logger;

    @SuppressWarnings("unused")
    private PlayerLoggerEventHandler eventHandler;

    @Preconditions
    public boolean checkLibraries()
    {
        String[] compulsoryLibs = { "antlr.Version", "org.dom4j.Text", "org.hibernate.annotations.common.Version", "org.hibernate.Version",
                "org.hibernate.jpa.AvailableSettings", "javax.persistence.Version", "org.jboss.jandex.Main", "javassist.CtClass", "org.jboss.logging.Logger",
                "org.jboss.logging.annotations.Message", "javax.transaction.Status" };
        List<String> erroredLibs = new ArrayList<String>();
        for (String clazz : compulsoryLibs)
        {
            try
            {
                Launch.classLoader.findClass(clazz);
            }
            catch (ClassNotFoundException cnfe)
            {
                erroredLibs.add(clazz);
                cnfe.printStackTrace();
            }
        }
        if (!erroredLibs.isEmpty())
        {
            OutputHandler.felog.error("[ForgeEssentials] You are missing the following library files.");
            for (Object error : erroredLibs.toArray())
            {
                System.err.println(error);
            }

            OutputHandler.felog.error("[PlayerLogger] As the necessary files could not be loaded, PlayerLogger will be disabled.");
            OutputHandler.felog.error("[PlayerLogger] Please verify that the necessary files are present if you wish to use PlayerLogger.");
            return false;
        }
        return true;
    }


    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        logger = new PlayerLogger();
        eventHandler = new PlayerLoggerEventHandler();
        ForgeEssentials.getConfigManager().registerLoader("PlayerLogger", new PlayerLoggerConfig());

        FECommandManager.registerCommand(new CommandRollback());
        FECommandManager.registerCommand(new CommandPlayerlogger());
        // FECommandManager.registerCommand(new CommandTestPlayerlogger());
    }

    @SubscribeEvent
    public void serverPreInit(FEModuleServerPreInitEvent e)
    {
        registerPermissions();
        logger.loadDatabase();
    }

    private void registerPermissions()
    {
        APIRegistry.perms.registerPermission(PERM, RegisteredPermValue.OP, "Player logger permisssions");
        APIRegistry.perms.registerPermission(PERM_WAND, RegisteredPermValue.OP, "Allow usage of player loggger wand (clock)");
    }

    @SubscribeEvent
    public void serverStopped(FEModuleServerStoppedEvent e)
    {
        if (logger != null)
            logger.close();
    }

    public static PlayerLogger getLogger()
    {
        return logger;
    }

}
