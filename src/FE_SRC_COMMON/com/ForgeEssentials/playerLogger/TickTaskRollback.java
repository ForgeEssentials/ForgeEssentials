package com.ForgeEssentials.playerLogger;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.minecraft.command.ICommandSender;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;

import com.ForgeEssentials.WorldControl.ModuleWorldControl;
import com.ForgeEssentials.api.snooper.TextFormatter;
import com.ForgeEssentials.playerLogger.types.blockChangeLog;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.ITickTask;
import com.ForgeEssentials.util.AreaSelector.Point;

public class TickTaskRollback implements ITickTask
{
	private boolean isComplete = false;
	private ICommandSender sender;
	private ResultSet rs;
	private int changed = 0;
	
	public TickTaskRollback(ICommandSender sender, ResultSet rs)
	{
		this.sender = sender;
		this.rs = rs;
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
				if(rs.next())
				{
					WorldServer world = FunctionHelper.getDimension(rs.getInt("Dim"));
					
					int X = rs.getInt("X");
					int Y = rs.getInt("Y");
					int Z = rs.getInt("Z");
					
					if(rs.getString("category").equalsIgnoreCase(blockChangeLog.blockChangeLogCategory.placed.toString()))
					{
						world.removeBlockTileEntity(X, Y, Z);
						world.setBlock(X, Y, Z, 0);
					}
					else if(rs.getString("category").equalsIgnoreCase(blockChangeLog.blockChangeLogCategory.broke.toString()))
					{
						String[] block = rs.getString("block").split(":");
						world.setBlockAndMetadataWithNotify(X, Y, Z, Integer.parseInt(block[0]), Integer.parseInt(block[1]));
						if(rs.getBlob("te") != null)
						{
							try
							{
								Blob blob = rs.getBlob("te");
								byte[] bdata = blob.getBytes(1, (int) blob.length());
								System.out.println(new String(bdata));
								TileEntity te = TextFormatter.reconstructTE(new String(bdata));
								world.setBlockTileEntity(X, Y, Z, te);
							}
							catch (Exception e)
							{
								sender.sendChatToPlayer("Could not restore TE data.");
								e.printStackTrace();
							}			
						}
					}
					world.markBlockForUpdate(X, Y, Z);
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
				
			currentTickChanged++;
			if (isComplete || currentTickChanged >= ModuleWorldControl.WCblocksPerTick)
			{
				// Stop running this tick.
				changed  += currentTickChanged;
				continueFlag = false;
			}
		}
	}
	
	@Override
	public void onComplete()
	{
		sender.sendChatToPlayer("Rollback done! Changed " + changed + " blocks.");
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
