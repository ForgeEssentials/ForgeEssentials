package com.forgeessentials.remote.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.remote.ModuleRemote;
import com.forgeessentials.util.UserIdent;

public class CommandRemoteQr extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "remoteqr";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        String url = "https://chart.googleapis.com/chart?cht=qr&chld=M|4&chs=547x547&chl=" + ModuleRemote.getInstance().getConnectString(new UserIdent(sender));
        url = url.replaceAll("\\|", "%7C");
        // @formatter:off
        sender.addChatMessage(IChatComponent.Serializer.func_150699_a("{"
                + "text:\"\","
                + "extra:["
                + " {"
                + "  text:\"Click here for QR code\","
                + "  color:red,"
                + "  underlined:true,"
                + "  background:yellow,"
                + "  clickEvent:{"
                + "   action:open_url,"
                + "   value:\"" + url + "\""
                + "  }"
                + " }"
                + "]}"));
        // @formatter:on
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleRemote.PERM;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/remoteqr: Prints a link remote access QR code";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

}
