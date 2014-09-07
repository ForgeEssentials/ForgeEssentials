package com.forgeessentials.economy.commands;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import java.util.Arrays;
import java.util.List;

public class CommandSellCommand extends ForgeEssentialsCommandBase {
    @Override
    public String getCommandName()
    {
        return "sellcommand";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Arrays.asList("sc", "scmd");
    }

    /*
     * Expected structure: "/sellcommand <player> <['amount'x]item[:'meta']> <command [args]>"
     */
    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        System.out.print(sender);
        if (args.length >= 3)
        {

            EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
            if (player != null)
            {

                boolean found = false;
                // Set needed parm
                int amount = 1, meta = -1;
                String item = null;
                ItemStack target = new ItemStack((Item)GameData.getItemRegistry().getObject(item), amount, meta);

                if (args[1].contains("x"))
                {
                    String[] split = args[1].split("x");
                    target.stackSize = amount = parseIntBounded(sender, split[0], 0, 64);
                    args[1] = split[1];
                }
                if (args[1].contains(":"))
                {
                    String[] split = args[1].split(":");
                    target.setItemDamage(meta = parseInt(sender, split[1]));
                    args[1] = split[0];
                }
                item = args[1];
                // Loop though inv and find a stack big enough to support the sell cmd
                for (int slot = 0; slot < player.inventory.mainInventory.length; slot++)
                {
                    ItemStack is = player.inventory.mainInventory[slot];
                    if (is != null)
                    {
                        if (is.getUnlocalizedName() == item)
                        {
                            if (meta == -1 || meta == is.getItemDamage())
                            {
                                if (is.stackSize >= amount)
                                {
                                    player.inventory.decrStackSize(slot, amount);
                                    found = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (found)
                {
                    // Do command in name of player

                    StringBuilder cmd = new StringBuilder(args.toString().length());
                    for (int i = 2; i < args.length; i++)
                    {
                        cmd.append(args[i]);
                        cmd.append(" ");
                    }
                    MinecraftServer.getServer().getCommandManager().executeCommand(player, cmd.toString());
                    OutputHandler.chatConfirmation(player, "That cost you " + amount + " x " + target.getDisplayName());
                }
                else
                {
                    //this should be removed
                    OutputHandler.chatError(player, "You can't afford that!!");
                }
            }
            else
            {
                //this should be removed
                OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
            }
        }
        else
        {
            ChatUtils.sendMessage(sender, "Improper syntax. Please try this instead: <player> <['amount'x]item[:'meta']> <command [args]>");
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return null;
    }

    @Override
    public boolean canPlayerUseCommand(EntityPlayer player)
    {
        return false;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/sellcommand <player> <['amount'x]item[:'meta']> <command [args]>";
    }

    @Override
    public RegGroup getReggroup()
    {

        return null;
    }

}
