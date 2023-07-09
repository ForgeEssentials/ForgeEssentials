package com.forgeessentials.tickets;

import com.forgeessentials.commons.selections.WarpPoint;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;

public class Ticket
{

    public int id;

    public String creator;

    public String category;

    public String message;

    public WarpPoint point;

    public Ticket(CommandSource sender, String category, String message)
    {
        id = ModuleTickets.getNextID();
        creator = sender.getTextName();
        if (sender.getEntity() instanceof PlayerEntity)
        {
            point = new WarpPoint((PlayerEntity) sender.getEntity());
        }
        this.category = category;
        this.message = message;
    }

}
