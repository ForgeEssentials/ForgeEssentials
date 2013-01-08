package com.ForgeEssentials.chat.commands;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.permission.query.PermQueryPlayer;
import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandCensor extends ForgeEssentialsCommandBase
{

private List<String> aliasList;

public CommandCensor()
{
super();
aliasList = new LinkedList<String>();
aliasList.add("banword");
aliasList.add("badword");
}

@Override
public String getCommandName()
{
return "msg";
}

@Override
public List getCommandAliases()
{
return aliasList;
}

@Override
public void processCommandPlayer(EntityPlayer sender, String[] args)
{
  for (int i = 1; i < args.length; i++){
    Chat.bannedWords.add(args[i]);
  }
  return;
}

@Override
public void processCommandConsole(ICommandSender sender, String[] args)
{
  for (int i = 1; i < args.length; i++){
    Chat.bannedWords.add(args[i]);
  }
  return;
}

@Override
public boolean canConsoleUseCommand()
{
return true;
}

@Override
public boolean canPlayerUseCommand(EntityPlayer player)
{
return PermissionsAPI.checkPermAllowed(new PermQueryPlayer(player, getCommandPerm()));
}

@Override
public String getCommandPerm()
{
return "ForgeEssentials.Chat.commands." + getCommandName();
}

}
