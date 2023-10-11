package com.forgeessentials.jscripting.fewrapper.fe.command;

import com.forgeessentials.core.commands.registration.FECommandParsingException;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

public enum JsArgumentType {
	BOOLEAN(),
	DOUBLE(),
	//DOUBLEMIN(),
	//DOUBLEMINMAX(),
	FLOAT(),
	//FLOATMIN(),
	//FLOATMINMAX(),
	INTEGER(),
	//INTEGERMIN(),
	//INTEGERMINMAX(),
	LONG(),
	//LONGMIN(),
	//LONGMINMAX(),
	STRINGWORD(),
	STRINGQUOTE(),
	STRINGGREEDY();
	public static ArgumentType<?> getType(JsArgumentType type) throws FECommandParsingException {
		if(type==BOOLEAN) {
			return (ArgumentType<Boolean>) BoolArgumentType.bool();
		}
		if(type==DOUBLE) {
			return (ArgumentType<Double>) DoubleArgumentType.doubleArg();
		}
		if(type==FLOAT) {
			return (ArgumentType<Float>) FloatArgumentType.floatArg();
		}
		if(type==INTEGER) {
			return (ArgumentType<Integer>) IntegerArgumentType.integer();
		}
		if(type==LONG) {
			return (ArgumentType<Long>) LongArgumentType.longArg();
		}
		if(type==STRINGWORD) {
			return (ArgumentType<String>) StringArgumentType.word();
		}
		if(type==STRINGQUOTE) {
			return (ArgumentType<String>) StringArgumentType.string();
		}
		if(type==STRINGGREEDY) {
			return (ArgumentType<String>) StringArgumentType.greedyString();
		}
		throw new FECommandParsingException("No such JsArgumentType: "+type.toString());
	}
}
