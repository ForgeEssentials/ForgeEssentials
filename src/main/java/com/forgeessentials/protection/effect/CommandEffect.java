package com.forgeessentials.protection.effect;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.output.logger.LoggingHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class CommandEffect extends ZoneEffect
{

    protected String command;

    public CommandEffect(ServerPlayerEntity player, int interval, String command)
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
            CommandDispatcher<CommandSource> dispatcher = ServerLifecycleHooks.getCurrentServer().getCommands()
                    .getDispatcher();
            for (CommandNode<CommandSource> commandNode : dispatcher.getRoot().getChildren())
            {
                if (cmdName == commandNode.getUsageText().substring(1))
                {
                    cmdExists = true;
                    break;
                }
            }
            ServerLifecycleHooks.getCurrentServer().getCommands().performCommand(player.createCommandSourceStack(),
                    String.join(" ", args));
            if (cmdExists == false)
            {
                LoggingHandler.felog.error(String.format("Could not find command for WorldBorder effect: %s", command));
                return;
            }
            ServerLifecycleHooks.getCurrentServer().getCommands()
                    .performCommand(new DoAsCommandSender(APIRegistry.IDENT_SERVER, player.createCommandSourceStack())
                            .createCommandSourceStack(), String.join(" ", args));
        }
        catch (CommandException e)
        {
            LoggingHandler.felog.error(String.format("Error executing zone command: %s", e.getMessage()));
        }
    }

}
