package com.forgeessentials.commands.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.commands.util.CommandDataManager;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.commands.util.Kit;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPostInitEvent;
import com.forgeessentials.util.events.FEPlayerEvent.NoPlayerInfoEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.questioner.Questioner;
import com.forgeessentials.util.questioner.QuestionerCallback;
import com.forgeessentials.util.questioner.QuestionerStillActiveException;

/**
 * Kit command with cooldown. Should also put armor in armor slots.
 *
 * @author Dries007
 */

public class CommandKit extends FEcmdModuleCommands implements ConfigurableCommand
{

    public static final String PERM = ModuleCommands.PERM + ".kit";
    public static final String PERM_ADMIN = ModuleCommands.PERM + ".admin";
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
    public void processCommandPlayer(final EntityPlayerMP sender, final String[] args) throws CommandException
    {
        /*
         * Print kits
         */
        if (args.length == 0)
        {
            ChatOutputHandler.chatNotification(sender, "Available kits:");
            String msg = "";
            for (Kit kit : CommandDataManager.kits.values())
            {
                if (PermissionManager.checkPermission(sender, getPermissionNode() + "." + kit.getName()))
                {
                    msg = kit.getName() + ", " + msg;
                }
            }
            ChatOutputHandler.chatNotification(sender, msg);
            return;
        }
        /*
         * Give kit
         */
        if (args.length == 1)
        {
            if (!CommandDataManager.kits.containsKey(args[0].toLowerCase()))
                throw new TranslatedCommandException("Kit %s does not exist.", args[0]);
            if (!PermissionManager.checkPermission(sender, getPermissionNode() + "." + args[0].toLowerCase()))
                throw new TranslatedCommandException(
                        "You have insufficient permissions to do that. If you believe you received this message in error, please talk to a server admin.");
            CommandDataManager.kits.get(args[0].toLowerCase()).giveKit(sender);
            return;
        }

        /*
         * Make kit
         */
        if (args.length >= 2 && args[1].equalsIgnoreCase("set") && PermissionManager.checkPermission(sender, getPermissionNode() + ".admin"))
        {
            final int cooldown = (args.length == 3) ? parseInt(args[2], -1, Integer.MAX_VALUE) : -1;

            if (!CommandDataManager.kits.containsKey(args[0].toLowerCase()))
            {
                new Kit(sender, args[0].toLowerCase(), cooldown);
                ChatOutputHandler.chatConfirmation(sender,
                        Translator.format("Kit created successfully. %s cooldown.", ChatOutputHandler.formatTimeDurationReadable(cooldown, true)));
            }
            else
            {
                try
                {
                    QuestionerCallback callback = new QuestionerCallback() {
                        @Override
                        public void respond(Boolean response)
                        {
                            if (response != null && response == true)
                            {
                                new Kit(sender, args[0].toLowerCase(), cooldown);
                                ChatOutputHandler.chatConfirmation(
                                        sender,
                                        Translator.format("Kit created successfully. %s cooldown.",
                                                ChatOutputHandler.formatTimeDurationReadable(cooldown, true)));
                            }
                        }
                    };
                    Questioner.add(sender, Translator.format("Kit %s already exists. overwrite?", args[0].toLowerCase()), callback);
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
        if (args.length == 2 && args[1].equalsIgnoreCase("del") && PermissionManager.checkPermission(sender, getPermissionNode() + ".admin"))
        {
            if (args.length == 2)
            {
                if (!CommandDataManager.kits.containsKey(args[0].toLowerCase()))
                    throw new TranslatedCommandException("Kit %s does not exist.", args[0]);
                CommandDataManager.removeKit(CommandDataManager.kits.get(args[0].toLowerCase()));
                ChatOutputHandler.chatConfirmation(sender, "Kit removed.");
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
        APIRegistry.perms.registerPermission(PERM_ADMIN, PermissionLevel.OP);
        APIRegistry.perms.registerPermission(PERM_BYPASS_COOLDOWN, PermissionLevel.OP);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
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
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
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

}
