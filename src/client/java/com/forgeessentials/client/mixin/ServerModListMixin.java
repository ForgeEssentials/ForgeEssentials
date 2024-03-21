package com.forgeessentials.client.mixin;

import java.util.function.Supplier;

import net.minecraftforge.fmllegacy.network.FMLHandshakeHandler;
import net.minecraftforge.fmllegacy.network.FMLHandshakeMessages;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.forgeessentials.client.ForgeEssentialsClient;

@Mixin(FMLHandshakeHandler.class)
public class ServerModListMixin
{
    /**
     * @author Maximuslotro
     * @reason get modlist from server connect
     */
    @Inject(at = @At("HEAD"),
            method = "handleServerModListOnClient",
            remap = false)
    public void getmodlist(FMLHandshakeMessages.S2CModList serverModList, Supplier<NetworkEvent.Context> c, CallbackInfo info)
    {
        ForgeEssentialsClient.getServerMods(serverModList.getModList());
    }
}
