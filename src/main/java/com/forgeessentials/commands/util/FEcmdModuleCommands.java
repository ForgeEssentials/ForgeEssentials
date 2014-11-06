package com.forgeessentials.commands.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

public abstract class FEcmdModuleCommands extends ForgeEssentialsCommandBase {

    private boolean enabledForCmdBlock = true;
    private boolean enabledForConsole = true;
    private boolean enabledForPlayer = true;
    private List<String> aliases = new ArrayList<String>();

    // ------------------------------------------------------------
    // Command usage

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        if (!super.canCommandSenderUseCommand(sender))
            return false;

        if (sender instanceof EntityPlayer)
            if (!enabledForPlayer)
                return canCommandSenderUseCommandException("This command is disabled for players");

        if (sender instanceof TileEntityCommandBlock)
            if (!enabledForCmdBlock)
                return canCommandSenderUseCommandException("This command is disabled for command-blocks");

        if (!enabledForConsole)
            return canCommandSenderUseCommandException("This command is disabled for console");

        return true;
    }

    // ------------------------------------------------------------
    // Command configuration

    /**
     * Loads configuration for the command.
     */
    public void loadConfig(Configuration config, String category)
    {
        config.addCustomCategoryComment(category, getPermissionNode());
        for (String alias : config.get(category, "aliases", getDefaultAliases()).getStringList())
            aliases.add(alias);
    }

    @Override
    public List<String> getCommandAliases()
    {
        return aliases;
    }

    /**
     * Returns a list of default aliases, that will be added to the configuration on first run
     */
    public String[] getDefaultAliases()
    {
        return new String[] {};
    }

    // ------------------------------------------------------------
    // Permissions

    /**
     * Register other permissions in addition to the command-permission.
     */
    public void registerExtraPermissions()
    {
    }

    /*
     * Returns the permission node based on the command name
     */
    @Override
    public String getPermissionNode()
    {
        return "fe.commands." + getCommandName();
    }

    // ------------------------------------------------------------
    // Command usage

    /**
     * Can the command be used by a player?
     */
    public boolean usableByPlayer()
    {
        return true;
    }

    /**
     * Can the command be used by a command-block?
     */
    public boolean usableByCmdBlock()
    {
        return canConsoleUseCommand();
    }

    /**
     * Is the command allowed to be used by command-blocks?
     */
    public boolean isEnabledForCmdBlock()
    {
        return enabledForCmdBlock;
    }

    /**
     * Is the command allowed to be used by console?
     */
    public boolean isEnabledForConsole()
    {
        return enabledForConsole;
    }

    /**
     * Is the command allowed to be used by a player?
     */
    public boolean isEnabledForPlayer()
    {
        return enabledForPlayer;
    }

    /**
     * Is the command allowed to be used by command-blocks?
     */
    public void setEnabledForCmdBlock(boolean enabledForCmdBlock)
    {
        this.enabledForCmdBlock = enabledForCmdBlock;
    }

    /**
     * Is the command allowed to be used by console?
     */
    public void setEnabledForConsole(boolean enabledForConsole)
    {
        this.enabledForConsole = enabledForConsole;
    }

    /**
     * Is the command allowed to be used by a player?
     */
    public void setEnabledForPlayer(boolean enabledForPlayer)
    {
        this.enabledForPlayer = enabledForPlayer;
    }

}
