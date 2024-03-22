package com.forgeessentials.chataddon.irc;

import java.util.UUID;

import org.pircbotx.hooks.events.MessageEvent;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

public class IrcCommandFaker implements CommandSource
{
	private MessageEvent event;

	@Override
	public void sendMessage(Component p_80166_, UUID p_80167_) {
		if(p_80166_.getString().startsWith("/")) {
    		event.respond("!"+p_80166_.getString().substring(1));
    		return;
    	}
    	event.respond(p_80166_.getString());
		
	}
    @Override
    public boolean acceptsSuccess()
    {
        return true;
    }

    @Override
    public boolean acceptsFailure()
    {
        return true;
    }

    @Override
    public boolean shouldInformAdmins()
    {
        return false;
    }

    public CommandSourceStack createCommandSourceStack(int level, MessageEvent event)
    {
    	this.event = event;
    	String name = event.getUser().getNick();
        if (level < 0)
        {
            level = 0;
        }
        else if (level > 4)
        {
            level = 4;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerLevel serverworld = server.overworld();
        return new CommandSourceStack(this, Vec3.ZERO, Vec2.ZERO,
                serverworld, level, name,
                new TextComponent(name), server, (Entity)null);
    }
}

