package com.forgeessentials.tickets;

import com.forgeessentials.commons.selections.WarpPoint;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Player;

public class Ticket
{

    public int id;

    public String creator;

    public String category;

    public String message;

    public WarpPoint point;

    public Ticket(CommandSourceStack sender, String category, String message)
    {
        id = ModuleTickets.getNextID();
        creator = sender.getTextName();
        if (sender.getEntity() instanceof Player)
        {
            point = new WarpPoint((Player) sender.getEntity());
        }
        this.category = category;
        this.message = message;
    }

}
