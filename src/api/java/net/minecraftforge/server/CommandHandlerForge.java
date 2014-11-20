package net.minecraftforge.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.util.HashMap;
import java.util.Map;

import static net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue.FALSE;
import static net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue.OP;
import static net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue.TRUE;
import cpw.mods.fml.common.FMLLog;
 
/**
 * Helper class for Forge permissions and command handling.
 *
 */
public class CommandHandlerForge {
    
    private static Map<String, String> permMap = new HashMap<String, String>();
    
    /**
     * Mods, use this!
     * This is a permissions-aware version of FMLServerStartingEvent's registerCommand.
     * You should be using this, and NOT FML's, if you wish to specify a custom permission node
     * @param your ICommand
     * @param node a permission node
     * @param permLevel the default permission level ({@link RegisteredPermValue})
     */
    public static void registerCommand(ICommand command, String node, RegisteredPermValue permLevel)
    {
        if (permMap.containsKey(command))
        {
            FMLLog.warning("Command %s tried to register a duplicate permission!", command);
            return;
        }
        permMap.put(command.getCommandName(), node);
        PermissionsManager.registerPermission(node, permLevel);
        CommandHandler ch = (CommandHandler) MinecraftServer.getServer().getCommandManager();
        ch.registerCommand(command);
    }
    
    /**
     * INTERNAL USE ONLY - used by CommandHandler for auto-registration of commands
     * Mods should be using doPermissionReg(ICommand, String, RegisteredPermValue)
     * @param cmd
     */
    public static void doPermissionReg(ICommand cmd)
    {
        if (cmd.getClass().getName().startsWith("net.minecraft.command") && cmd instanceof CommandBase)
        {
            int level = ((CommandBase) cmd).getRequiredPermissionLevel();
            RegisteredPermValue value;
            
            switch (level)
            {
            case 0: 
                value = TRUE;
                break;
            
            case 1:
            case 2:
            case 3:
            case 4:
                value = OP;
                break;
            default:
                value = FALSE;
            }
            permMap.put(cmd.getCommandName(), "mc." + cmd.getCommandName());
            PermissionsManager.registerPermission("mc." + cmd.getCommandName(), value);
            
        }
        else if (!permMap.containsKey(cmd.getCommandName()))
        {
            permMap.put(cmd.getCommandName(), "cmd." + cmd.getCommandName());
            PermissionsManager.registerPermission("cmd." + cmd.getCommandName(), OP);
        }
    }
    
    /**
     * INTERNAL USE ONLY - used by CommandHandler to check usage permissions
     * If you need to check permissions for non-command objects, or additional permissions for commands,
     * call PermissionsManager directly.
     */
    public static boolean canUse(ICommand command, ICommandSender sender)
    {
        if (sender instanceof EntityPlayerMP && permMap.get(command.getCommandName()) != null)
        {
            return PermissionsManager.checkPermission((EntityPlayerMP) sender, permMap.get(command.getCommandName()));
        }
        else 
            return command.canCommandSenderUseCommand(sender);
    }

 
}