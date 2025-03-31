package com.forgeessentials.commands.item;

import java.lang.ref.WeakReference;

import net.minecraft.block.BlockWorkbench;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commands.util.ContainerCheatyWorkbench;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

public class CommandCraft extends ForgeEssentialsCommandBase
{

    protected WeakReference<EntityPlayer> lastPlayer = new WeakReference<>(null);

    public CommandCraft()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "craft";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/craft Open a crafting window.";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".craft";
    }

    @SubscribeEvent
    public void playerOpenContainerEvent(PlayerContainerEvent.Open event)
    {
        if (event.getContainer().canInteractWith(event.getEntityPlayer()) == false && lastPlayer.get() == event.getEntityPlayer())
        {
            event.setResult(Result.ALLOW);
        }
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP player, String[] args) throws CommandException
    {
        lastPlayer = new WeakReference<>(player);
        player.displayGui(new BlockWorkbench.InterfaceCraftingTable(player.world, player.getPosition()) {
            @Override public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
            {
                return new ContainerCheatyWorkbench(playerInventory, player.world);
            }
        });
    }

}
