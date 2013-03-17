package com.ForgeEssentials.playerLogger.types;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import com.ForgeEssentials.api.json.JSONObject;
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
	public java.sql.Blob			te;

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

			try
			{
				this.te = new SerialBlob(new JSONObject().put(te.getClass().getName(), TextFormatter.toJSONnbtComp(nbt).toString()).toString().getBytes());
			}
			catch (Exception e)
			{
				OutputHandler.severe(e);
				e.printStackTrace();
			}
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
		return "CREATE TABLE IF NOT EXISTS " + getName() + "(id INT UNSIGNED NOT NULL AUTO_INCREMENT,PRIMARY KEY (id), player VARCHAR(32), category VARCHAR(32), block VARCHAR(32), Dim INT, X INT, Y INT, Z INT, time DATETIME, te LONGBLOB)";
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
				ps.setBlob(9, log.te);
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
