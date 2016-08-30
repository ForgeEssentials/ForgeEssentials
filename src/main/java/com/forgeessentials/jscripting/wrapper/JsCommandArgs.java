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

    public UserIdent parsePlayer() throws CommandException
    {
        return that.parsePlayer(true, false);
    }

    public UserIdent parsePlayer(boolean mustExist) throws CommandException
    {
        return that.parsePlayer(mustExist, false);
    }

    public UserIdent parsePlayer(boolean mustExist, boolean mustBeOnline) throws CommandException
    {
        return that.parsePlayer(mustExist, mustBeOnline);
    }

    public Item parseItem() throws CommandException
    {
        return that.parseItem();
    }

    public Block parseBlock() throws CommandException
    {
        return that.parseBlock();
    }

    public String parsePermission() throws CommandException
    {
        return that.parsePermission();
    }

    public void checkPermission(String perm) throws CommandException
    {
        that.checkPermission(perm);
    }

    public boolean hasPermission(String perm) throws CommandException
    {
        return that.hasPermission(perm);
    }

    public void tabComplete(String... completionList) throws CommandException
    {
        that.tabComplete(completionList);
    }

    /**
     * @tsd.ignore
     */
    public void tabComplete(Collection<String> completionList) throws CommandException
    {
        that.tabComplete(completionList);
    }

    public void tabCompleteWord(String completion)
    {
        that.tabCompleteWord(completion);
    }

    public WorldServer parseWorld() throws CommandException
    {
        return that.parseWorld();
    }

    public int parseInt() throws CommandException
    {
        return that.parseInt();
    }

    public int parseInt(int min, int max) throws CommandException
    {
        return that.parseInt(min, max);
    }

    public long parseLong() throws CommandException
    {
        return that.parseLong();
    }

    public double parseDouble() throws CommandException
    {
        return that.parseDouble();
    }

    public boolean parseBoolean() throws CommandException
    {
        return that.parseBoolean();
    }

    public long parseTimeReadable() throws CommandException
    {
        return that.parseTimeReadable();
    }

    public void checkTabCompletion() throws CommandException
    {
        that.checkTabCompletion();
    }

    public void requirePlayer() throws CommandException
    {
        that.requirePlayer();
    }

    public WorldPoint getSenderPoint() throws CommandException
    {
        return that.getSenderPoint();
    }

    public WorldZone getWorldZone() throws CommandException
    {
        return that.getWorldZone();
    }

    public void needsPlayer() throws CommandException
    {
        that.needsPlayer();
    }

}
