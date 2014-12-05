package com.forgeessentials.scripting.macros;

import java.io.File;
import java.io.IOException;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.scripting.ModuleScripting;
import com.forgeessentials.util.FunctionHelper;

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
            throw new CommandException("Missing macro name argument.");
        File scriptFile = new File(macroDir, args[0] + ".txt");
        try
        {
            MacroReader.run(scriptFile, sender, FunctionHelper.dropFirstString(args));
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
}
