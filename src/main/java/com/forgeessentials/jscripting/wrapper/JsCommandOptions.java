package com.forgeessentials.jscripting.wrapper;

public class JsCommandOptions
{

    public String name;

    public String usage; // $optional

    public String permission; // $optional

    public boolean opOnly = true; // $optional

    public Object processCommand; // $type=CommandCallback$

    public Object tabComplete; // $optional $type=CommandCallback$

}
