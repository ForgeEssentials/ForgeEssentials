package com.forgeessentials.playerlogger.event;

import javax.persistence.EntityManager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.CommandEvent;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action02Command;
import com.forgeessentials.util.CommandUtils;

public class LogEventCommand extends PlayerLoggerEvent<CommandEvent>
{

    public LogEventCommand(CommandEvent event)
    {
        super(event);
    }

    @Override
    public void process(EntityManager em)
    {
        Action02Command action = new Action02Command();
        action.time = date;
        action.command = event.getParseResults().getContext().getNodes().get(0).toString();
        if (event.getParseResults().getContext().getNodes().size() > 1)
            action.arguments = StringUtils.join(event.getParseResults().getContext().getNodes(), ' ');
        if (event.getParseResults().getContext().getSource().getEntity() instanceof PlayerEntity)
        {
            PlayerEntity player = ((PlayerEntity) event.getParseResults().getContext().getSource().getEntity());
            action.player = getPlayer(player);
            action.world = getWorld(player.level.dimension().location().toString());
            action.x = (int) player.position().x;
            action.y = (int) player.position().y;
            action.z = (int) player.position().z;
        }
        else if (CommandUtils.GetSource(event.getParseResults().getContext().getSource()) instanceof CommandBlockLogic)
        {
            CommandBlockLogic block = ((CommandBlockLogic) CommandUtils.GetSource(event.getParseResults().getContext().getSource()));
            action.player = getPlayer(UserIdent.getVirtualPlayer("commandblock"));
            action.world = getWorld(block.getLevel().dimension().toString());
            BlockPos pos = new BlockPos(block.getPosition());
            action.x = pos.getX();
            action.y = pos.getY();
            action.z = pos.getZ();
        }
        em.persist(action);
    }

}