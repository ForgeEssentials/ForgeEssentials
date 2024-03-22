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

public class NetworkClientSendingOnParentCommandSender implements CommandSource
{
    String connectedId;

    public NetworkClientSendingOnParentCommandSender(String connectedId)
    {
        this.connectedId = connectedId;
    }

    @Override
    public void sendMessage(Component chatComponent, UUID senderUUID)
    {
        if (ModuleNetworking.getClients().containsKey(connectedId))
            if(ModuleNetworking.getClients().get(connectedId).getCurrentChannel().isOpen()) {
                ModuleNetworking.getInstance().getServer().sendPacketFor(
                        ModuleNetworking.getClients().get(connectedId).getCurrentChannel(), 
                        new Packet11SharedCommandResponse(chatComponent.getString()));
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
                serverworld, 4, "Client@"+connectedId,
                new TextComponent("Client@"+connectedId), server, new FakePlayer(serverworld, new GameProfile(UUID.fromString("35763490-CD67-428C-9A29-4DED4429A489"), "Client@"+connectedId)));
    }

}
