package com.forgeessentials.jscripting.fewrapper.fe.command;

import java.util.List;

public class JsCommandNodeWrapper
{
    /**
     * Don't EVER USE THIS! INTERNAL USE ONLY! <br>
     * @tsd.optional
     */
    public List<JsCommandNodeWrapper> listsChildNodes;

    /**
     * Don't implement this directly, instead use childNode[anyLetterNumber] (ex. childNode4, childNodeXYZ)
     * @tsd.optional 
     */
    public Object childNode;

	/**
	 * @tsd.type JsNodeType
	 */
	public String type;

	/**
	 * @tsd.type JsCommandNodeLiteral/JsCommandNodeArgument
	 */
	public Object containedNode;
}
