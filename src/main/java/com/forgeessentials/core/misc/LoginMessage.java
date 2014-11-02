package com.forgeessentials.core.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.compat.CompatReiMinimap;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;

import cpw.mods.fml.common.FMLCommonHandler;

public class LoginMessage {
    private static List<String> messageList = new ArrayList<String>();
    private static MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

    public static void loadFile()
    {
        messageList.clear();
        File file = new File(ForgeEssentials.FEDIR, "MOTD.txt");
        if (file.exists())
        {
            try
            {
                try (BufferedReader br = new BufferedReader(new FileReader(file)))
                {
                    while (br.ready())
                    {
                        String line = br.readLine().trim();
                        if (!(line.startsWith("#") || line.isEmpty()))
                        {
                            messageList.add(line);
                        }
                    }
                }
            }
            catch (IOException e)
            {
                OutputHandler.felog.info("Error reading the MOTD file.");
            }
        }
        else
        {
            try
            {
                file.createNewFile();
                try (PrintWriter pw = new PrintWriter(file))
                {
                    pw.println("# This file contains the message sent to the player on login.");
                    pw.println("# Lines starting with # are not read.");
                    pw.println("# There are several codes that can be used to format the text.");
                    pw.println("# If you want to use color, use this symbol & (ALT code 21) to indicate a color.");
                    pw.println("# Handy link: http://www.minecraftwiki.net/wiki/Formatting_codes");
                    pw.println("# Other codes:");
                    pw.println("# %playername% => The name of the player the message is send to");
                    pw.println("# %rank% => Players rank (see chat config)");
                    pw.println("# %groupPrefix% => Group prefix (see chat config)");
                    pw.println("# %groupSuffix% => Group suffix (see chat config)");
                    pw.println("# %playerPrefix% => Player prefix (see chat config)");
                    pw.println("# %playerSuffix% => Player suffix (see chat config)");
                    // pw.println("# %balance% => Prints the users balance (economy)");
                    pw.println("# %players% => Amount of players online.");
                    pw.println("# %uptime% => Current server uptime.");
                    pw.println("# %uniqueplayers% => Amount of unique player logins.");
                    pw.println("# %time% => Local server time. All in one string.");
                    pw.println("# %hour% ; %min% ; %sec% => Local server time.");
                    pw.println("# %day% ; %month% ; %year% => Local server date.");
                    pw.println("# %online => Nice list of online players.");
                    pw.println("# ");
                    pw.println("# If you would like more codes, you can make an issue on https://github.com/ForgeEssentials/ForgeEssentialsMain/issues");
                    pw.println("");
                    pw.println("Welcome %playername%, to a server running ForgeEssentials.");
                    pw.println("There are %players% players online, and we have had %uniqueplayers% unique players.");
                    pw.println("Server time: %time%. Uptime: %uptime%");
                }
                loadFile();
            }
            catch (Exception e)
            {
                OutputHandler.felog.info("Error reading the MOTD file.");
                e.printStackTrace();
            }

        }
    }

    public static void setMOTD(Collection<String> messages) {
    	messageList.clear();
    	messageList.addAll(messages);
    }
    
    public static void sendLoginMessage(ICommandSender sender)
    {
        for (int id = 0; id < messageList.size(); id++)
        {
            if (id == 0)
            {
                if (sender instanceof EntityPlayer)
                {
                    OutputHandler.sendMessage(sender, CompatReiMinimap.reimotd((EntityPlayer) sender) + Format(messageList.get(id), sender.getCommandSenderName()));
                }
                else
                {
                    OutputHandler.sendMessage(sender, Format(messageList.get(id), sender.getCommandSenderName()));
                }
            }
            else
            {
                OutputHandler.sendMessage(sender, Format(messageList.get(id), sender.getCommandSenderName()));
            }
        }
    }

    /**
     * Formats the chat, replacing given strings by their values
     *
     * @param String to parse the amount to add to the WalletHandler
     */
    private static String Format(String line, String playerName)
    {
        Calendar cal = Calendar.getInstance();

        // int WalletHandler = WalletHandler.getWalletHandler(player); //needed to return WalletHandler info
        line = FunctionHelper.formatColors(line); // colors...
        line = FunctionHelper.format(line);

        line = FunctionHelper.replaceAllIgnoreCase(line, "%players%", online()); // players online
        line = FunctionHelper.replaceAllIgnoreCase(line, "%uptime%", getUptime()); // uptime
        line = FunctionHelper.replaceAllIgnoreCase(line, "%uniqueplayers%", uniqueplayers()); // unique players
        line = FunctionHelper.replaceAllIgnoreCase(line, "%online", FunctionHelper.getFormattedPlayersOnline()); // All online players

        // time stuff
        line = FunctionHelper.replaceAllIgnoreCase(line, "%time%", FunctionHelper.getCurrentTimeString());
        line = FunctionHelper.replaceAllIgnoreCase(line, "%hour%", "" + cal.get(Calendar.HOUR));
        line = FunctionHelper.replaceAllIgnoreCase(line, "%min%", "" + cal.get(Calendar.MINUTE));
        line = FunctionHelper.replaceAllIgnoreCase(line, "%sec%", "" + cal.get(Calendar.SECOND));
        line = FunctionHelper.replaceAllIgnoreCase(line, "%day%", "" + cal.get(Calendar.DAY_OF_MONTH));
        line = FunctionHelper.replaceAllIgnoreCase(line, "%month%", "" + cal.get(Calendar.MONTH));
        line = FunctionHelper.replaceAllIgnoreCase(line, "%year%", "" + cal.get(Calendar.YEAR));

        //line = FunctionHelper.replaceAllIgnoreCase(line, "%rank%", FunctionHelper.getGroupRankString(playerName));

        line = FunctionHelper.replaceAllIgnoreCase(line, "%playerPrefix%", FunctionHelper.formatColors(FunctionHelper.getPlayerPrefixSuffix(new UserIdent(playerName), false)).trim());
        line = FunctionHelper.replaceAllIgnoreCase(line, "%playerSuffix%", FunctionHelper.formatColors(FunctionHelper.getPlayerPrefixSuffix(new UserIdent(playerName), true)).trim());
        line = FunctionHelper.replaceAllIgnoreCase(line, "%groupPrefix%", FunctionHelper.formatColors(FunctionHelper.getPlayerGroupPrefixSuffix(new UserIdent(playerName), false)).trim());
        line = FunctionHelper.replaceAllIgnoreCase(line, "%groupSuffix%", FunctionHelper.formatColors(FunctionHelper.getPlayerGroupPrefixSuffix(new UserIdent(playerName), true)).trim());

        // Player stuff
        EntityPlayer player = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().func_152612_a(playerName);
        if (player != null) {
            line = FunctionHelper.replaceAllIgnoreCase(line, "%playername%", player.getDisplayName()); // username
        }

        return line;
    }

    private static String online()
    {
        int online = 0;
        try
        {
            online = server.getCurrentPlayerCount();
        }
        catch (Exception e)
        {
        }
        return "" + online;
    }

    private static String uniqueplayers()
    {
        int logins = 0;
        try
        {
            logins = server.getConfigurationManager().getAvailablePlayerDat().length;
        }
        catch (Exception e)
        {
        }
        return "" + logins;
    }

    public static String getUptime()
    {
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        int secsIn = (int) (rb.getUptime() / 1000);
        return FunctionHelper.parseTime(secsIn);
    }
    
}