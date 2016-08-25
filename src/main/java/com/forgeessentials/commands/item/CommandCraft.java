package com.forgeessentials.commands.item;

import java.lang.ref.WeakReference;

import net.minecraft.block.BlockWorkbench;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerOpenContainerEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;

public class CommandCraft extends ForgeEssentialsCommandBase
{

    protected WeakReference<EntityPlayer> lastPlayer = new WeakReference<>(null);

    public CommandCraft()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void playerOpenContainerEvent(PlayerOpenContainerEvent event)
    {
        if (event.canInteractWith == false && lastPlayer.get() == event.entityPlayer)
        {
            event.setResult(Result.ALLOW);
        }
    }

    @Override
    public String getCommandName()
    {
        return "craft";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException
    {
        EntityPlayerMP player = sender;
        player.displayGui(new BlockWorkbench.InterfaceCraftingTable(player.worldObj, player.getPosition()));
        lastPlayer = new WeakReference<EntityPlayer>(player);
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args) throws CommandException
    {
        throw new TranslatedCommandException(FEPermissions.MSG_NO_CONSOLE_COMMAND);
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
    public String getCommandUsage(ICommandSender sender)
    {
        return "/craft Open a crafting window.";
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + "." + getCommandName();
    }

}
