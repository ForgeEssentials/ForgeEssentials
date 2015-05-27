package com.forgeessentials.commands.player;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;

public class CommandAFK extends FEcmdModuleCommands
{

    public static final String PERM = "fe.commands.afk";

    public static final String PERM_ANNOUNCE = PERM + ".announce";

    public static final String PERM_WARMUP = PERM + ".warmup";

    public static final String PERM_AUTOTIME = PERM + ".autotime";

    public static final String PERM_AUTOKICK = PERM + ".autokick";

    @Override
    public String getCommandName()
    {
        return "afk";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/afk: Mark yourself as away.";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(PERM_ANNOUNCE, RegisteredPermValue.TRUE);
        APIRegistry.perms.registerPermissionProperty(PERM_WARMUP, "10", "Time a player needs to wait before he can go afk with /afk");
        APIRegistry.perms.registerPermissionProperty(PERM_AUTOTIME, "120", "Auto afk time in seconds");
        APIRegistry.perms.registerPermission(PERM_AUTOKICK, RegisteredPermValue.FALSE, "Automatically kick a player, when he is AFK");
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        UserIdent ident = UserIdent.get(sender);
        int autoTime = FunctionHelper.parseIntDefault(ident.getPermissionProperty(CommandAFK.PERM_AUTOTIME), 60 * 2);
        int warmup = FunctionHelper.parseIntDefault(ident.getPermissionProperty(PERM_WARMUP), 0);
        PlayerInfo.get(sender).setActive(autoTime * 1000 - warmup * 1000);
        OutputHandler.chatConfirmation(sender, Translator.format("Stand still for %d seconds.", warmup));
    }

}
