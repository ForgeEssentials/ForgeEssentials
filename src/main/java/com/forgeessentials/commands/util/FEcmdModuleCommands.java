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

    // ---------------------------
    // config interaction
    // ---------------------------

    /**
     * Loads configuration for the command.
     * Remember to call super.loadConfig if you overwrite this method.
     */
    public void loadConfig(Configuration config, String category)
    {
        config.addCustomCategoryComment(category, getPermissionNode());
        for (String alias : config.get(category, "aliases", getDefaultAliases()).getStringList())
        {
            aliases.add(alias);
        }
    }

    @Override
    public List<String> getCommandAliases()
    {
        return aliases;
    }

    /**
     * Returns a list of default aliases, that will be added to the configuration on firstrun
     */
    public String[] getDefaultAliases()
    {
        return new String[] { };
    }

    /**
     * You don't need to register the commandpermission.
     */
    public void registerExtraPermissions() {
    }

    /* 
     * Returns the permission node based on the command name
     */
    public String getPermissionNode()
    {
        return "fe.commands." + getCommandName();
    }

	/* 
	 * Check, if the command-sender can use the command.
	 * This checks, if the command has been invoked by a player, a command-block or by console.
	 * @see com.forgeessentials.core.commands.ForgeEssentialsCommandBase#canCommandSenderUseCommand(net.minecraft.command.ICommandSender)
	 */
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		if (sender instanceof EntityPlayer) {
			if (!enabledForPlayer)
				return false;
			return canPlayerUseCommand((EntityPlayer) sender);
		} else if (sender instanceof TileEntityCommandBlock) {
			if (!enabledForCmdBlock)
				return false;
			return canCommandBlockUseCommand((TileEntityCommandBlock) sender);
		} else {
			if (!enabledForConsole)
				return false;
			return canConsoleUseCommand();
		}
	}

    // ---------------------------
    // command usage
    // ---------------------------

    /**
     * Can the command be used by a command-block?
     */
    public boolean usableByCmdBlock()
    {
        return canConsoleUseCommand();
    }

    /**
     * Can the command be used by a player?
     */
    public boolean usableByPlayer()
    {
        return true;
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
