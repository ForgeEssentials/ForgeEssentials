package com.forgeessentials.chat.irc;

import java.util.UUID;

import org.pircbotx.User;

import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.authlib.GameProfile;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class IrcCommandSender extends FakePlayer
{
    private static final UUID FEIRC_UUID = UUID.fromString("35763490-CD67-428C-9A29-4DED4429A487");
    private User user;

    public IrcCommandSender(User user)
    {
        this(ServerLifecycleHooks.getCurrentServer().getLevel(ServerWorld.OVERWORLD),
                new GameProfile(FEIRC_UUID, "@" + user.getRealName()), user);
    }

    public IrcCommandSender(ServerWorld world, GameProfile name, User user)
    {
        super(world, name);
        this.user = user;
    }

    public User getUser()
    {
        return user;
    }

    public String getTextName()
    {
        return "IRC:" + user.getNick();
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return new StringTextComponent(this.getTextName());
    }

    @Override
    public void sendMessage(ITextComponent chatComponent, UUID senderUUID)
    {
        if (user.getBot().isConnected())
            user.send().message(ChatOutputHandler.stripFormatting(chatComponent.getString()));
    }

    @Override
    public Vector3d position()
    {
        return new Vector3d(0, 0, 0);
    }

    @Override
    public MinecraftServer getServer()
    {
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public boolean acceptsSuccess()
    {
        return ServerLifecycleHooks.getCurrentServer().getLevel(ServerWorld.OVERWORLD).getGameRules()
                .getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK);
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
