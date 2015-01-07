package com.forgeessentials.scripting;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import net.minecraft.entity.player.EntityPlayer;

import java.io.File;

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
            ScriptParser.run(pscript, player, new String[] { });

        }
        catch (Exception e)
        {
            OutputHandler.felog.warning("Could not find command script for player " + player.getCommandSenderName() + ", ignoring!");
        }
        // now run group scripts - must be global
        try
        {
            File gscript = new File(event.group, APIRegistry.perms.getPrimaryGroup(new UserIdent(player)) + ".txt");
            ScriptParser.run(gscript, player, new String[] { });
        }
        catch (Exception e)
        {
            OutputHandler.felog.warning("Could not find command script for group " + APIRegistry.perms.getPrimaryGroup(new UserIdent(player)).toString() + ", ignoring!");
        }
    }

}
