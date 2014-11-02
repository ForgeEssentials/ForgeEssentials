package com.forgeessentials.economy;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.economy.commands.CommandAddToWallet;
import com.forgeessentials.economy.commands.CommandGetWallet;
import com.forgeessentials.economy.commands.CommandMoney;
import com.forgeessentials.economy.commands.CommandPaidCommand;
import com.forgeessentials.economy.commands.CommandPay;
import com.forgeessentials.economy.commands.CommandRemoveWallet;
import com.forgeessentials.economy.commands.CommandSellCommand;
import com.forgeessentials.economy.commands.CommandSetWallet;
import com.forgeessentials.economy.commands.plots.CommandBuyPlot;
import com.forgeessentials.economy.commands.plots.CommandListPlot;
import com.forgeessentials.economy.commands.plots.CommandRemovePlot;
import com.forgeessentials.economy.commands.plots.CommandSellPlot;
import com.forgeessentials.economy.commands.plots.CommandSetPlot;
import com.forgeessentials.economy.plots.PlotManager;
import com.forgeessentials.util.FunctionHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

import java.io.File;

/**
 * Call the WalletHandler class when working with Economy
 */
@FEModule(name = "Economy", parentMod = ForgeEssentials.class, configClass = ConfigEconomy.class)
public class ModuleEconomy {
    @FEModule.Config
    public static ConfigEconomy config;

    @FEModule.ModuleDir
    public static File moduleDir;

    public static int startbudget;

    public static int psfPrice;
    
    @SubscribeEvent
    public void preLoad(FMLPreInitializationEvent e)
    {
        FunctionHelper.netHandler.registerMessage(S4PacketEconomy.class, S4PacketEconomy.class, 4, Side.CLIENT);
    }

    @SubscribeEvent
    public void load(FMLInitializationEvent e)
    {
        APIRegistry.wallet = new WalletHandler();
        FMLCommonHandler.instance().bus().register(APIRegistry.wallet);
    }

    @SubscribeEvent
    public void serverStarting(FMLServerStartingEvent e)
    {
        FunctionHelper.registerServerCommand(new CommandAddToWallet());
        FunctionHelper.registerServerCommand(new CommandRemoveWallet());
        FunctionHelper.registerServerCommand(new CommandGetWallet());
        FunctionHelper.registerServerCommand(new CommandSetWallet());
        FunctionHelper.registerServerCommand(new CommandPay());
        FunctionHelper.registerServerCommand(new CommandPaidCommand());
        FunctionHelper.registerServerCommand(new CommandSellCommand());
        FunctionHelper.registerServerCommand(new CommandMoney());
        FunctionHelper.registerServerCommand(new CommandBuyPlot());
        FunctionHelper.registerServerCommand(new CommandRemovePlot());
        FunctionHelper.registerServerCommand(new CommandSellPlot());
        FunctionHelper.registerServerCommand(new CommandSetPlot());
        FunctionHelper.registerServerCommand(new CommandListPlot());
        PlotManager.load();
    }

    @SubscribeEvent
    public void serverStop(FMLServerStoppingEvent e)
    {
        PlotManager.save();
    }
}
