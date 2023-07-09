package com.forgeessentials.client.mixin;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import com.forgeessentials.client.ForgeEssentialsClient;

import net.minecraftforge.fml.network.FMLHandshakeHandler;
import net.minecraftforge.fml.network.FMLHandshakeMessages;
import net.minecraftforge.fml.network.NetworkEvent;

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
    // @Shadow(remap = false) @Final static Marker FMLHSMARKER;
    // @Shadow(remap = false) @Final private static Logger LOGGER;
    //
    // @Shadow(remap = false) private Set<ResourceLocation> registriesToReceive;
    // @Shadow(remap = false) private Map<ResourceLocation, ForgeRegistry.Snapshot> registrySnapshots;
    // /**
    // * @author
    // * @reason
    // */
    // @Overwrite(remap = false)
    // void handleServerModListOnClient(FMLHandshakeMessages.S2CModList serverModList, Supplier<NetworkEvent.Context> c)
    // {
    // LOGGER.debug(FMLHSMARKER, "Logging into server with mod list [{}]", String.join(", ", serverModList.getModList()));
    // boolean accepted = validateClientChannels(serverModList.getChannels());
    // c.get().setPacketHandled(true);
    // if (!accepted) {
    // LOGGER.error(FMLHSMARKER, "Terminating connection with server, mismatched mod list");
    // c.get().getNetworkManager().disconnect(new StringTextComponent("Connection closed - mismatched mod channel list"));
    // return;
    // }
    // System.out.println("POP");
    // ForgeEssentialsClient.getServerMods(serverModList.getModList());
    // gethandshakeChannel().reply(new FMLHandshakeMessages.C2SModListReply(), c.get());
    //
    // LOGGER.debug(FMLHSMARKER, "Accepted server connection");
    // // Set the modded marker on the channel so we know we got packets
    // c.get().getNetworkManager().channel().attr(FML_NETVERSION()).set(FMLNetworkConstants.NETVERSION);
    // c.get().getNetworkManager().channel().attr(getFML_CONNECTION_DATA())
    // .set(newFMLConnectionData(serverModList.getModList(), serverModList.getChannels()));
    //
    // this.registriesToReceive = new HashSet<>(serverModList.getRegistries());
    // this.registrySnapshots = Maps.newHashMap();
    // LOGGER.debug(REGISTRIES, "Expecting {} registries: {}", ()->this.registriesToReceive.size(), ()->this.registriesToReceive);
    // }
    // private static SimpleChannel gethandshakeChannel() {
    // return ObfuscationReflectionHelper.getPrivateValue(FMLNetworkConstants.class, null, "handshakeChannel");
    // }
    // private static AttributeKey<FMLConnectionData> getFML_CONNECTION_DATA() {
    // return ObfuscationReflectionHelper.getPrivateValue(FMLNetworkConstants.class, null, "FML_CONNECTION_DATA");
    // }
    // private static AttributeKey<String> FML_NETVERSION() {
    // return ObfuscationReflectionHelper.getPrivateValue(FMLNetworkConstants.class, null, "FML_NETVERSION");
    // }
    // private static boolean validateClientChannels(final Map<ResourceLocation, String> channels) {
    // try {
    // Method validateClientChannels = NetworkRegistry.class.getDeclaredMethod(
    // "validateClientChannels", Map<>.class);
    // validateClientChannels.setAccessible(true);
    // return (boolean)validateClientChannels.invoke(NetworkRegistry.class, channels);
    // } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // return false;
    // }
    // private static FMLConnectionData newFMLConnectionData(List<String> modList, Map<ResourceLocation, String> channels) {
    // try {
    // Constructor<FMLConnectionData> data = FMLConnectionData.class.getDeclaredConstructor();
    // data.setAccessible(true);
    // FMLConnectionData dataInstance =data.newInstance(modList, channels);
    // if(!(dataInstance instanceof FMLConnectionData)) {
    // return null;
    // }
    // return dataInstance;
    // } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // return null;
    // }
    // @Shadow(remap=false)
    // public List<String> mods;
    //
    // /**
    // * @author Maximuslotro
    // * @reason get modlist from server connect
    // */
    // @Overwrite(remap=false)
    // public List<String> getModList() {
    // System.out.println("BOOP");
    // ForgeEssentialsClient.getServerMods(mods);
    // return mods;
    // }
}
