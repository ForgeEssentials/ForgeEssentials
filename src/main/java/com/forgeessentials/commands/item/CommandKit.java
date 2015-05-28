package com.forgeessentials.commands.item;

import java.util.ArrayList;
import java.util.List;

import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.FEPlayerEvent.NoPlayerInfoEvent;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;
import com.forgeessentials.util.questioner.QuestionerStillActiveException;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.util.CommandDataManager;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.commands.util.Kit;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

/**
 * Kit command with cooldown. Should also put armor in armor slots.
 *
 * @author Dries007
 */

public class CommandKit extends FEcmdModuleCommands implements ConfigurableCommand
{

    public static final String PERM = COMMANDS_PERM + ".kit";
    public static final String PERM_ADMIN = COMMANDS_PERM + ".admin";
    public static final String PERM_BYPASS_COOLDOWN = PERM + ".bypasscooldown";

    public static final String[] tabCompletionArg2 = new String[] { "set", "del" };

    protected String kitForNewPlayers;

    public CommandKit()
    {
        APIRegistry.getFEEventBus().register(this);
    }

    @Override
    public String getCommandName()
    {
        return "kit";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        /*
         * Print kits
         */
        if (args.length == 0)
        {
            OutputHandler.chatNotification(sender, "Available kits:");
            String msg = "";
            for (Kit kit : CommandDataManager.kits.values())
            {
                if (PermissionsManager.checkPermission(sender, getPermissionNode() + "." + kit.getName()))
                {
                    msg = kit.getName() + ", " + msg;
                }
            }
            OutputHandler.chatNotification(sender, msg);
            return;
        }
        /*
         * Give kit
         */
        if (args.length == 1)
        {
            if (!CommandDataManager.kits.containsKey(args[0].toLowerCase()))
                throw new TranslatedCommandException("Kit %s does not exist.", args[0]);
            if (!PermissionsManager.checkPermission(sender, getPermissionNode() + "." + args[0].toLowerCase()))
                throw new TranslatedCommandException(
                        "You have insufficient permissions to do that. If you believe you received this message in error, please talk to a server admin.");
            CommandDataManager.kits.get(args[0].toLowerCase()).giveKit(sender);
            return;
        }

        /*
         * Make kit
         */
        if (args.length >= 2 && args[1].equalsIgnoreCase("set") && PermissionsManager.checkPermission(sender, getPermissionNode() + ".admin"))
        {
            if (!CommandDataManager.kits.containsKey(args[0].toLowerCase()))
            {
                int cooldown = -1;
                if (args.length == 3)
                {
                    cooldown = parseIntWithMin(sender, args[2], -1);
                }
                new Kit(sender, args[0].toLowerCase(), cooldown);
                OutputHandler.chatConfirmation(sender,
                        Translator.format("Kit created successfully. %s cooldown.", FunctionHelper.formatDateTimeReadable(cooldown, true)));
            }
            else
            {
                try
                {
                    Questioner.add(sender, Translator.format("Kit %s already exists. overwrite?", args[0].toLowerCase()), new HandleKitOverrides(sender, args));
                }
                catch (QuestionerStillActiveException e)
                {
                    throw new QuestionerStillActiveException.CommandException();
                }
            }
            return;
        }

        /*
         * Delete kit
         */
        if (args.length == 2 && args[1].equalsIgnoreCase("del") && PermissionsManager.checkPermission(sender, getPermissionNode() + ".admin"))
        {
            if (args.length == 2)
            {
                if (!CommandDataManager.kits.containsKey(args[0].toLowerCase()))
                    throw new TranslatedCommandException("Kit %s does not exist.", args[0]);
                CommandDataManager.removeKit(CommandDataManager.kits.get(args[0].toLowerCase()));
                OutputHandler.chatConfirmation(sender, "Kit removed.");
            }
        }

        /*
         * You're doing it wrong!
         */
        throw new TranslatedCommandException(getCommandUsage(sender));
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(PERM_ADMIN, RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission(PERM_BYPASS_COOLDOWN, RegisteredPermValue.OP);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 0)
        {
            List<String> kits = new ArrayList<String>();
            for (Kit kit : CommandDataManager.kits.values())
                kits.add(kit.getName());
            return getListOfStringsMatchingLastWord(args, kits);
        }
        else if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, tabCompletionArg2);
        return null;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/kit [name] OR [name] [set|del] <cooldown> Allows you to receive free kits which are pre-defined by the server owner.";
    }

    @Override
    public void loadConfig(Configuration config, String category)
    {
        kitForNewPlayers = config.get(category, "kitForNewPlayers", "", "Name of kit to issue to new players. If this is left blank, it will be ignored.")
                .getString();
    }

    @SubscribeEvent
    public void checkKitExistence(FEModuleServerPostInitEvent e)
    {
        if (!CommandDataManager.kits.containsKey(kitForNewPlayers))
            kitForNewPlayers = null;
    }

    @SubscribeEvent
    public void issueWelcomeKit(NoPlayerInfoEvent e)
    {
        Kit kit = CommandDataManager.kits.get(kitForNewPlayers);
        if (kit != null)
        {
            kit.giveKit(e.entityPlayer);
        }
    }

    static class HandleKitOverrides implements QuestionerCallback
    {

        private String[] args;

        private EntityPlayerMP sender;

        private HandleKitOverrides(EntityPlayerMP sender, String[] args)
        {
            this.args = args;
            this.sender = sender;
        }

        @Override
        public void respond(Boolean response)
        {
            if (response != null && response == true)
            {
                int cooldown = -1;
                if (args.length == 3)
                {
                    cooldown = parseIntWithMin(sender, args[2], -1);
                }
                new Kit(sender, args[0].toLowerCase(), cooldown);
                OutputHandler.chatConfirmation(sender,
                        Translator.format("Kit created successfully. %s cooldown.", FunctionHelper.formatDateTimeReadable(cooldown, true)));
            }
        }

    }

}
