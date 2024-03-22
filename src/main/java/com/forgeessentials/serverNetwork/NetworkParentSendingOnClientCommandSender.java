package com.forgeessentials.serverNetwork;

import java.util.UUID;

import com.forgeessentials.serverNetwork.packetbase.packets.Packet11SharedCommandResponse;
import com.mojang.authlib.GameProfile;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
public class NetworkParentSendingOnClientCommandSender implements CommandSource
{
    String connectedId;

    public NetworkParentSendingOnClientCommandSender(String connectedId)
    {
        this.connectedId = connectedId;
    }
    @Override
    public void sendMessage(Component chatComponent, UUID senderUUID)
    {
        if(ModuleNetworking.getInstance().getClient().isChannelOpen()) {
            ModuleNetworking.getInstance().getClient().sendPacket(new Packet11SharedCommandResponse(chatComponent.getString()));
        }
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

    public CommandSourceStack createCommandSourceStack()
    {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerLevel serverworld = server.overworld();
        return new CommandSourceStack(this, Vec3.ZERO, Vec2.ZERO,
                serverworld, 4, "Parent@"+connectedId,
                new TextComponent("Parent@"+connectedId), server, new FakePlayer(serverworld, new GameProfile(UUID.fromString("35763490-CD67-428C-9A29-4DED4429A488"), "Parent@"+connectedId)));
    }
}
