package com.forgeessentials.commands.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.item.CommandBind;
import com.forgeessentials.commands.item.CommandCraft;
import com.forgeessentials.commands.item.CommandDrop;
import com.forgeessentials.commands.item.CommandEnchant;
import com.forgeessentials.commands.item.CommandEnderchest;
import com.forgeessentials.commands.item.CommandKit;
import com.forgeessentials.commands.item.CommandRename;
import com.forgeessentials.commands.item.CommandRepair;
import com.forgeessentials.commands.item.CommandVirtualchest;
import com.forgeessentials.commands.player.CommandAFK;
import com.forgeessentials.commands.player.CommandBubble;
import com.forgeessentials.commands.player.CommandBurn;
import com.forgeessentials.commands.player.CommandCapabilities;
import com.forgeessentials.commands.player.CommandDoAs;
import com.forgeessentials.commands.player.CommandFly;
import com.forgeessentials.commands.player.CommandGameMode;
import com.forgeessentials.commands.player.CommandHeal;
import com.forgeessentials.commands.player.CommandInventorySee;
import com.forgeessentials.commands.player.CommandKill;
import com.forgeessentials.commands.player.CommandLocate;
import com.forgeessentials.commands.player.CommandNoClip;
import com.forgeessentials.commands.player.CommandPotion;
import com.forgeessentials.commands.player.CommandSeen;
import com.forgeessentials.commands.player.CommandSmite;
import com.forgeessentials.commands.player.CommandSpeed;
import com.forgeessentials.commands.player.CommandTempBan;
import com.forgeessentials.commands.server.CommandGetCommandBook;
import com.forgeessentials.commands.server.CommandModlist;
import com.forgeessentials.commands.server.CommandPing;
import com.forgeessentials.commands.server.CommandRules;
import com.forgeessentials.commands.server.CommandServerSettings;
import com.forgeessentials.commands.world.CommandButcher;
import com.forgeessentials.commands.world.CommandFindblock;
import com.forgeessentials.commands.world.CommandPush;
import com.forgeessentials.commands.world.CommandRemove;
import com.forgeessentials.commands.world.CommandTime;
import com.forgeessentials.commands.world.CommandWeather;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;

public class CommandRegistrar
{

    public static List<FEcmdModuleCommands> cmdList = new ArrayList<>();

    static
    {
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
        cmdList.add(new CommandFly());
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

}
