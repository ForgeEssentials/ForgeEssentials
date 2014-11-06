package com.forgeessentials.commands.admin;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commands.util.FEcmdModuleCommands;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class CommandServerDo extends FEcmdModuleCommands {

    @Override
    public String getCommandName()
    {
        return "serverdo";
    }

    @Override
    public void processCommandPlayer(EntityPlayer player, String[] args)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && args.length >= 1)
        {
            String cmd = args[0];
            for (int i = 1; i < args.length; ++i)
            {
                cmd = cmd + " " + args[i];
            }
            MinecraftServer.getServer().getCommandManager().executeCommand(new DummyCommandSender(player), cmd);
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/serverdo Run a command as the console.";
    }

    public class DummyCommandSender implements ICommandSender {

        private EntityPlayer player;

        public DummyCommandSender(EntityPlayer player)
        {
            this.player = player;
        }

        @Override public String getCommandSenderName()
        {
            return "FEServerDo";
        }

        @Override public IChatComponent func_145748_c_()
        {
            return null;
        }

        @Override public void addChatMessage(IChatComponent iChatComponent)
        {
            player.addChatMessage(iChatComponent);

        }

        @Override public boolean canCommandSenderUseCommand(int i, String s)
        {
            return true;
        }

        @Override public ChunkCoordinates getPlayerCoordinates()
        {
            return null;
        }

        @Override public World getEntityWorld()
        {
            return null;
        }
    }
}
