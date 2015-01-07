package com.forgeessentials.scripting;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.FunctionHelper;
import com.google.common.io.Files;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MacroCommand extends ForgeEssentialsCommandBase
{
    private File macroDir;

    public MacroCommand()
    {
        macroDir = new File(ModuleScripting.moduleDir, "macros/");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 1)
        {
            runArgRetriever(sender, macroDir);
        }
        File scriptFile = new File(macroDir, args[0] + ".txt");
        try
        {
            ScriptParser.run(scriptFile, sender, FunctionHelper.dropFirstString(args));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new CommandException("Error running macro!");
        }

    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.script.macro";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandName()
    {
        return "macro";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/macro <name> [args] Run a macro.";
    }

    public static Map<String, String> argMap = new HashMap<String, String>();

    // this is a limited parser that only looks for the special argument-defining lines
    public static void runArgRetriever(ICommandSender sender, File baseDir)
    {
        for (File macroFile : baseDir.listFiles())
        {
            try
            {
                ArrayList<String> scriptargs = new ArrayList<>();
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
                    reader.close();
                    streamReader.close();
                    stream.close();
                }

                boolean first = true;
                String put = "";

                for (String argCode : scriptargs)
                {
                    put = put + (first ? "<" + argCode + ">" : " <" + argCode + ">");
                    first = false;
                }
                argMap.put(Files.getNameWithoutExtension(macroFile.getName()), put);
            }
            catch (IOException e)
            {

            }

        }

    }
}
