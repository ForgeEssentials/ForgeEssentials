package com.forgeessentials.protection.commands;

import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.util.CommandParserArgs;
import com.mojang.authlib.GameProfile;

public class CommandPlaceblock extends ParserCommandBase
{

    public static final UUID uuid = UUID.fromString("12345678-e153-3f49-84a8-b60ca564e69e");

    public static final GameProfile gp = new GameProfile(uuid, "fakeplayer");

    @Override
    public String getCommandName()
    {
        return "placeblock";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/placeblock";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.commands.placeblock";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public void parse(CommandParserArgs arguments)
    {
        // EntityPlayerMP player = FakePlayerFactory.get((WorldServer) arguments.senderPlayer.worldObj, gp);
        EntityPlayerMP player = FakePlayerFactory.get((WorldServer) arguments.senderPlayer.worldObj, new GameProfile(arguments.senderPlayer.getPersistentID(),
                arguments.senderPlayer.getCommandSenderName()));
        ForgeEventFactory.onPlayerInteract(player, Action.LEFT_CLICK_BLOCK, -192, 4, -640, 0, arguments.senderPlayer.worldObj);
    }
}
