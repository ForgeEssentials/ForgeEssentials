package com.forgeessentials.jscripting.wrapper;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.item.Item;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldServer;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.CommandParserArgs;

public class JsCommandArgs
{

    private CommandParserArgs that;

    public final JsCommandSender sender;

    public final JsEntityPlayer player;

    public final JsUserIdent ident;

    public final boolean isTabCompletion;

    public JsCommandArgs(CommandParserArgs args)
    {
        this.that = args;
        this.sender = new JsCommandSender(args.sender);
        this.player = args.senderPlayer == null ? null : new JsEntityPlayer(args.senderPlayer);
        this.ident = args.ident == null ? null : new JsUserIdent(args.ident);
        this.isTabCompletion = args.isTabCompletion;
    }

    public String[] toArray()
    {
        return that.toArray();
    }

    @Override
    public String toString()
    {
        return that.toString();
    }

    public void sendMessage(IChatComponent message)
    {
        that.sendMessage(message);
    }

    public void confirm(String message, Object... args)
    {
        that.confirm(message, args);
    }

    public void notify(String message, Object... args)
    {
        that.notify(message, args);
    }

    public void warn(String message, Object... args)
    {
        that.warn(message, args);
    }

    public void error(String message, Object... args)
    {
        that.error(message, args);
    }

    public int size()
    {
        return that.size();
    }

    public String remove()
    {
        return that.remove();
    }

    public String peek()
    {
        return that.peek();
    }

    public boolean isEmpty()
    {
        return that.isEmpty();
    }

    public boolean hasPlayer()
    {
        return that.hasPlayer();
    }

    public UserIdent parsePlayer()
    {
        return that.parsePlayer(true, false);
    }

    public UserIdent parsePlayer(boolean mustExist)
    {
        return that.parsePlayer(mustExist, false);
    }

    public UserIdent parsePlayer(boolean mustExist, boolean mustBeOnline)
    {
        return that.parsePlayer(mustExist, mustBeOnline);
    }

    public Item parseItem()
    {
        return that.parseItem();
    }

    public Block parseBlock()
    {
        return that.parseBlock();
    }

    public String parsePermission()
    {
        return that.parsePermission();
    }

    public void checkPermission(String perm)
    {
        that.checkPermission(perm);
    }

    public boolean hasPermission(String perm)
    {
        return that.hasPermission(perm);
    }

    public void tabComplete(String... completionList)
    {
        that.tabComplete(completionList);
    }

    public void tabComplete(Collection<String> completionList) // tsgen ignore
    {
        that.tabComplete(completionList);
    }

    public void tabCompleteWord(String completion)
    {
        that.tabCompleteWord(completion);
    }

    public WorldServer parseWorld()
    {
        return that.parseWorld();
    }

    public int parseInt()
    {
        return that.parseInt();
    }

    public int parseInt(int min, int max) throws CommandException
    {
        return that.parseInt(min, max);
    }

    public long parseLong()
    {
        return that.parseLong();
    }

    public double parseDouble()
    {
        return that.parseDouble();
    }

    public boolean parseBoolean()
    {
        return that.parseBoolean();
    }

    public long parseTimeReadable()
    {
        return that.parseTimeReadable();
    }

    public void checkTabCompletion()
    {
        that.checkTabCompletion();
    }

    public void requirePlayer()
    {
        that.requirePlayer();
    }

    public WorldPoint getSenderPoint()
    {
        return that.getSenderPoint();
    }

    public WorldZone getWorldZone()
    {
        return that.getWorldZone();
    }

    public void needsPlayer()
    {
        that.needsPlayer();
    }

}
