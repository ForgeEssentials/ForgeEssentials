package com.forgeessentials.jscripting.command;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.jscripting.ModuleJScripting;
import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.util.CommandParserArgs;

public class CommandJScriptCommand extends ParserCommandBase
{

    public final ScriptInstance script;

    public final String name;

    public String usage;

    public String permission;

    private boolean opOnly;

    public CommandJScriptCommand(ScriptInstance script, String name, String usage, String permission, boolean opOnly)
    {
        this.script = script;
        this.name = name;
        this.opOnly = opOnly;
        this.usage = usage != null ? usage : "/" + name + ": scripted command - no description";
        this.permission = permission != null ? permission : ModuleJScripting.PERM + ".command." + name;
    }

    public CommandJScriptCommand(ScriptInstance script, String name, String usage)
    {
        this(script, name, usage, null, true);
    }

    public CommandJScriptCommand(ScriptInstance script, String name)
    {
        this(script, name, null);
    }

    @Override
    public String getCommandName()
    {
        return name;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return usage;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return opOnly ? PermissionLevel.OP : PermissionLevel.TRUE;
    }

    @Override
    public String getPermissionNode()
    {
        return permission;
    }

    @Override
    public void parse(CommandParserArgs arguments)
    {
        CommandJScript.runCommand(arguments, name);
    }

}