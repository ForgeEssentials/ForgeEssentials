package com.forgeessentials.playermarket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
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
    public static final String PERM_LIMIT = PERM + ".limit";
    public static final String PERM_TIMEOUT = PERM + ".timeout";
    public static final String PERM_TIMEOUT_MAX = PERM_TIMEOUT + ".max";
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
    public List<WeakReference<AuctionStack>> timeoutStacks = new ArrayList<>();

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

            APIRegistry.perms.registerPermissionProperty(PERM_TIMEOUT_MAX, null, "Max / Default timeout for selling items");
            APIRegistry.perms.registerPermissionProperty(PERM_LIMIT, null, "Per player limit for selling items");
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

            for (AuctionStack stack : data.itemsListed)
            {
                if (stack.hasTimeout && stack.timeout > 0)
                {
                    timeoutStacks.add(new WeakReference<>(stack));
                }
            }
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

    public static synchronized void writeTrade(String msg)
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
            logWriter.println(msg);
            logWriter.flush();
        }
    }
    public static void logTrade(String action, String user, AuctionStack stack)
    {
        AuctionStack _stack = stack.copy();
        new Thread(() -> {
            String msg = String.format("%1$tY-%1$tm-%1$te %1$tH:%1$tM:%1$tS.%1$tL", new Date());
            msg += String.format(" Action: %s, User: %s, Seller: %s, Amount: %d, Item: %s\n", action, user, _stack.sellerName, _stack.price,
                    _stack.stack);
            if (_stack.stack.hasTagCompound())
            {
                msg += _stack.stack.getTagCompound();
            }
            writeTrade(msg);
        }).start();
    }

    long ticks = 0;

    @SubscribeEvent
    public void tick(ServerTickEvent e)
    {
        if (e.phase != Phase.START)
        {
            return;
        }

        ticks++;
        if (ticks % 20 == 0)
        {
            List<WeakReference<AuctionStack>> removalQueue = new ArrayList<>();

            for (WeakReference<AuctionStack> weakStack : timeoutStacks)
            {
                AuctionStack stack = weakStack.get();
                if (stack != null && stack.hasTimeout)
                {
                    if (stack.timeout > 0)
                    {
                        stack.timeout--;
                    }
                    else
                    {
                        removalQueue.add(weakStack);
                    }
                }
            }

            timeoutStacks.removeAll(removalQueue);
        }
    }
}
