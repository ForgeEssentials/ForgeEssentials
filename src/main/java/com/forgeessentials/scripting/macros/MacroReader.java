package com.forgeessentials.scripting.macros;

import com.forgeessentials.util.OutputHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MacroReader
{
    public static void run(File macroFile, ICommandSender sender) throws IOException
    {
        ArrayList<String> scripts = new ArrayList<String>();

        OutputHandler.felog.info("Reading command script file " + macroFile.getAbsolutePath());
        FileInputStream stream = new FileInputStream(macroFile);
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

        for (Object s : scripts.toArray())
        {
            String s1 = s.toString();
            MinecraftServer.getServer().getCommandManager().executeCommand(sender, s1);
            OutputHandler.felog.info("Successfully run command scripts for " + sender.getCommandSenderName());
        }

    }
}
