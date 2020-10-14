package com.forgeessentials.commands.item;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commands.util.VirtualChest;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;

/**
 * Opens a configurable virtual chest
 */
public class CommandVirtualchest extends ForgeEssentialsCommandBase implements ConfigurableCommand
{
    public static int size = 54;
    public static String name = "Vault 13";

    @Override
    public String getPrimaryAlias()
    {
        return "virtualchest";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "vchest" };
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/vchest Open a virtual chest";
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
        return ModuleCommands.PERM + ".virtualchest";
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP sender, String[] args) throws CommandException
    {
        EntityPlayerMP player = sender;
        if (player.openContainer != player.inventoryContainer)
        {
            player.closeScreen();
        }
        player.getNextWindowId();

        VirtualChest chest = new VirtualChest(player);
        player.displayGUIChest(chest);
    }

    @Override
    public void loadConfig(Configuration config, String category)
    {
        size = config.get(category, "VirtualChestRows", 6, "1 row = 9 slots. 3 = 1 chest, 6 = double chest (max size!).").getInt(6) * 9;
        name = config.get(category, "VirtualChestName", "Vault 13", "Don't use special stuff....").getString();
    }

    @Override
    public void loadData()
    {
        /* do nothing */
    }

}
