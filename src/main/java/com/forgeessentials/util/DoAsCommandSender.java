package com.forgeessentials.util;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.UUID;


import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.authlib.GameProfile;

public class DoAsCommandSender extends FakePlayer
{
    private static final UUID DOAS_UUID = UUID.fromString("35763490-CD67-428C-9A29-4DED4429A487");

    protected CommandSource sender;

    protected UserIdent ident;

    protected boolean hideChatMessages;

    public DoAsCommandSender(ServerWorld world, GameProfile name, UserIdent user)
    {
        super(world, name);
        this.ident = user;
    }
    public DoAsCommandSender()
    {
        this(ServerLifecycleHooks.getCurrentServer().getLevel(ServerWorld.OVERWORLD),
                new GameProfile(DOAS_UUID, "@SERVER"),
                APIRegistry.IDENT_SERVER);
        this.ident = APIRegistry.IDENT_SERVER;
        this.sender = getServer().createCommandSourceStack();
    }

    public DoAsCommandSender(UserIdent ident)
    {
        this(ident.getPlayerMP().getLevel(),
                new GameProfile(DOAS_UUID, "@" + ident.getUsername()),
                ident);
        this.ident = ident;
        this.sender = ident.getPlayerMP().createCommandSourceStack();
    }

    public DoAsCommandSender(UserIdent ident, CommandSource sender)
    {
        this(sender.getLevel(),
                new GameProfile(DOAS_UUID, "@" + ident.getUsername()),
                ident);
        this.ident = ident;
        this.sender = sender;
    }

    public CommandSource getOriginalSender()
    {
        return sender;
    }

    public UserIdent getUserIdent()
    {
        return ident;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return sender.getDisplayName();
    }

	@Override
	public void sendMessage(ITextComponent message, UUID p_145747_2_) {
		if (!hideChatMessages)
		    ChatOutputHandler.sendMessageI(sender, message);
		
	}

    @Override
    public ServerWorld getLevel()
    {
        return sender.getLevel();
    }

    @Override
    public BlockPos blockPosition()
    {
        return new BlockPos(position());
    }

    @Override
    public Vector3d position()
    {
        return sender.getPosition();
    }

    @Override
    public Entity getEntity()
    {
        return sender.getEntity();
    }

    public MinecraftServer getServer()
    {
        return ServerLifecycleHooks.getCurrentServer();
    }

    public UserIdent getIdent()
    {
        return ident;
    }

    public void setIdent(UserIdent ident)
    {
        this.ident = ident;
    }

    public void setHideChatMessages(boolean hideChatMessages)
    {
        this.hideChatMessages = hideChatMessages;
    }

    public boolean isHideChatMessages()
    {
        return hideChatMessages;
    }

	@Override
	public boolean acceptsSuccess() {
	      return sender.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK);
	}

	@Override
	public boolean acceptsFailure() {
		return true;
	}

	@Override
	public boolean shouldInformAdmins() {
		return true;
	}

}