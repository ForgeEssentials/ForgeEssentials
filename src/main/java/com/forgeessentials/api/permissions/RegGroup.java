package com.forgeessentials.api.permissions;

import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.permissions.api.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;

/**
 * RegistrationGroups
 * @author AbrarSyed
 */
public enum RegGroup
{
	/**
	 * Used for blankets permissions tied to no particular player or group in a
	 * zone. All players are part of this group. This group is guaranteed
	 * existence
	 */
	ZONE("_ZONE_", " ", " ", null, 0, RegisteredPermValue.TRUE)
	{
		@Override
		public Group getGroup()
		{
			return APIRegistry.perms.getDEFAULT();
		}
	},

	/**
	 * This is the group, b default, that all players are assigned to when they
	 * first log in. The players in this group are usually denied commands and
	 * breaking blocks before they are promoted to members.
	 */
	GUESTS("Guests", EnumChatFormatting.GRAY + "[GUEST]", " ", null, 0, RegisteredPermValue.TRUE),

	/**
	 * This is usually for players that are actually members of the server. They
	 * will most likely be able to use basic commands as well as break blocks
	 * and stuff in the world.
	 */
	MEMBERS("Members", " ", " ", null, 0, RegisteredPermValue.NONOP),

	/**
	 * This is usually for players that are admins or owners of a given zone
	 * They will most likely have WorldEdit access, as well as the power to edit
	 * permissions in the zone.
	 */
	ZONE_ADMINS("ZoneAdmins", EnumChatFormatting.RED + "[ZoneAdmin]", " ", null, 0, RegisteredPermValue.OP),

	/**
	 * This is automatically assigned to the server owner when they make a world
	 * available to the LAN. This is also best kept for players that have direct
	 * access to the server's console and filesystem.
	 */
	OWNERS("Owners", EnumChatFormatting.RED + "[OWNER]", " ", null, 999, RegisteredPermValue.OP);

	private RegGroup(String name, String parent, String prefix, String suffix, int priority, RegisteredPermValue equivalent)
	{
		this.name = name;
		this.equivalent = equivalent;
		group = new Group(name, parent, prefix, suffix, APIRegistry.zones.getGLOBAL().getZoneName(), priority);
	}

	@Override
	public String toString()
	{
		return name;
	}

	public Group getGroup()
	{
		return group;
	}
	
	public RegisteredPermValue getEquivalent()
	{
		return equivalent;
	}

	private Group				group;
	private String				name;
	private RegisteredPermValue equivalent;

	public static final String	LADDER	= "mainLadder";
}
