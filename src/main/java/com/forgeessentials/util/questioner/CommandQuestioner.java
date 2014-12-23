package com.forgeessentials.util.questioner;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.util.ArrayList;
import java.util.List;

public class CommandQuestioner extends ForgeEssentialsCommandBase
{
    public boolean status;

    public CommandQuestioner(boolean status)
    {
        this.status = status;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        QuestionCenter.processAnswer(sender, status);
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<String> getCommandAliases()
    {
        ArrayList<String> list = new ArrayList<String>();
        if (status)
        {
            list.add("accept");
            list.add("allow");
            list.add("give");
        }
        else
        {
            list.add("decline");
            list.add("deny");
            list.add("take");
        }
        return list;
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
    public String getCommandName()
    {
        if (status) return "yes";
        return "no";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        if (status) return "/yes Reply yes to a question.";
        return "/no Reply no to a question.";
    }
}
