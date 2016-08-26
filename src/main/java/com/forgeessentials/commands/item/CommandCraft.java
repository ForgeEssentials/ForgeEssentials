package com.forgeessentials.commands.item;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commands.util.ContainerCheatyWorkbench;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

public class CommandCraft extends ForgeEssentialsCommandBase
{
    
    @Override
    public String getCommandName()
    {
        return "fecraft";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "craft" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/craft Open a crafting window.";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".craft";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        EntityPlayerMP player = sender;
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

}
