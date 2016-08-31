package com.forgeessentials.jscripting.wrapper;

import java.util.Collection;

import net.minecraft.command.CommandException;
import net.minecraft.util.IChatComponent;

import com.forgeessentials.jscripting.wrapper.entity.JsEntityPlayer;
import com.forgeessentials.jscripting.wrapper.item.JsItem;
import com.forgeessentials.jscripting.wrapper.server.JsUserIdent;
import com.forgeessentials.jscripting.wrapper.world.JsBlock;
import com.forgeessentials.jscripting.wrapper.world.JsWorldPoint;
import com.forgeessentials.jscripting.wrapper.world.JsWorldServer;
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

    public String get(int index)
    {
        return that.get(index);
    }

    public boolean isEmpty()
    {
        return that.isEmpty();
    }

    public boolean hasPlayer()
    {
        return that.hasPlayer();
    }

    public JsUserIdent parsePlayer() throws CommandException
    {
        return new JsUserIdent(that.parsePlayer(true, false));
    }

    public JsUserIdent parsePlayer(boolean mustExist) throws CommandException
    {
        return new JsUserIdent(that.parsePlayer(mustExist, false));
    }

    public JsUserIdent parsePlayer(boolean mustExist, boolean mustBeOnline) throws CommandException
    {
        return new JsUserIdent(that.parsePlayer(mustExist, mustBeOnline));
    }

    public JsItem parseItem() throws CommandException
    {
        return JsItem.get(that.parseItem());
    }

    public JsBlock parseBlock() throws CommandException
    {
        return JsBlock.get(that.parseBlock());
    }

    public String parsePermission() throws CommandException
    {
        return that.parsePermission();
    }

    public void checkPermission(String perm) throws CommandException
    {
        that.checkPermission(perm);
    }

    public boolean hasPermission(String perm)
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

    public void tabCompleteWord(String completion) throws CommandException
    {
        that.tabCompleteWord(completion);
    }

    public JsWorldServer parseWorld() throws CommandException
    {
        return new JsWorldServer(that.parseWorld());
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

    public JsWorldPoint<?> getSenderPoint()
    {
        return new JsWorldPoint<>(that.getSenderPoint());
    }

    // TODO: Add permissions to scripting
    // public JsWorldZone getWorldZone()
    // {
    // return that.getWorldZone();
    // }

    public void needsPlayer() throws CommandException
    {
        that.needsPlayer();
    }

}
