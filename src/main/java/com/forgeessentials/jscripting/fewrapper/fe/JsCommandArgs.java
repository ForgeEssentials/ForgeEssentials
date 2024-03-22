package com.forgeessentials.jscripting.fewrapper.fe;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.item.ItemArgument;

import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.jscripting.wrapper.mc.JsCommandSource;
import com.forgeessentials.jscripting.wrapper.mc.entity.JsPlayerEntity;
import com.forgeessentials.jscripting.wrapper.mc.item.JsItem;
import com.forgeessentials.jscripting.wrapper.mc.world.JsBlock;
import com.forgeessentials.jscripting.wrapper.mc.world.JsServerWorld;
import com.forgeessentials.util.CommandContextParcer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class JsCommandArgs extends JsWrapper<CommandContextParcer> {

	public final JsCommandSource sender;

	public final JsPlayerEntity player;

	public final JsUserIdent ident;

	public final CommandContext<CommandSourceStack> context;

	public final String params;

	public JsCommandArgs(CommandContextParcer that) {
		super(that);
		this.params = that.methodParms;
		this.context = that.context;
		this.sender = JsCommandSource.get(that.sender);
		this.player = that.senderPlayer == null ? null : JsPlayerEntity.get(that.senderPlayer);
		this.ident = that.ident == null ? null : new JsUserIdent(that.ident);
	}

	public boolean hasPlayer() {
		return that.hasPlayer();
	}

	public void confirm(String message, Object... args) {
		that.confirm(message, args);
	}

	public void notify(String message, Object... args) {
		that.notify(message, args);
	}

	public void warn(String message, Object... args) {
		that.warn(message, args);
	}

	public void error(String message, Object... args) {
		that.error(message, args);
	}

	public JsUserIdent parsePlayer(String name) throws FECommandParsingException {
		return new JsUserIdent(that.parsePlayer(name, true, false));
	}

	public JsUserIdent parsePlayer(String name, boolean mustExist) throws FECommandParsingException {
		return new JsUserIdent(that.parsePlayer(name, mustExist, false));
	}

	public JsUserIdent parsePlayer(String name, boolean mustExist, boolean mustBeOnline)
			throws FECommandParsingException {
		return new JsUserIdent(that.parsePlayer(name, mustExist, mustBeOnline));
	}

	public JsItem parseItem(String argumentName){
		return JsItem.get(ItemArgument.getItem(context, argumentName).getItem());
	}

	public JsBlock parseBlock(String argumentName){
		return JsBlock.get(BlockStateArgument.getBlock(context, argumentName).getState().getBlock());
	}

	public JsServerWorld parseWorld(String argumentName) throws FECommandParsingException, CommandSyntaxException {
		return new JsServerWorld(DimensionArgument.getDimension(context, argumentName));
	}

	public boolean hasPermission(String perm) {
		return that.hasPermission(perm);
	}

	public long parseTimeReadable(String time) throws FECommandParsingException {
		return that.parseTimeReadable(time);
	}

	public JsWorldPoint<?> getSenderPoint() {
		return new JsWorldPoint<>(that.getSenderPoint());
	}

	// TODO: Add permissions to scripting // public JsWorldZone getWorldZone() // {
	// // return that.getWorldZone(); // }

	public void needsPlayer() throws FECommandParsingException {
		that.needsPlayer();
	}

	public boolean getArgumentBoolean(String argumentName) throws FECommandParsingException {
    	return that.getArgumentBoolean(argumentName);
    }

	public double getArgumentDouble(String argumentName) throws FECommandParsingException {
    	return that.getArgumentDouble(argumentName);
    }

	public float getArgumentFloat(String argumentName) throws FECommandParsingException {
    	return that.getArgumentFloat(argumentName);
    }

	public int getArgumentInteger(String argumentName) throws FECommandParsingException {
    	return that.getArgumentInteger(argumentName);
    }

	public long getArgumentLong(String argumentName) throws FECommandParsingException {
    	return that.getArgumentLong(argumentName);
    }

	public String getArgumentString(String argumentName) throws FECommandParsingException {
    	return that.getArgumentString(argumentName);
    }
}
