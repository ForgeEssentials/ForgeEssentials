package com.forgeessentials.worldcontrol.commands;

//Depreciated

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.PlayerInfo;
import com.forgeessentials.util.BackupArea;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.tasks.TaskRegistry;
import com.forgeessentials.worldcontrol.TickTasks.TickTaskSetBackup;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class CommandRedo extends WorldControlCommandBase {

    public CommandRedo()
    {
        super(true);
    }

    @Override
    public String getName()
    {
        return "redo";
    }

    @Override
    public void processCommandPlayer(EntityPlayer player, String[] args)
    {
        BackupArea back = PlayerInfo.getPlayerInfo(player.username).getNextRedo();

        if (back == null)
        {
            OutputHandler.chatError(player, "Nothing to redo!");
            return;
        }

        TaskRegistry.registerTask(new TickTaskSetBackup(player, back, true));

        ChatUtils.sendMessage(player, "Working on redo");
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/redo";
    }

    @Override
    public RegGroup getReggroup()
    {

        return RegGroup.OWNERS;
    }
}
