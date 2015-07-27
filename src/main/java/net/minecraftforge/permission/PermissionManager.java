package net.minecraftforge.permission;

import java.util.Map;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class PermissionManager
{

    public static final String DEFAULT_COMMAND_NODE = "command.";

    protected static IPermissionProvider permissionProvider = new DefaultPermissionProvider();

    protected static PermissionManager instance = new PermissionManager();

    /* ------------------------------------------------------------ */

    public PermissionManager()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void commandEvent(CommandEvent event)
    {
        if (!checkPermission(event.sender, event.command))
        {
            event.setCanceled(true);
            ChatComponentTranslation msg = new ChatComponentTranslation("commands.generic.permission", new Object[0]);
            msg.getChatStyle().setColor(EnumChatFormatting.RED);
            event.sender.addChatMessage(msg);
        }
    }

    /* ------------------------------------------------------------ */

    public static void setPermissionProvider(IPermissionProvider provider) throws IllegalStateException
    {
        if (provider == null)
            provider = new DefaultPermissionProvider();
        if (!(permissionProvider instanceof DefaultPermissionProvider))
        {
            FMLLog.severe("Registration of permission provider %s overwriting permission provider %s!", provider.getClass().getName(), permissionProvider
                    .getClass().getName());
        }
        permissionProvider = provider;
        FMLLog.fine("Registered permission provider %s", permissionProvider.getClass().getName());
    }

    public static IPermissionProvider getPermissionProvider()
    {
        return permissionProvider;
    }

    /* ------------------------------------------------------------ */

    public static String getCommandPermission(ICommand command)
    {
        if (command instanceof PermissionObject)
        {
            String permission = ((PermissionObject) command).getPermissionNode();
            if (permission != null)
                return permission;
        }
        return DEFAULT_COMMAND_NODE + command.getCommandName();
    }

    public static PermissionLevel getCommandLevel(ICommand command)
    {
        if (command instanceof PermissionObject)
            return ((PermissionObject) command).getPermissionLevel();
        if (command instanceof CommandBase)
            return PermissionLevel.fromInteger(((CommandBase) command).getRequiredPermissionLevel());
        return PermissionLevel.OP;
    }

    /**
     * <b>FOR INTERNAL USE ONLY</b> <br>
     * This method should not be called directly, but instead is called by forge upon registration of a new command
     * 
     * @param command
     */
    public static void registerCommandPermission(ICommand command)
    {
        String permission = getCommandPermission(command);
        PermissionLevel level = getCommandLevel(command);
        registerPermission(permission, level);
    }

    /**
     * <b>FOR INTERNAL USE ONLY</b> <br>
     * TODO This method should be removed in the PR
     * 
     * @param command
     */
    public static void registerCommandPermissions()
    {
        @SuppressWarnings("unchecked")
        Map<String, ICommand> commands = MinecraftServer.getServer().getCommandManager().getCommands();
        for (ICommand command : commands.values())
            registerCommandPermission(command);
    }

    /* ------------------------------------------------------------ */

    public static void registerPermission(String permission, PermissionLevel level)
    {
        permissionProvider.registerPermission(permission, level);
    }

    public static boolean checkPermission(PermissionContext context, String permission)
    {
        return permissionProvider.checkPermission(context, permission);
    }

    public static boolean checkPermission(EntityPlayer player, String permission)
    {
        return checkPermission(new PermissionContext(player), permission);
    }

    public static boolean checkPermission(ICommandSender sender, ICommand command, String permission)
    {
        return checkPermission(new PermissionContext(sender, command), permission);
    }

    public static boolean checkPermission(ICommandSender sender, ICommand command)
    {
        return checkPermission(new PermissionContext(sender, command), getCommandPermission(command));
    }

    public static boolean checkPermission(ICommandSender sender, String permission)
    {
        return checkPermission(new PermissionContext(sender), permission);
    }

}
