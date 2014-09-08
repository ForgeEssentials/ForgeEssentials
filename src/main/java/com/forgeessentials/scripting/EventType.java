package com.forgeessentials.scripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.OutputHandler;

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
        ArrayList<String> scripts = new ArrayList<String>();
        OutputHandler.felog.info("Running command scripts for player " + player.getCommandSenderName());

        //  run player scripts
        try
        {
            File pscript = new File(event.player, player.getPersistentID() + ".txt");

            OutputHandler.felog.info("Reading command script file " + pscript.getAbsolutePath());
            FileInputStream stream = new FileInputStream(pscript);
            InputStreamReader streamReader = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(streamReader);
            String read = reader.readLine();
            while (read != null)
            {
                // ignore the comment things...
                if (read.startsWith("#"))
                {
                    read = reader.readLine();
                    continue;
                }

                // add to the rules list.
                scripts.add(read);

                // read the next string
                read = reader.readLine();

                reader.close();
                streamReader.close();
                stream.close();

            }
        }
        catch (Exception e)
        {
            OutputHandler.felog.warning("Could not find command script for player " + player.getCommandSenderName() + ", ignoring!");
        }
        // now run group scripts - must be global
        try
        {
            File gscript = new File(event.group, APIRegistry.perms.getHighestGroup(player).name + ".txt");
            OutputHandler.felog.info("Reading command script file " + gscript.getAbsolutePath());
            FileInputStream stream = new FileInputStream(gscript);
            InputStreamReader streamReader = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(streamReader);
            String read = reader.readLine();
            while (read != null)
            {
                // ignore the comment things...
                if (read.startsWith("#"))
                {
                    read = reader.readLine();
                    continue;
                }

                // add to the rules list.
                scripts.add(read);

                // read the next string
                read = reader.readLine();

                reader.close();
                streamReader.close();
                stream.close();

            }
        }
        catch (Exception e)
        {
            OutputHandler.felog.warning("Could not find command script for group " + APIRegistry.perms.getHighestGroup(player).toString() + ", ignoring!");
        }
        finally
        {
            for (Object s : scripts.toArray())
            {
                String s1 = s.toString();
                MinecraftServer.getServer().getCommandManager().executeCommand(player, s1);
                OutputHandler.felog.info("Successfully run command scripts for player " + player.getCommandSenderName());
            }
        }
    }

}
