package com.forgeessentials.playerlogger.event;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.playerlogger.PlayerLoggerEvent;
import com.forgeessentials.playerlogger.entity.Action02Command;
import com.forgeessentials.util.CommandUtils;
import com.mojang.brigadier.context.ParsedCommandNode;

public class LogEventCommand extends PlayerLoggerEvent<CommandEvent>
{

    public LogEventCommand(CommandEvent event)
    {
        super(event);
    }

    @Override
    public void process(EntityManager em)
    {
    	if(event.getParseResults().getContext().getNodes().isEmpty())
            return;
        Action02Command action = new Action02Command();
        action.time = date;
        action.command = event.getParseResults().getContext().getNodes().get(0).getNode().getName();
        action.arguments="";
        if(event.getParseResults().getContext().getNodes().size() > 1) {
        	List<String> arguments = new ArrayList<>();
        	//System.out.println(event.getParseResults().getReader().getString());
        	for(ParsedCommandNode<CommandSource> node :event.getParseResults().getContext().getNodes()) {
            	arguments.add(node.getNode().getName());
                //System.out.println(node.getNode().getName());
            }
        	arguments.remove(0);
        	action.arguments = String.join(" ", arguments);
        }
        if (event.getParseResults().getContext().getSource().getEntity() instanceof PlayerEntity)
        {
            PlayerEntity player = ((PlayerEntity) event.getParseResults().getContext().getSource().getEntity());
            action.player = getPlayer(player);
            action.world = player.level.dimension().location().toString();
            action.x = (int) player.position().x;
            action.y = (int) player.position().y;
            action.z = (int) player.position().z;
        }
        else if (CommandUtils.GetSource(event.getParseResults().getContext().getSource()) instanceof CommandBlockLogic)
        {
            CommandBlockLogic block = ((CommandBlockLogic) CommandUtils.GetSource(event.getParseResults().getContext().getSource()));
            action.player = getPlayer(UserIdent.getVirtualPlayer("commandblock"));
            action.world = block.getLevel().dimension().location().toString();
            BlockPos pos = new BlockPos(block.getPosition());
            action.x = pos.getX();
            action.y = pos.getY();
            action.z = pos.getZ();
        }
        else {
            action.player = getPlayer(UserIdent.getVirtualPlayer("console"));
            action.world = ServerLifecycleHooks.getCurrentServer().overworld().dimension().location().toString();
            BlockPos pos = new BlockPos(0, 0, 0);
            action.x = pos.getX();
            action.y = pos.getY();
            action.z = pos.getZ();
        }
        //System.out.println("["+action.time.toGMTString()+"]");
        //System.out.println("["+action.command+"]");
        //System.out.println("["+action.arguments+"]");
        //System.out.println("["+action.player.username+"]");
        //System.out.println("["+action.world.id+"]");
        //System.out.println("["+action.x+"]");
        //System.out.println("["+action.y+"]");
        //System.out.println("["+action.z+"]");
        //System.out.println("["+action.id+"]");
        em.persist(action);
    }

}