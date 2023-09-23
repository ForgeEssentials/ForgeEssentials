package com.forgeessentials.jscripting.fewrapper.fe.command;

public class JsCommandNodeWrapper
{
    /**
     * @tsd.optional
     * @tsd.type JsCommandTypeWrapper
     */
    public Object[] childTree;

	/**
	 * @tsd.type JsNodeType
	 */
	public String type;

	/**
	 * @tsd.type JsCommandNodeLiteral/JsCommandNodeArgument
	 */
	public Object containedNode;
}
