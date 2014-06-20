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

public class CommandUndo extends WorldControlCommandBase {

    public CommandUndo()
    {
        super(true);
    }

    @Override
    public String getName()
    {
        return "undo";
    }

    @Override
    public void processCommandPlayer(EntityPlayer player, String[] args)
    {
        BackupArea back = PlayerInfo.getPlayerInfo(player.username).getNextUndo();

        if (back == null)
        {
            OutputHandler.chatError(player, "Nothing to undo.");
            return;
        }

        TaskRegistry.registerTask(new TickTaskSetBackup(player, back, false));

        ChatUtils.sendMessage(player, "Working on undo.");
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/undo";
    }

    @Override
    public RegGroup getReggroup()
    {

        return RegGroup.OWNERS;
    }
}
