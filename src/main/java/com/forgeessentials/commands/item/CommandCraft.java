package com.forgeessentials.commands.item;

import net.minecraft.block.BlockWorkbench;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.permission.PermissionLevel;

import java.lang.ref.WeakReference;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commands.util.ContainerCheatyWorkbench;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

public class CommandCraft extends ForgeEssentialsCommandBase
{
 
    protected WeakReference<EntityPlayer> lastPlayer = new WeakReference<>(null);

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
    	sender.displayGui(new BlockWorkbench.InterfaceCraftingTable(sender.worldObj, sender.getPosition()) {
            @Override public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
            {
                return new ContainerCheatyWorkbench(playerInventory, sender.worldObj);
            }
        });
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
    }

}
