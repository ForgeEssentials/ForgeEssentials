package com.forgeessentials.commands.admin;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.util.List;

public class CommandDoAs extends FEcmdModuleCommands {
    @Override
    public String getCommandName()
    {
        return "doas";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length == 0)
        {
            OutputHandler.chatError(sender, getCommandUsage(sender));
        }
        if ((sender instanceof EntityPlayerMP) && args[0].equals("[CONSOLE]"))
        {
            EntityPlayerMP player = (EntityPlayerMP)sender;

            if (PermissionsManager.checkPermission(player, "fe.commands.doas.console"))
            {
                if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && args.length >= 2)
                {
                    String cmd = args[0];
                    for (int i = 1; i < args.length; ++i)
                    {
                        cmd = cmd + " " + args[i];
                    }
                    MinecraftServer.getServer().getCommandManager().executeCommand(new DummyCommandSender(player), cmd);
                }
            }
        }

        StringBuilder cmd = new StringBuilder(args.toString().length());
        for (int i = 1; i < args.length; i++)
        {
            cmd.append(args[i]);
            cmd.append(" ");
        }
        EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
        if (player != null)
        {
            OutputHandler.chatWarning(player, String.format("Player %s is attempting to issue a command as you.", sender.getCommandSenderName()));
            FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager().executeCommand(player, cmd.toString());
            OutputHandler.chatConfirmation(sender, String.format("Successfully issued command as %s", args[0]));
        }
        else
        {
            OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
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

        return "/doas <player> <command> Run a command as another player.";
    }

    @Override
    public void registerExtraPermissions()
    {
        PermissionsManager.registerPermission("fe.commands.doas.console", RegisteredPermValue.OP);
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
