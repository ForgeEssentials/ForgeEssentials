package com.forgeessentials.scripting.macros;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.scripting.ModuleScripting;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.io.File;
import java.io.IOException;

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
        File scriptFile = new File(macroDir, args[0] + ".txt");
        try
        {
            MacroReader.run(scriptFile, sender, FunctionHelper.dropFirstString(args));
        }
        catch (IOException e)
        {
            OutputHandler.chatError(sender, "Could not execute command script!");
            e.printStackTrace();
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
