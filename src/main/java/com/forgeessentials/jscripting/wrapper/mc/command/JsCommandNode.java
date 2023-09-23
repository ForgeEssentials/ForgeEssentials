package com.forgeessentials.jscripting.wrapper.mc.command;

public class JsCommandNode
{
	public boolean insertExecution;
	public String executionParams;
    /**
     * @tsd.optional
     * @tsd.type JsCommandTypeWrapper
     */
    public Object[] childCommandTree;
}
