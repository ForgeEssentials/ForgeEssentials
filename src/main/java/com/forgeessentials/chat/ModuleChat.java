package com.forgeessentials.chat;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.chat.commands.CommandAutoMessage;
import com.forgeessentials.chat.commands.CommandMail;
import com.forgeessentials.chat.commands.CommandMsg;
import com.forgeessentials.chat.commands.CommandMute;
import com.forgeessentials.chat.commands.CommandNickname;
import com.forgeessentials.chat.commands.CommandPm;
import com.forgeessentials.chat.commands.CommandR;
import com.forgeessentials.chat.commands.CommandUnmute;
import com.forgeessentials.chat.irc.IRCChatFormatter;
import com.forgeessentials.chat.irc.IRCHelper;
import com.forgeessentials.chat.irc.PlayerEventHandler;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.compat.CommandSetChecker;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

@FEModule(name = "Chat", parentMod = ForgeEssentials.class, configClass = ConfigChat.class)
public class ModuleChat {
    @FEModule.Config
    public static ConfigChat conf;

    @FEModule.ModuleDir
    public static File moduleDir;

    public static PrintWriter chatLog;
    public static PrintWriter cmdLog;
    public static File logdir;
    public static boolean connectToIRC;
    private MailSystem mailsystem;
    private PlayerEventHandler ircPlayerHandler;

    @SubscribeEvent
    public void load(FMLInitializationEvent e)
    {

        MinecraftForge.EVENT_BUS.register(new ChatFormatter());
        MinecraftForge.EVENT_BUS.register(new CommandMuter());

        if (!IRCHelper.suppressEvents && connectToIRC)
        {
            ircPlayerHandler = new PlayerEventHandler();
            MinecraftForge.EVENT_BUS.register(ircPlayerHandler);
            FMLCommonHandler.instance().bus().register(ircPlayerHandler);
            MinecraftForge.EVENT_BUS.register(new IRCChatFormatter());
        }
    }

    @SubscribeEvent
    public void postLoad(FMLPostInitializationEvent e)
    {
        mailsystem = new MailSystem();

    }

    @SubscribeEvent
    public void serverStarting(FMLServerStartingEvent e)
    {
        FunctionHelper.registerServerCommand(new CommandMsg());
        FunctionHelper.registerServerCommand(new CommandR());
        FunctionHelper.registerServerCommand(new CommandNickname());
        FunctionHelper.registerServerCommand(new CommandPm());
        FunctionHelper.registerServerCommand(new CommandMute());
        FunctionHelper.registerServerCommand(new CommandUnmute());
        FunctionHelper.registerServerCommand(new CommandMail());
        FunctionHelper.registerServerCommand(new CommandAutoMessage());

        try
        {
            logdir = new File(moduleDir, "logs/");
            logdir.mkdirs();
        }
        catch (Exception e1)
        {
            OutputHandler.felog.warning("Could not create chat log directory!");
        }
        try
        {
            File file = new File(logdir, "chat-" + FunctionHelper.getCurrentDateString() + ".log");
            if (!file.exists())
            {
                file.createNewFile();
            }
            chatLog = new PrintWriter(file);
        }
        catch (Exception e1)
        {
            OutputHandler.felog.warning("Could not create chat log file!");
        }

        try
        {
            File file = new File(logdir, "cmd-" + FunctionHelper.getCurrentDateString() + ".log");
            if (!file.exists())
            {
                file.createNewFile();
            }
            cmdLog = new PrintWriter(file);
        }
        catch (Exception e1)
        {
            OutputHandler.felog.warning("Could not create command log file!");
        }

        APIRegistry.perms.registerPermission("fe.chat.usecolor", RegisteredPermValue.TRUE);
        APIRegistry.perms.registerPermission("fe.chat.nickname.others", RegisteredPermValue.OP);
    }

    @SubscribeEvent
    public void serverStarted(FMLServerStartedEvent e)
    {
        removeTell(FMLCommonHandler.instance().getMinecraftServerInstance());
        new AutoMessage(FMLCommonHandler.instance().getMinecraftServerInstance());
        MailSystem.LoadAll();
        FMLCommonHandler.instance().bus().register(mailsystem);
        if (connectToIRC)
        {
            IRCHelper.connectToServer();
        }
    }

    @SubscribeEvent
    public void serverStopping(FMLServerStoppingEvent e)
    {
        MailSystem.SaveAll();

        chatLog.close();
        cmdLog.close();
        if (connectToIRC)
        {
            IRCHelper.shutdown();
        }
    }

    private void removeTell(MinecraftServer server)
    {
        if (server.getCommandManager() instanceof CommandHandler)
        {
            try
            {
                Set<?> cmdList = ReflectionHelper
                        .getPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), CommandSetChecker.FIELDNAME);

                ICommand toRemove = null;
                Class<?> cmdClass = null;
                for (Object cmdObj : cmdList)
                {
                    ICommand cmd = (ICommand) cmdObj;
                    if (cmd.getCommandName().equalsIgnoreCase("tell"))
                    {
                        try
                        {
                            cmdClass = cmd.getClass();
                            Package pkg = cmdClass.getPackage();
                            if (pkg == null || !pkg.getName().contains("ForgeEssentials"))
                            {
                                toRemove = cmd;
                                break;
                            }
                        }
                        catch (Exception e)
                        {
                            OutputHandler.felog.finer("Can't remove " + cmd.getCommandName());
                            OutputHandler.felog.finer("" + e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    }
                }
                if (toRemove != null)
                {
                    OutputHandler.felog.finer("Removing command '" + toRemove.getCommandName() + "' from class: " + cmdClass.getName());
                    cmdList.remove(toRemove);
                }
                ReflectionHelper
                        .setPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), cmdList, "commandSet", "b", "field_71561_b");

                Map<String, CommandMsg> cmds = ReflectionHelper
                        .getPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), "commandMap", "a", "field_71562_a");
                if (cmds.containsKey("tell"))
                {
                    OutputHandler.felog.finer("Removing command tell from vanilla set.");
                    cmds.remove("tell");
                    cmds.put("tell", new CommandMsg());
                }
                ReflectionHelper.setPrivateValue(CommandHandler.class, (CommandHandler) server.getCommandManager(), cmds, "commandMap", "a", "field_71562_a");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

}
