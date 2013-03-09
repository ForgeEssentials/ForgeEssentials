package com.ForgeEssentials.playerLogger;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.minecraft.command.ICommandSender;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;

import com.ForgeEssentials.WorldControl.ConfigWorldControl;
import com.ForgeEssentials.api.snooper.TextFormatter;
import com.ForgeEssentials.playerLogger.types.blockChangeLog;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.tasks.ITickTask;

public class TickTaskRollback implements ITickTask
{
	private boolean				isComplete			= false;
	private ICommandSender		sender;
	private ResultSet			rs;
	private int					changed				= 0;
	private boolean				undo;
	private WorldServer			world;
	private int					X;
	private int					Y;
	private int					Z;
	private String				username;
	private Connection			connection;
	private Statement			st;

	public TickTaskRollback(ICommandSender sender, String username, boolean undo) throws SQLException
	{
		this.sender = sender;
		this.undo = undo;
		this.username = username;
		connection = DriverManager.getConnection(ModulePlayerLogger.url, ModulePlayerLogger.username, ModulePlayerLogger.password);
		st = connection.createStatement();

		if (undo)
		{
			st.execute("SELECT * FROM  `blockChange` WHERE  `player` LIKE  '" + username + "' ORDER BY time ASC");
		}
		else
		{
			st.execute("SELECT * FROM  `blockChange` WHERE  `player` LIKE  '" + username + "' ORDER BY time DESC");
		}
		rs = st.getResultSet();
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
				if (rs.next())
				{
					world = FunctionHelper.getDimension(rs.getInt("Dim"));

					X = rs.getInt("X");
					Y = rs.getInt("Y");
					Z = rs.getInt("Z");

					if (rs.getString("category").equalsIgnoreCase(blockChangeLog.blockChangeLogCategory.placed.toString()))
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
					else if (rs.getString("category").equalsIgnoreCase(blockChangeLog.blockChangeLogCategory.broke.toString()))
					{
						if (undo)
						{
							remove();
						}
						else
						{
							place();
						}
					}
					currentTickChanged++;
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

			if (isComplete || currentTickChanged >= ConfigWorldControl.blocksPerTick)
			{
				// Stop running this tick.
				changed += currentTickChanged;
				continueFlag = false;
			}
		}
	}

	public void place() throws SQLException
	{
		String[] block = rs.getString("block").split(":");
		world.setBlockAndMetadataWithNotify(X, Y, Z, Integer.parseInt(block[0]), Integer.parseInt(block[1]));
		if (rs.getBlob("te") != null)
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

	public void remove() throws SQLException
	{
		world.removeBlockTileEntity(X, Y, Z);
		world.setBlock(X, Y, Z, 0);
	}

	@Override
	public void onComplete()
	{
		sender.sendChatToPlayer("Rollback done! Changed " + changed + " blocks. You can undo by using '/rb " + username + " undo'");
		try
		{
			rs.close();
			st.close();
			connection.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
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
