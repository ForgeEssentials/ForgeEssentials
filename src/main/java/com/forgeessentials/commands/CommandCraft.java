package com.forgeessentials.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.util.ContainerCheatyWorkbench;
import com.forgeessentials.commands.util.FEcmdModuleCommands;

public class CommandCraft extends FEcmdModuleCommands {
    @Override
    public String getCommandName()
    {
        return "craft";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        EntityPlayerMP player = (EntityPlayerMP) sender;
        player.getNextWindowId();
        player.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(player.currentWindowId, 1, "Crafting", 9, true));
        player.openContainer = new ContainerCheatyWorkbench(player.inventory, player.worldObj);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addCraftingToCrafters(player);
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/craft Open a crafting window.";
    }
}
