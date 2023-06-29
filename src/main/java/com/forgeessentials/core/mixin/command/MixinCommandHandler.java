package com.forgeessentials.core.mixin.command;

//
//import net.minecraft.command.CommandSource;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraftforge.server.permission.PermissionAPI;
//
//import com.forgeessentials.api.UserIdent;
//import com.forgeessentials.core.misc.PermissionManager;
//import com.forgeessentials.util.CommandUtils;
//import com.forgeessentials.util.DoAsCommandSender;
//
////@Mixin(CommandHandler.class)
//public class MixinCommandHandler
//{
//
//    public boolean checkPerms(String commandNode, CommandSource sender)
//    {
//        String node = PermissionManager.getCommandPermission(commandNode);
//        if (CommandUtils.GetSource(sender) instanceof DoAsCommandSender)
//        {
//            if (!((DoAsCommandSender) CommandUtils.GetSource(sender)).getIdent().isPlayer())
//            {
//                if (((DoAsCommandSender) CommandUtils.GetSource(sender)).getIdent().isNpc())
//                {
//                    return PermissionAPI.hasPermission(((DoAsCommandSender) CommandUtils.GetSource(sender)).getIdent().getGameProfile(), node, null);
//                }
//                else
//                {
//                    return true;
//                }
//            }
//            else
//            {
//                return PermissionAPI.hasPermission(((DoAsCommandSender) CommandUtils.GetSource(sender)).getIdent().getPlayer(), node);
//            }
//        }
//
//        if (sender.getEntity() instanceof PlayerEntity)
//        {
//            return PermissionAPI.hasPermission((PlayerEntity) sender.getEntity(), node);
//        }
//        else
//        {
//            UserIdent ident = UserIdent.get(sender);
//            return PermissionAPI.hasPermission(ident.getGameProfile(), node, null);
//        }
//    }
//}