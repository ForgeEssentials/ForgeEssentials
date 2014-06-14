package com.forgeessentials.permission;

import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class CommandFEPermPromote {
    public static void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        ChatUtils.sendMessage(sender, "TEST! Promote parsing");

        if (args.length == 0)
        // Not possible
        // OutputHandler.chatError(sender,
        // "Improper syntax. Please try this instead: " + "");
        {
            return;
        }

        EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
        if (player == null)
        {
            // No such player!
        }
        if (args.length == 1)
        // default ladder
        {
            return;
        }
        if (!args[1].equalsIgnoreCase("from"))
        {
            // Ladder specified.
            // Ladder = arg 1
            if (args.length >= 3)
            {
                if (args.length == 4)
                {
                    // Zone set.
                    // arg 3 = zone
                }
            }
            return;
        }
        if (args[1].equalsIgnoreCase("from"))
        {
            // Ladder specified.
            // Ladder = arg 1
            if (args.length >= 3)
            {
                if (args.length == 4)
                {
                    // Zone set.
                    // arg 3 = zone
                }
            }
            return;
        }

        OutputHandler.chatError(sender, "Improper syntax. Please try this instead: " + "");
    }

    public static void processCommandConsole(ICommandSender sender, String[] args)
    {
        // Copy paste :p
    }

}
