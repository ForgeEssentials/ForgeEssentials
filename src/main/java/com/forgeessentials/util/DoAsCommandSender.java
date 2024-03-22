package com.forgeessentials.util;

import java.util.UUID;


import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.authlib.GameProfile;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.GameRules;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

public class DoAsCommandSender extends FakePlayer
{
    private static final UUID DOAS_UUID = UUID.fromString("35763490-CD67-428C-9A29-4DED4429A487");

    protected CommandSourceStack sender;

    protected UserIdent ident;

    protected boolean hideChatMessages;

    public DoAsCommandSender(ServerLevel world, GameProfile name, UserIdent ident)
    {
        super(world, name);
        this.ident = ident;
    }

    public DoAsCommandSender()
    {
        this(ServerLifecycleHooks.getCurrentServer().getLevel(ServerLevel.OVERWORLD),
                new GameProfile(DOAS_UUID, "@SERVER"), APIRegistry.IDENT_SERVER);
        this.sender = getServer().createCommandSourceStack();
    }

    public DoAsCommandSender(UserIdent ident)
    {
        this(ident.getPlayerMP().getLevel(), new GameProfile(DOAS_UUID, "@" + ident.getUsername()), ident);
        this.sender = ident.getPlayerMP().createCommandSourceStack();
    }

    public DoAsCommandSender(UserIdent ident, CommandSourceStack sender)
    {
        this(sender.getLevel(), new GameProfile(DOAS_UUID, "@" + ident.getUsername()), ident);
        this.sender = sender;
    }

    public CommandSourceStack getOriginalSender()
    {
        return sender;
    }

    public UserIdent getUserIdent()
    {
        return ident;
    }

    @Override
    public Component getDisplayName()
    {
        return new TextComponent(ident.getUsername());
    }

    @Override
    public void sendMessage(Component message, UUID p_145747_2_)
    {
        if (!hideChatMessages)
            ChatOutputHandler.sendMessageI(sender, message);

    }

    @Override
    public ServerLevel getLevel()
    {
        return sender.getLevel();
    }

    @Override
    public BlockPos blockPosition()
    {
        return new BlockPos(position());
    }

    @Override
    public Vec3 position()
    {
        return sender.getPosition();
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
    public boolean acceptsSuccess()
    {
        return sender.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK);
    }

    @Override
    public boolean acceptsFailure()
    {
        return true;
    }

    @Override
    public boolean shouldInformAdmins()
    {
        return true;
    }

}