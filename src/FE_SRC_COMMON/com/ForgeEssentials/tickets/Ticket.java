package com.ForgeEssentials.tickets;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.AreaSelector.WarpPoint;
import com.ForgeEssentials.api.data.IReconstructData;
import com.ForgeEssentials.api.data.SaveableObject;
import com.ForgeEssentials.api.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.api.data.SaveableObject.SaveableField;
import com.ForgeEssentials.api.data.SaveableObject.UniqueLoadingKey;

@SaveableObject
public class Ticket
{
	@UniqueLoadingKey
	@SaveableField
	public int			id;

	@SaveableField
	public String		creator;

	@SaveableField
	public String		category;

	@SaveableField
	public String		message;

	@SaveableField
	public WarpPoint	point;

	public Ticket(ICommandSender sender, String category, String message)
	{
		id = ModuleTickets.getNextID();
		creator = sender.getCommandSenderName();
		if (sender instanceof EntityPlayer)
		{
			point = new WarpPoint((EntityPlayer) sender);
		}
		this.category = category;
		this.message = message;
	}

	private Ticket(int id, String creator, String category, String message, WarpPoint point)
	{
		this.id = id;
		this.creator = creator;
		this.category = category;
		this.message = message;
		this.point = point;
	}

	@Reconstructor
	private static Ticket reconstruct(IReconstructData tag)
	{
		return new Ticket(Integer.parseInt((String) tag.getFieldValue("id")), (String) tag.getFieldValue("creator"), (String) tag.getFieldValue("category"), (String) tag.getFieldValue("message"), (WarpPoint) tag.getFieldValue("point"));
	}
}
