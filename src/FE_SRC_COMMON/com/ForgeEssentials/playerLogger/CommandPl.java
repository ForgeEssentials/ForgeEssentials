package com.ForgeEssentials.playerLogger;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;

import com.ForgeEssentials.api.snooper.TextFormatter;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.playerLogger.types.blockChangeLog;
import com.ForgeEssentials.util.FunctionHelper;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Main playerlogger command. Still WIP
 * 
 * @author Dries007
 * 
 */

public class CommandPl extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "playerlogger";
	}

	@Override
	public List getCommandAliases()
	{
		return Arrays.asList(new String[] {"pl"});
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if(sender.worldObj.isRemote) return;
		if (args.length == 0)
		{
			// TODO INFO
			
			return;
		}
		if (args[0].equalsIgnoreCase("get"))
		{
			int limit = 5;
			if (args.length == 2)
			{
				limit = parseIntWithMin(sender, args[1], 0);
			}
			sender.getEntityData().setBoolean("lb", true);
			sender.getEntityData().setInteger("lb_limit", limit);
			sender.sendChatToPlayer("Click a block and you will get the last " + limit + " changes.");
		}
		if (args[0].equalsIgnoreCase("rollback") || args[0].equalsIgnoreCase("rb"))
		{
			try
			{
				String username = FunctionHelper.getPlayerFromUsername(args[1]).username;
				Connection connection = DriverManager.getConnection(ModulePlayerLogger.url, ModulePlayerLogger.username, ModulePlayerLogger.password);
				Statement st = connection.createStatement();
				st.execute("SELECT * FROM  `blockchange` WHERE  `player` LIKE  '" + username + "'");
				ResultSet res = st.getResultSet();

				sender.sendChatToPlayer("Results:");

				while (res.next())
				{
					String teString = res.getBlob("te") == null ? "no" : "yes";
					sender.sendChatToPlayer(res.getString("player") + " " + res.getString("category") + " block " + res.getString("block") + " at " + res.getTimestamp("time") + " TE: " + teString);
					
					WorldServer world = FunctionHelper.getDimension(res.getInt("Dim"));
					
					int X = res.getInt("X");
					int Y = res.getInt("Y");
					int Z = res.getInt("Z");
					
					if(res.getString("category").equalsIgnoreCase(blockChangeLog.blockChangeLogCategory.placed.toString()))
					{
						world.removeBlockTileEntity(X, Y, Z);
						world.setBlock(X, Y, Z, 0);
					}
					else if(res.getString("category").equalsIgnoreCase(blockChangeLog.blockChangeLogCategory.broke.toString()))
					{
						String[] block = res.getString("block").split(":");
						world.setBlockAndMetadataWithNotify(X, Y, Z, Integer.parseInt(block[0]), Integer.parseInt(block[1]));
					}
					if(res.getBlob("te") != null)
					{
						try
						{
							Blob blob = res.getBlob("te");
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
					world.markBlockForUpdate(X, Y, Z);
				}
			}
			catch (Exception e)
			{
				sender.sendChatToPlayer("Error.");
				e.printStackTrace();
			}
		}
		// TODO add further stuff.
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{

	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return "";
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, "get", "rollback");
		}
		else if (args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
		}
		else
		{
			return null;
		}
	}
}
