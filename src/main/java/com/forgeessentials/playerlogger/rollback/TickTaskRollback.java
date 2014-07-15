package com.forgeessentials.playerlogger.rollback;

import com.forgeessentials.api.TextFormatter;
import com.forgeessentials.playerlogger.blockChange;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.tasks.ITickTask;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class TickTaskRollback implements ITickTask {
    private boolean isComplete = false;
    private ICommandSender sender;
    private int changed = 0;
    private boolean undo;
    private WorldServer world;
    private Iterator<blockChange> i;
    private blockChange bc;

    /**
     * @param sender
     * @param undo@throws SQLException
     */
    public TickTaskRollback(ICommandSender sender, boolean undo, ArrayList<blockChange> changes) throws SQLException
    {
        this.sender = sender;
        this.undo = undo;
        this.i = changes.iterator();
    }

    @Override
    public void tick()
    {
        int currentTickChanged = 0;
        boolean continueFlag = true;

        while (continueFlag)
        {
            try
            {
                if (i.hasNext())
                {
                    bc = i.next();
                    world = FunctionHelper.getDimension(bc.dim);

                    if (bc.type == 0)
                    {
                        if (!undo)
                        {
                            place();
                        }
                        else
                        {
                            remove();
                        }
                    }
                    else if (bc.type == 1)
                    {
                        if (undo)
                        {
                            place();
                        }
                        else
                        {
                            remove();
                        }
                    }
                    currentTickChanged++;
                    world.markBlockForUpdate(bc.X, bc.Y, bc.Z);
                }
                else
                {
                    isComplete = true;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            if (isComplete || currentTickChanged >= 20)
            {
                // Stop running this tick.
                changed += currentTickChanged;
                continueFlag = false;
            }
        }
    }

    public void place() throws SQLException
    {
        String[] block = bc.block.split(":");

        Block blockPlace = GameData.getBlockRegistry().getObject(block[0]); //legacy
        world.setBlock(bc.X, bc.Y, bc.Z, blockPlace, Integer.parseInt(block[1]), 2);
        if (bc.te != null)
        {
            try
            {
                Blob blob = bc.te;
                byte[] bdata = blob.getBytes(1, (int) blob.length());
                System.out.println(new String(bdata));
                TileEntity te = TextFormatter.reconstructTE(new String(bdata));
                world.setTileEntity(bc.X, bc.Y, bc.Z, te);
            }
            catch (Exception e)
            {
                ChatUtils.sendMessage(sender, "Could not restore TE data.");
                e.printStackTrace();
            }
        }
    }

    public void remove() throws SQLException
    {
        world.removeTileEntity(bc.X, bc.Y, bc.Z);
        world.setBlock(bc.X, bc.Y, bc.Z, Blocks.air);
    }

    @Override
    public void onComplete()
    {
        ChatUtils.sendMessage(sender, "Rollback done! Changed " + changed + " blocks.");
    }

    @Override
    public boolean isComplete()
    {
        return isComplete;
    }

    @Override
    public boolean editsBlocks()
    {
        return true;
    }

}
