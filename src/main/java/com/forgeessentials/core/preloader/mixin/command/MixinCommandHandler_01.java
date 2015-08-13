package com.forgeessentials.core.preloader.mixin.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.forgeessentials.util.ServerUtil;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Mixin(CommandHandler.class)
public abstract class MixinCommandHandler_01
{

    @Shadow
    private final Map<String, ICommand> commandMap = new HashMap();

    @Shadow
    private final Set<ICommand> commandSet = new HashSet();

    @Overwrite
    public List<String> getPossibleCommands(ICommandSender sender, String p_71558_2_)
    {
        String[] cmdLine = p_71558_2_.split(" ", -1);
        String commandName = cmdLine[0];

        if (cmdLine.length == 1)
        {
            List commandNames = new ArrayList();
            Set<ICommand> commandSet = new HashSet<>();
            for (Entry<String, ICommand> cmd : commandMap.entrySet())
                if (!commandSet.contains(cmd) && CommandBase.doesStringStartWith(commandName, cmd.getKey())
                        && PermissionManager.checkPermission(sender, cmd.getValue()))
                {
                    commandNames.add(cmd.getKey());
                    commandSet.add(cmd.getValue());
                }
            return commandNames;
        }
        else
        {
            if (cmdLine.length > 1)
            {
                ICommand cmd = commandMap.get(commandName);
                if (cmd != null)
                    return cmd.addTabCompletionOptions(sender, ServerUtil.dropFirst(cmdLine));
            }
            return null;
        }
    }

    @Overwrite
    public List<ICommand> getPossibleCommands(ICommandSender sender)
    {
        ArrayList commandNames = new ArrayList();
        for (ICommand command : new HashSet<ICommand>(commandMap.values()))
            if (PermissionManager.checkPermission(sender, command))
                commandNames.add(command);
        return commandNames;
    }

}
