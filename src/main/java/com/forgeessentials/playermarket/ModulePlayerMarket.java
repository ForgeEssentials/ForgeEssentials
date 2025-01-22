package com.forgeessentials.playermarket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.playermarket.PlayerMarketData.AuctionStack;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPreInitEvent;
import com.forgeessentials.util.events.ServerEventHandler;
import com.forgeessentials.util.output.LoggingHandler;

@FEModule(name = "PlayerMarket", parentMod = ForgeEssentials.class)
public class ModulePlayerMarket extends ServerEventHandler
{
    public static final String PERM = "fe.playermarket";
    public static final String PERM_CMD = PERM + ".command";
    public static final String PERM_CMD_SELL_BASE = PERM_CMD + ".sell";
    public static final String PERM_CMD_BUY_BASE = PERM_CMD + ".buy";
    public static final String PERM_CMD_SERVER = PERM_CMD + ".server";
    public static final String PERM_CMD_REMOVE = PERM_CMD + ".remove";

    @FEModule.Instance
    protected static ModulePlayerMarket instance;

    @FEModule.ModuleDir
    static File moduleDir;

    public PlayerMarketData data = new PlayerMarketData();

    public static ModulePlayerMarket instance()
    {
        return instance;
    }

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        if (ModuleLauncher.getModuleList().contains("Economy"))
        {
            FECommandManager.registerCommand(new PlayerMarketCommand());
            APIRegistry.perms.registerPermission(PERM + ".*", DefaultPermissionLevel.OP, "Auction House base node");
            APIRegistry.perms.registerPermission(PERM_CMD + ".*", DefaultPermissionLevel.OP, "Auction House Commands");
            APIRegistry.perms.registerPermission(PERM_CMD_SERVER + ".*", DefaultPermissionLevel.OP, "Allows Listing an item as the server!");
            APIRegistry.perms.registerPermission(PERM_CMD_REMOVE + ".*", DefaultPermissionLevel.OP, "Allows removing any item!");

            APIRegistry.perms.registerPermission(PERM_CMD_SELL_BASE + ".*", DefaultPermissionLevel.ALL,
                    "Allows selling a specific item! ex: fe.playermarket.sell.minecraft.iron_block");
            APIRegistry.perms.registerPermission(PERM_CMD_BUY_BASE + ".*", DefaultPermissionLevel.ALL,
                    "Allows buying a specific item! ex: fe.playermarket.buy.minecraft.iron_block");
        }
        else
        {
            LoggingHandler.felog.fatal("PlayerMarket requires the economy module to be enabled!  It has been soft disabled!");
        }
    }

    @SubscribeEvent
    public void serverPreInit(FEModuleServerPreInitEvent e)
    {
        PlayerMarketData data = DataManager.getInstance().load(PlayerMarketData.class, "PlayerMarket");
        if (data != null)
        {
            this.data = data;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void save(WorldEvent.Save event)
    {
        if (event.getWorld() == FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld())
        {
            DataManager.getInstance().save(data, "PlayerMarket");
        }
    }

    private static PrintWriter logWriter;

    public static void logTrade(String action, String user, AuctionStack stack)
    {
        if (logWriter == null)
        {
            File logFile = new File(moduleDir, String.format("Log/%1$tY-%1$tm-%1$te_%1$tH.%1$tM.log", new Date()));
            try
            {
                File dir = logFile.getParentFile();
                if (!dir.exists() && !dir.mkdirs())
                {
                    LoggingHandler.felog.warn(String.format("Could not create market log directory %s!", logFile.getPath()));
                }
                else
                {
                    logWriter = new PrintWriter(logFile);
                }
            }
            catch (FileNotFoundException e)
            {
                LoggingHandler.felog.error(String.format("Could not create market log file %s.", logFile.getAbsolutePath()));
            }
        }

        if (logWriter != null)
        {
            logWriter.println(
                    String.format("Action: %s, User: %s, Seller: %s, Amount: %d, Item: %s", action, user, stack.sellerName, stack.price, stack.stack));
            if (stack.stack.hasTagCompound())
            {
                logWriter.println(stack.stack.getTagCompound());
            }
            logWriter.flush();
        }
    }
}
