package com.forgeessentials.util;

//Depreciated

import java.util.ArrayList;

public class BackupArea
{
    public ArrayList<BlockSaveable> before;
    public ArrayList<BlockSaveable> after;

    public BackupArea()
    {
        before = new ArrayList<BlockSaveable>();
        after = new ArrayList<BlockSaveable>();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof BackupArea)
        {
            return before.equals(((BackupArea) obj).before);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return (11 + before.hashCode()) * 29 + after.hashCode();
    }

}
