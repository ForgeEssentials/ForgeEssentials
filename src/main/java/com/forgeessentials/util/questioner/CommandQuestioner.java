package com.forgeessentials.util.questioner;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

public class CommandQuestioner extends ForgeEssentialsCommandBase
{
    private final boolean type;

    public CommandQuestioner(boolean type)
    {
        this.type = type;
    }

    @Override
    public String getCommandName()
    {
        if (type)
            return "yes";
        else
            return "no";
    }

    @Override
    public String[] getDefaultAliases()
    {
        if (type)
            return new String[] { "accept", "allow", "give" };
        else
            return new String[] { "decline", "deny", "take" };
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.questioner";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        if (type)
            return "/yes Reply yes to a question.";
        else
            return "/no Reply no to a question.";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        Questioner.answer(sender, type);
    }

}
