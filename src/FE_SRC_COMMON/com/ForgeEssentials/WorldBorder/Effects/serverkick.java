package com.ForgeEssentials.WorldBorder.Effects;

import com.ForgeEssentials.WorldBorder.WorldBorder;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class serverkick implements IEffect
{
	private String	message	= "You passed the world border!";

	@Override
	public void registerConfig(Configuration config, String category)
	{
		message = config.get(category, "Message", message, "Message to send to the player on the kick screen.").value;
	}

	@Override
	public void execute(WorldBorder wb, EntityPlayerMP player)
	{
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
		{
			player.playerNetServerHandler.kickPlayerFromServer(message);
		}
		else
		{
			player.sendChatToPlayer("You should have been kicked from the server with this message:");
			player.sendChatToPlayer(message);
			player.sendChatToPlayer("Since this is SSP, thats not possible.");
		}
	}
}
