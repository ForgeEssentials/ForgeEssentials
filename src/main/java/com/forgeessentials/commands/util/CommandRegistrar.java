package com.forgeessentials.commands.util;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.admin.CommandModlist;
import com.forgeessentials.commands.tools.CommandPing;
import com.forgeessentials.commands.admin.CommandBubble;
import com.forgeessentials.commands.admin.CommandDoAs;
import com.forgeessentials.commands.admin.CommandInventorySee;
import com.forgeessentials.commands.admin.CommandServerSettings;
import com.forgeessentials.commands.admin.CommandTempBan;
import com.forgeessentials.commands.game.CommandBurn;
import com.forgeessentials.commands.game.CommandButcher;
import com.forgeessentials.commands.game.CommandEnchant;
import com.forgeessentials.commands.game.CommandGameMode;
import com.forgeessentials.commands.game.CommandKit;
import com.forgeessentials.commands.game.CommandPush;
import com.forgeessentials.commands.game.CommandRules;
import com.forgeessentials.commands.game.CommandSmite;
import com.forgeessentials.commands.game.cheat.CommandCraft;
import com.forgeessentials.commands.game.cheat.CommandEnderchest;
import com.forgeessentials.commands.game.cheat.CommandHeal;
import com.forgeessentials.commands.game.cheat.CommandPotion;
import com.forgeessentials.commands.game.cheat.CommandRepair;
import com.forgeessentials.commands.game.cheat.CommandVirtualchest;
import com.forgeessentials.commands.game.player.CommandGetCommandBook;
import com.forgeessentials.commands.game.player.CommandKill;
import com.forgeessentials.commands.game.player.CommandNoClip;
import com.forgeessentials.commands.game.player.CommandSpeed;
import com.forgeessentials.commands.game.world.CommandTime;
import com.forgeessentials.commands.game.world.CommandWeather;
import com.forgeessentials.commands.tools.CommandAFK;
import com.forgeessentials.commands.tools.CommandBind;
import com.forgeessentials.commands.tools.CommandCapabilities;
import com.forgeessentials.commands.tools.CommandDrop;
import com.forgeessentials.commands.tools.CommandFindblock;
import com.forgeessentials.commands.tools.CommandLocate;
import com.forgeessentials.commands.tools.CommandRemove;
import com.forgeessentials.commands.tools.CommandRename;
import com.forgeessentials.commands.tools.CommandSeen;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import net.minecraftforge.common.config.Configuration;

import java.util.ArrayList;

public class CommandRegistrar
{

    public static ArrayList<FEcmdModuleCommands> cmdList;

    static
    {
        cmdList = new ArrayList<FEcmdModuleCommands>();
        cmdList.add(new CommandTime());
        cmdList.add(new CommandEnchant());
        cmdList.add(new CommandLocate());
        cmdList.add(new CommandRules());
        cmdList.add(new CommandModlist());
        cmdList.add(new CommandButcher());
        cmdList.add(new CommandRemove());
        cmdList.add(new CommandAFK());
        cmdList.add(new CommandKit());
        cmdList.add(new CommandEnderchest());
        cmdList.add(new CommandVirtualchest());
        cmdList.add(new CommandCapabilities());
        cmdList.add(new CommandCraft());
        cmdList.add(new CommandPing());
        cmdList.add(new CommandInventorySee());
        cmdList.add(new CommandSmite());
        cmdList.add(new CommandBurn());
        cmdList.add(new CommandPotion());
        cmdList.add(new CommandRepair());
        cmdList.add(new CommandHeal());
        cmdList.add(new CommandKill());
        cmdList.add(new CommandGameMode());
        cmdList.add(new CommandDoAs());
        cmdList.add(new CommandServerSettings());
        cmdList.add(new CommandGetCommandBook());
        cmdList.add(new CommandWeather());
        cmdList.add(new CommandBind());
        cmdList.add(new CommandRename());
        // cmdList.add(new CommandVanish());
        cmdList.add(new CommandPush());
        cmdList.add(new CommandDrop());
        cmdList.add(new CommandFindblock());
        cmdList.add(new CommandNoClip());
        cmdList.add(new CommandBubble());
        cmdList.add(new CommandSpeed());
        cmdList.add(new CommandSeen());
        cmdList.add(new CommandTempBan());
    }

    public static void commandConfigs(Configuration config)
    {
        config.load();

        // Add categories
        config.addCustomCategoryComment("commands", "All FE commands will have a config space here.");
        config.addCustomCategoryComment("CommandBlock", "Toggle server wide command block usage here.");
        config.addCustomCategoryComment("Player", "Toggle server wide player usage here.");
        config.addCustomCategoryComment("Console", "Toggle console usage here.");

        for (FEcmdModuleCommands fecmd : cmdList)
        {
            if (fecmd.usableByCmdBlock())
                fecmd.setEnabledForCmdBlock(config.get("CommandBlock", fecmd.getCommandName(), fecmd.isEnabledForCmdBlock()).getBoolean());
            if (fecmd.usableByPlayer())
                fecmd.setEnabledForPlayer(config.get("Player", fecmd.getCommandName(), fecmd.isEnabledForPlayer()).getBoolean());
            if (fecmd.canConsoleUseCommand())
                fecmd.setEnabledForConsole(config.get("Console", fecmd.getCommandName(), fecmd.isEnabledForConsole()).getBoolean());

            String category = "commands." + fecmd.getCommandName();
            config.addCustomCategoryComment(category, fecmd.getPermissionNode());

            fecmd.loadConfig(config, category);
        }

        config.save();
    }

    public static void registerCommands(FEModuleServerInitEvent e)
    {
        for (FEcmdModuleCommands cmd : cmdList)
        {
            cmd.registerExtraPermissions();
            cmd.register();
            APIRegistry.perms.registerPermissionDescription(cmd.getPermissionNode(), cmd.getCommandUsage(null));
        }
    }

}
