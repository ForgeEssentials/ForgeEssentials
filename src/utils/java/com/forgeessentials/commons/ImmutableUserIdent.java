package com.forgeessentials.commons;

import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Immutable UserIdent (unchanging username and uuid) for maps
 */
public class ImmutableUserIdent extends UserIdent {

    public ImmutableUserIdent(UUID uuid)
    {
        super(uuid);
    }

    public ImmutableUserIdent(String ident)
    {
        super(ident);
    }

    public ImmutableUserIdent(EntityPlayerMP player)
    {
        super(player);
    }

    public ImmutableUserIdent(EntityPlayer player)
    {
        super(player);
    }

    public ImmutableUserIdent(UUID uuid, String username)
    {
        super(uuid, username);
    }

    public ImmutableUserIdent(String uuid, String username)
    {
        super(uuid, username);
    }

    public ImmutableUserIdent(String ident, ICommandSender sender)
    {
        super(ident, sender);
    }

    public ImmutableUserIdent(UserIdent ident)
    {
        super(ident);
    }

    // ------------------------------------------------------------

    @Override
    public void identifyUser()
    {
        if (player == null)
        {
            if (uuid != null)
                player = getPlayerByUuid(uuid);
            else if (username != null)
                player = getPlayerByUsername(username);
            else if (profile != null)
                player = getPlayerByUuid(profile.getId());
        }

        if (profile == null && uuid != null)
            profile = getGameProfileByUuid(uuid);
    }

    @Override
    public void updateUsername()
    {
        /* do nothing */
    }

}
