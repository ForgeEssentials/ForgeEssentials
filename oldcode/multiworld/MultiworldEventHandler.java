package com.forgeessentials.multiworld;

import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 
 * @author gnif
 */
public class MultiworldEventHandler extends ChannelInboundHandlerAdapter
{
    
	private MultiworldManager manager;

	public MultiworldEventHandler(MultiworldManager manager) {
		super();

		this.manager = manager;
		NetworkRegistry.INSTANCE.getChannel("FORGE", Dist.DEDICATED_SERVER)
				.pipeline().addFirst("MultiworldEventHandler", this);
	}

	@Override public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception { if (evt instanceof NetworkHandshakeEstablished) {
      NetworkHandshakeEstablished event = (NetworkHandshakeEstablished) evt;
      
      // REPLY does not work, see https://github.com/MinecraftForge/FML/issues/360 FMLEmbeddedChannel channel = NetworkRegistry.INSTANCE.getChannel("FORGE",
      Dist.DEDICATED_SERVER); channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler. OutboundTarget.DISPATCHER);
      channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(event.dispatcher);
      
      for (Multiworld world : manager.getWorlds()) channel.writeOutbound(new DimensionRegisterMessage(world.dimensionId,
      DimensionManager.getProviderType(world.dimensionId).name())); }
      
      ctx.fireUserEventTriggered(evt); }
     
}