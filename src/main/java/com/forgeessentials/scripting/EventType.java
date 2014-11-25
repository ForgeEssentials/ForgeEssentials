package com.forgeessentials.scripting;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.scripting.macros.MacroReader;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public enum EventType {
    LOGIN("login"),
    RESPAWN("respawn"),
    ZONECHANGE("zonechange");

    protected File group;
    protected File player;

    private EventType(String name)
    {
        this.group = new File(ModuleScripting.moduleDir, name + "/group");
        this.player = new File(ModuleScripting.moduleDir, name + "/player");
    }

    protected void mkdirs() throws Exception
    {
        this.group.mkdirs();
        this.player.mkdirs();
    }

    public static void run(EntityPlayer player, EventType event)
    {
        OutputHandler.felog.info("Running command scripts for player " + player.getCommandSenderName());

        //  run player scripts
        try
        {
            File pscript = new File(event.player, player.getCommandSenderName() + ".txt");
            OutputHandler.felog.info("Reading command script file " + pscript.getAbsolutePath());
            MacroReader.run(pscript, player);

        }
        catch (Exception e)
        {
            OutputHandler.felog.warning("Could not find command script for player " + player.getCommandSenderName() + ", ignoring!");
        }
        // now run group scripts - must be global
        try
        {
            File gscript = new File(event.group, APIRegistry.perms.getPrimaryGroup(new UserIdent(player)) + ".txt");
            OutputHandler.felog.info("Reading command script file " + gscript.getAbsolutePath());
            MacroReader.run(gscript, player);
        }
        catch (Exception e)
        {
            OutputHandler.felog.warning("Could not find command script for group " + APIRegistry.perms.getPrimaryGroup(new UserIdent(player)).toString() + ", ignoring!");
        }
    }

}
