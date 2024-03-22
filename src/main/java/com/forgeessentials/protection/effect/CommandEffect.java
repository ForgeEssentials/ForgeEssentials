package com.forgeessentials.protection.effect;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

public class CommandEffect extends ZoneEffect
{

    protected String command;

    public CommandEffect(ServerPlayer player, int interval, String command)
    {
        super(player, interval, false);
        this.command = command;
    }

    @Override
    public void execute()
    {
        try
        {
            String[] args = command.split(" ");
            String cmdName = args[0];
            // args = ArrayUtils.remove(args, 0); //we need command name to be in the args

            // Slightly preprocess command for backwards compatibility
            for (int i = 0; i < args.length; i++)
                if (args[i].equals("@player"))
                    args[i] = player.getDisplayName().getString();

            boolean cmdExists = false;
            // check if command exists
            CommandDispatcher<CommandSourceStack> dispatcher = ServerLifecycleHooks.getCurrentServer().getCommands()
                    .getDispatcher();
            for (CommandNode<CommandSourceStack> commandNode : dispatcher.getRoot().getChildren())
            {
                if (cmdName.equals(commandNode.getUsageText().substring(1)))
                {
                    cmdExists = true;
                    break;
                }
            }
            ServerLifecycleHooks.getCurrentServer().getCommands().performCommand(player.createCommandSourceStack(),
                    String.join(" ", args));
            if (!cmdExists)
            {
                LoggingHandler.felog.error(String.format("Could not find command for WorldBorder effect: %s", command));
                return;
            }
            ServerLifecycleHooks.getCurrentServer().getCommands()
                    .performCommand(new DoAsCommandSender(APIRegistry.IDENT_SERVER, player.createCommandSourceStack())
                            .createCommandSourceStack(), String.join(" ", args));
        }
        catch (CommandRuntimeException e)
        {
            LoggingHandler.felog.error(String.format("Error executing zone command: %s", e.getMessage()));
        }
    }

}
