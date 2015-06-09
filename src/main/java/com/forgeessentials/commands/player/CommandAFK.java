package com.forgeessentials.commands.player;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.commands.CommandFeSettings;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.ServerUtil;

public class CommandAFK extends FEcmdModuleCommands
{

    public static final String PERM = "fe.commands.afk";

    public static final String PERM_ANNOUNCE = PERM + ".announce";

    public static final String PERM_WARMUP = PERM + ".warmup";

    public static final String PERM_AUTOTIME = PERM + ".autotime";

    public static final String PERM_AUTOKICK = PERM + ".autokick";

    public CommandAFK()
    {
        CommandFeSettings.addAlias("afktime", PERM_AUTOTIME);
    }

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
        APIRegistry.perms.registerPermissionProperty(PERM_AUTOTIME, "480", "Auto afk time in seconds. Set to 0 to disable.");
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

        // expected syntax: /afk timeout <group|player> <timeout>
        // to set custom afk timeout for yourself, replace <player> with your own username
        if (args[0].equalsIgnoreCase("timeout"))
        {
            UserIdent applyTo = UserIdent.get(args[1], true);
            if (applyTo != null)
            {
                APIRegistry.perms.setPlayerPermissionProperty(applyTo, PERM_AUTOTIME, args[2]);
            }
            else
            {
                APIRegistry.perms.setGroupPermissionProperty(args[1], PERM_AUTOTIME, args[2]);
            }
        }
        // expected syntax: /afk timeout <group|player> [true|false}
        else if (args[0].equalsIgnoreCase("autokick"))
        {
            UserIdent applyTo = UserIdent.get(args[1], true);
            if (applyTo != null)
            {
                APIRegistry.perms.setPlayerPermissionProperty(applyTo, PERM_AUTOKICK, args[2]);
            }
            else
            {
                APIRegistry.perms.setGroupPermissionProperty(args[1], PERM_AUTOKICK, args[2]);
            }
        }
        else
        {
            int autoTime = ServerUtil.parseIntDefault(ident.getPermissionProperty(CommandAFK.PERM_AUTOTIME), 60 * 2);
            int warmup = ServerUtil.parseIntDefault(ident.getPermissionProperty(PERM_WARMUP), 0);
            PlayerInfo.get(sender).setActive(autoTime * 1000 - warmup * 1000);
            OutputHandler.chatConfirmation(sender, Translator.format("Stand still for %d seconds.", warmup));
        }
    }

}
