package com.forgeessentials.core.preloader.mixin.scoreboard;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.management.ServerConfigurationManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.forgeessentials.multiworld.MultiworldScoreboard;

@Mixin(ServerScoreboard.class)
public class MixinServerScoreboard extends Scoreboard
{
    ServerScoreboard _this;

    @Redirect(method = {
            "func_96536_a",
            "func_96516_a",
            "func_178820_a",
            "setObjectiveInDisplaySlot",
            "addPlayerToTeam",
            "removePlayerFromTeam",
            "onObjectiveDisplayNameChanged",
            "broadcastTeamCreated",
            "sendTeamUpdate",
            "func_96513_c"
    },
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/ServerConfigurationManager;sendPacketToAllPlayers(Lnet/minecraft/network/Packet;)V"))
    public void RedirectPacket(ServerConfigurationManager instance, Packet packet)
    {
        Object _this = (Object) this;
        if (_this instanceof MultiworldScoreboard)
        {

            ((MultiworldScoreboard) _this).multiworld.sendPacketToAllPlayers(packet);
        }
        else
        {
            instance.sendPacketToAllPlayers(packet);
        }
    }

    @Redirect(method = "func_96549_e", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/ServerConfigurationManager;getPlayerList()Ljava/util/List;"))
    public List<EntityPlayerMP> RedirectPlayerList(ServerConfigurationManager instance)
    {
        Object _this = (Object) this;
        if (_this instanceof MultiworldScoreboard)
        {

            return ((MultiworldScoreboard) _this).multiworld.getPlayerList();
        }
        else
        {
            return instance.getPlayerList();
        }
    }
}
