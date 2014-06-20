package com.forgeessentials.snooper;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.ChatUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class CommandReloadQuery extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "queryreload";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        reload(sender);
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        reload(sender);
    }

    public void reload(ICommandSender sender)
    {
        ChatUtils.sendMessage(sender, "Killing old one....");
        ModuleSnooper.stop();
        ChatUtils.sendMessage(sender, "Making new one....");
        ModuleSnooper.start();
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getCommandPerm()
    {
        return "fe.snooper" + getCommandName();
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/queryreload Reload queries from the SQL database";
    }

    @Override
    public RegGroup getReggroup()
    {

        return RegGroup.OWNERS;
    }

}
