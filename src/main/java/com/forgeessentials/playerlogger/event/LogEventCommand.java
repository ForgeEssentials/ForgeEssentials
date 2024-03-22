package com.forgeessentials.playerlogger.event;

import javax.persistence.EntityManager;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action02Command;
import com.forgeessentials.util.CommandUtils;
import com.forgeessentials.util.CommandUtils.CommandInfo;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

public class LogEventCommand extends PlayerLoggerEvent<CommandEvent>
{

    public LogEventCommand(CommandEvent event)
    {
        super(event);
    }

    @Override
    public void process(EntityManager em)
    {
        if (event.getParseResults().getContext().getNodes().isEmpty())
            return;
        Action02Command action = new Action02Command();
        action.time = date;
        CommandInfo info = CommandUtils.getCommandInfo(event);
        action.command = info.getCommandName();
        action.arguments = info.getActualArgsString();
        if (event.getParseResults().getContext().getSource().getEntity() instanceof Player)
        {
            Player player = ((Player) event.getParseResults().getContext().getSource().getEntity());
            action.player = getPlayer(player);
            action.world = player.level.dimension().location().toString();
            action.x = (int) player.position().x;
            action.y = (int) player.position().y;
            action.z = (int) player.position().z;
        }
        else if (CommandUtils
                .GetSource(event.getParseResults().getContext().getSource()) instanceof BaseCommandBlock)
        {
            BaseCommandBlock block = ((BaseCommandBlock) CommandUtils
                    .GetSource(event.getParseResults().getContext().getSource()));
            action.player = getPlayer(UserIdent.getVirtualPlayer("commandblock"));
            action.world = block.getLevel().dimension().location().toString();
            BlockPos pos = new BlockPos(0, 0, 0);
            action.x = pos.getX();
            action.y = pos.getY();
            action.z = pos.getZ();
        }
        else
        {
            action.player = getPlayer(UserIdent.getVirtualPlayer("console"));
            ServerLevel overworld = ServerLifecycleHooks.getCurrentServer().overworld();
            action.world = overworld.dimension().location().toString();
            BlockPos pos = new BlockPos(0, 0, 0);
            action.x = pos.getX();
            action.y = pos.getY();
            action.z = pos.getZ();
        }
        // System.out.println("["+action.time.toGMTString()+"]");
        // System.out.println("["+action.command+"]");
        // System.out.println("["+action.arguments+"]");
        // System.out.println("["+action.player.username+"]");
        // System.out.println("["+action.world.id+"]");
        // System.out.println("["+action.x+"]");
        // System.out.println("["+action.y+"]");
        // System.out.println("["+action.z+"]");
        // System.out.println("["+action.id+"]");
        em.persist(action);
    }

}