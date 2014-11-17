package com.forgeessentials.commands;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.commands.util.PlayerInvChest;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.util.List;

/**
 * Opens other player inventory.
 *
 * @author Dries007
 */
public class CommandInventorySee extends FEcmdModuleCommands {

    public CommandInventorySee()
    {
    }

    @Override
    public String getCommandName()
    {
        return "invsee";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (!FMLCommonHandler.instance().getEffectiveSide().isServer())
        {
            return;
        }
        EntityPlayerMP player = (EntityPlayerMP) sender;
        EntityPlayerMP victim = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().func_152612_a(args[0]);

        if (player.openContainer != player.inventoryContainer)
        {
            player.closeScreen();
        }
        player.getNextWindowId();

        PlayerInvChest chest = new PlayerInvChest(victim, (EntityPlayerMP) sender);
        player.playerNetServerHandler
                .sendPacket(new S2DPacketOpenWindow(player.currentWindowId, 0, chest.getInventoryName(), chest.getSizeInventory(), true));
        player.openContainer = new ContainerChest(player.inventory, chest);
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
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else
        {
            return null;
        }
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/invsee See a player's inventory.";
    }

}
