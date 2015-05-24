package com.forgeessentials.commands;

import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.commons.network.Packet5Noclip;
import com.forgeessentials.commons.network.Packet6Speed;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commands.util.CommandDataManager;
import com.forgeessentials.commands.util.CommandRegistrar;
import com.forgeessentials.commands.util.CommandsEventHandler;
import com.forgeessentials.commands.util.ConfigCmd;
import com.forgeessentials.commands.util.LoginMessage;
import com.forgeessentials.commands.util.MobTypeLoader;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStopEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;

@FEModule(name = "Commands", parentMod = ForgeEssentials.class)
public class ModuleCommands {
    
    public static CommandsEventHandler eventHandler = new CommandsEventHandler();

    @SubscribeEvent
    public void preLoad(FEModulePreInitEvent e)
    {
        MobTypeLoader.preLoad((FMLPreInitializationEvent)e.getFMLEvent());
        MinecraftForge.EVENT_BUS.register(eventHandler);
        FMLCommonHandler.instance().bus().register(eventHandler);
        NetworkUtils.netHandler.registerMessage(new IMessageHandler<Packet5Noclip, IMessage>()
        {

            @Override
            public IMessage onMessage(Packet5Noclip message, MessageContext ctx)
            {
                return null;
            }
        }, Packet5Noclip.class, 5, Side.CLIENT);
        NetworkUtils.netHandler.registerMessage(new IMessageHandler<Packet6Speed, IMessage>()
        {
            @Override
            public IMessage onMessage(Packet6Speed message, MessageContext ctx)
            {
                return null;
            }
        }, Packet6Speed.class, 6, Side.CLIENT);
        LoginMessage.loadFile();
    }

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        ForgeEssentials.getConfigManager().registerLoader("Commands", new ConfigCmd());
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
    	CommandRegistrar.registerCommands(e);
        CommandDataManager.load();
        APIRegistry.perms.registerPermissionDescription("fe.commands", "Permission nodes for FE commands module");
        APIRegistry.perms.registerPermission("fe.commands" + Zone.ALL_PERMS, RegisteredPermValue.OP);
    }

    @SubscribeEvent
    public void serverStopping(FEModuleServerStopEvent e)
    {
        CommandDataManager.save();
    }

}
