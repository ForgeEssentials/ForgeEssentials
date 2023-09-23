package com.forgeessentials.jscripting.wrapper.mc.command;

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
	public ArgumentType<?> getType(JsArgumentType type) throws FECommandParsingException {
		if(type==BOOLEAN) {
			return BoolArgumentType.bool();
		}
		if(type==DOUBLE) {
			return DoubleArgumentType.doubleArg();
		}
		if(type==FLOAT) {
			return FloatArgumentType.floatArg();
		}
		if(type==INTEGER) {
			return IntegerArgumentType.integer();
		}
		if(type==LONG) {
			return LongArgumentType.longArg();
		}
		if(type==STRINGWORD) {
			return StringArgumentType.word();
		}
		if(type==STRINGQUOTE) {
			return StringArgumentType.string();
		}
		if(type==STRINGGREEDY) {
			return StringArgumentType.greedyString();
		}
		throw new FECommandParsingException("No such JsArgumentType: "+type.toString());
	}
}
