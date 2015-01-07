package com.forgeessentials.scripting;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.scripting.commands.ShortcutCommands;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.command.ICommandSender;

import java.io.File;

@FEModule(name = "Scripting", parentMod = ForgeEssentials.class, isCore = false)
public class ModuleScripting
{

    @FEModule.ModuleDir
    public static File moduleDir = new File(ForgeEssentials.getFEDirectory(), "scripting/");

    @SubscribeEvent
    public void preInit(FEModulePreInitEvent e)
    {
        OutputHandler.felog.info("Scripts are being read from " + moduleDir.getAbsolutePath());
        try
        {
            moduleDir.mkdirs();
            for (EventType et : EventType.values())
            {
                et.mkdirs();
            }

        }
        catch (Exception e1)
        {
            OutputHandler.felog.warning("Could not setup scripting folders - you might have to do it yourself.");
        }
    }

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        ShortcutCommands.loadConfig(ForgeEssentials.getFEDirectory());
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        FunctionHelper.registerServerCommand(new CommandScript());
        FunctionHelper.registerServerCommand(new TimedTaskManager());
        FunctionHelper.registerServerCommand(new MacroCommand());
        ShortcutCommands.load();
    }

    @FEModule.Reload
    public void reload(ICommandSender sender)
    {
        ShortcutCommands.parseConfig();
        ShortcutCommands.load();
    }
}
