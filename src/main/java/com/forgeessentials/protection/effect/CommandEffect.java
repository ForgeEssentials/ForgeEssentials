package com.forgeessentials.protection.effect;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import org.apache.commons.lang3.ArrayUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.DoAsCommandSender;
import com.forgeessentials.util.output.LoggingHandler;

public class CommandEffect extends ZoneEffect
{

    protected String command;

    public CommandEffect(EntityPlayerMP player, int interval, String command)
    {
        super(player, interval, false);
        this.command = command;
    }

    @Override
    public void execute()
    {
        try
        {
            // ScriptParser.run(command, player, null);

            String[] args = command.split(" ");
            String cmdName = args[0];
            args = ArrayUtils.remove(args, 0);

            // Slightly preprocess command for backwards compatibility
            for (int i = 0; i < args.length; i++)
                if (args[i].equals("@player"))
                    args[i] = player.getName();

            ICommand mcCommand = (ICommand) MinecraftServer.getServer().getCommandManager().getCommands().get(cmdName);
            if (mcCommand == null)
            {
                LoggingHandler.felog.error(String.format("Could not find command for WorldBorder effect: ", command));
                return;
            }
            mcCommand.processCommand(new DoAsCommandSender(APIRegistry.IDENT_SERVER, player), args);
        }
        catch (CommandException e)
        {
            LoggingHandler.felog.error(String.format("Error executing zone command: %s", e.getMessage()));
        }
    }

}
