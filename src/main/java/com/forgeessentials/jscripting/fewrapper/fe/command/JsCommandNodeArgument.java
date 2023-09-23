package com.forgeessentials.jscripting.fewrapper.fe.command;

public class JsCommandNodeArgument// extends JsCommandNode
{
	public Boolean executesMethod = false;

	/**
     * @tsd.optional
     */
	public String executionParams;
	public String argumentName;

	/**
	 * @tsd.type JsArgumentType
	 */
	public String argumentType;
}
