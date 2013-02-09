package com.ForgeEssentials.playerLogger.types;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import com.ForgeEssentials.api.snooper.TextFormatter;
import com.ForgeEssentials.util.OutputHandler;

public class blockChangeLog extends logEntry
{
	public blockChangeLogCategory	cat;
	public String					username;
	public int						dim;
	public int						x;
	public int						y;
	public int						z;
	public String					block;
	public String					te	= "";

	public blockChangeLog(blockChangeLogCategory cat, EntityPlayer player, String block, int X, int Y, int Z, TileEntity te)
	{
		super();
		this.cat = cat;
		username = player.username;
		dim = player.dimension;
		this.block = block;
		x = X;
		y = Y;
		z = Z;
		if (te != null)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			te.writeToNBT(nbt);
			HashMap<String, String> data = new HashMap();
			data.put(te.getClass().getName(), TextFormatter.toJSONnbtComp(nbt));
			this.te = TextFormatter.toJSON(data);
			OutputHandler.finer(te.getClass().getSimpleName());
			OutputHandler.finer(this.te);
			OutputHandler.finer(this.te.length());
		}
	}

	public blockChangeLog()
	{
		super();
	}

	@Override
	public String getName()
	{
		return "blockChange";
	}

	@Override
	public String getTableCreateSQL()
	{
		return "CREATE TABLE IF NOT EXISTS " + getName() + "(id INT UNSIGNED NOT NULL AUTO_INCREMENT,PRIMARY KEY (id), player VARCHAR(32), category VARCHAR(32), block VARCHAR(32), Dim INT, X INT, Y INT, Z INT, time DATETIME, te BLOB)";
	}

	@Override
	public String getprepareStatementSQL()
	{
		return "INSERT INTO " + getName() + " (player, category, block, Dim, X, Y, Z, time, te) VALUES (?,?,?,?,?,?,?,?,?);";
	}

	@Override
	public void makeEntries(Connection connection, List<logEntry> buffer) throws SQLException
	{
		PreparedStatement ps = connection.prepareStatement(getprepareStatementSQL());
		Iterator<logEntry> i = buffer.iterator();
		while (i.hasNext())
		{
			logEntry obj = i.next();
			if (obj instanceof blockChangeLog)
			{
				blockChangeLog log = (blockChangeLog) obj;
				ps.setString(1, log.username);
				ps.setString(2, log.cat.toString());
				ps.setString(3, log.block);
				ps.setInt(4, log.dim);
				ps.setInt(5, log.x);
				ps.setInt(6, log.y);
				ps.setInt(7, log.z);
				ps.setTimestamp(8, log.time);
				if (log.te.equals(""))
				{
					ps.setNull(9, java.sql.Types.BLOB);
				}
				else
				{
					java.sql.Blob blob = new SerialBlob(log.te.getBytes());
					ps.setBlob(9, blob);
				}
				ps.execute();
				ps.clearParameters();
			}
		}
		ps.close();
	}

	public enum blockChangeLogCategory
	{
		broke, placed
	}
}
