package com.forgeessentials.commands;

import java.util.HashMap;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandPotion extends FEcmdModuleCommands {
    public static HashMap<String, Integer> names;

    static
    {
        names = new HashMap<String, Integer>();
        names.put("speed", 1);
        names.put("slowness", 2);
        names.put("haste", 3);
        names.put("miningfatigue", 4);
        names.put("strength", 5);
        names.put("heal", 6);
        names.put("damage", 7);
        names.put("jumpboost", 8);
        names.put("nausea", 9);
        names.put("regeneration", 10);
        names.put("resistance", 11);
        names.put("fireresistance", 12);
        names.put("waterbreathing", 13);
        names.put("invisibility", 14);
        names.put("blindness", 15);
        names.put("nightvision", 16);
        names.put("hunger", 17);
        names.put("weakness", 18);
        names.put("poison", 19);
        names.put("wither", 20);
    }

    @Override
    public String getCommandName()
    {
        return "potion";
    }

	/*
     * Expected syntax: /potion player effect duration [ampl]
	 */

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        int ID = 0;
        int dur = 0;
        int ampl = 0;

        if (args.length == 4)
        {
            ampl = parseIntWithMin(sender, args[3], 0);
        }
        else if (args.length != 3)
        {
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: <player> <effect> <duration> [ampl]");
            return;
        }

        if (names.containsKey(args[1]))
        {
            ID = names.get(args[1]);
        }
        else
        {
            OutputHandler.chatError(sender, "That potion effect was not found.");
            return;
        }

        dur = parseIntWithMin(sender, args[2], 0) * 20;

        PotionEffect eff = new PotionEffect(ID, dur, ampl);
        if (args[0].equalsIgnoreCase("me"))
        {
            sender.addPotionEffect(eff);
        }
        else if (PermissionsManager.checkPermission(sender, getPermissionNode() + ".others"))
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);

            if (player != null)
            {
                player.addPotionEffect(eff);
            }
            else
            {
                OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
            }
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        int ID = 0;
        int dur = 0;
        int ampl = 0;

        if (args.length == 4)
        {
            ampl = parseIntWithMin(sender, args[3], 0);
        }
        else if (args.length != 3)
        {
            OutputHandler.sendMessage(sender, "Improper syntax. Please try this instead: <player> <effect> <duration> [ampl]");
            return;
        }

        dur = parseIntWithMin(sender, args[2], 0) * 20;
        PotionEffect eff = new PotionEffect(ID, dur, ampl);

        EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);

        if (player != null)
        {
            player.addPotionEffect(eff);
        }
        else
        {
            OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else if (args.length == 2)
        {
            return getListOfStringsFromIterableMatchingLastWord(args, names.keySet());
        }
        else
        {
            return null;
        }
    }

    @Override
    public void registerExtraPermissions()
    {
        PermissionsManager.registerPermission(getPermissionNode() + ".others", RegisteredPermValue.OP);
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/potion <player> <effect> <duration> [ampl] Give the specified player a potion effect.";
    }

}
