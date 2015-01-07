package com.forgeessentials.scripting;

import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ScriptParser
{
    public static void run(File macroFile, ICommandSender sender, String[] args) throws IOException
    {
        ArrayList<String> scripts = new ArrayList<String>();
        ArrayList<String> scriptargs = new ArrayList<>();

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

            // expected syntax: $ <arg number> <argCode>
            if (read.startsWith("$"))
            {
                String[] argCode = FunctionHelper.dropFirstString(read.split(" "));
                int order = Integer.parseInt(argCode[0]);
                scriptargs.add(order, argCode[1]);
            }

            read = read.replaceAll("%p", sender.getCommandSenderName());

            if (sender instanceof EntityPlayerMP)
            {
                EntityPlayerMP player = (EntityPlayerMP) sender;

                read = read.replaceAll("%px", Integer.toString(player.getPlayerCoordinates().posX));
                read = read.replaceAll("%py", Integer.toString(player.getPlayerCoordinates().posY));
                read = read.replaceAll("%pz", Integer.toString(player.getPlayerCoordinates().posZ));
            }

            for (int i = 0; i < scriptargs.size(); i++) {
                read = read.replaceAll("%" + scriptargs.get(i), args[i]);
            }

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
            OutputHandler.chatNotification(sender, "Successfully run command scripts for " + sender.getCommandSenderName());
        }

    }
}
