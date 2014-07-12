package com.forgeessentials.permissions;

import com.forgeessentials.util.FunctionHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Set;

public class PermissionsList {
    private static final String OUTPUT_FILE = "PermissionsList.txt";
    private File output;

    public PermissionsList()
    {
        output = new File(ModulePermissions.permsFolder, OUTPUT_FILE);
        if (output.exists())
        {
            output.delete();
        }
    }

    public boolean shouldMake()
    {
        return !output.exists();
    }

    public void output(Set<String> permissions)
    {
        int permsize = permissions.size();
        try
        {
            output.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(output));

            writer.write("#// ------------ PERMISSIONS LIST ------------ \\\\#");
            writer.newLine();
            writer.write("#// --------------- " + FunctionHelper.getCurrentDateString() + " --------------- \\\\#");
            writer.newLine();
            writer.write("#// ------------ Total amount: " + permsize + " ------------ \\\\#");
            writer.newLine();
            writer.write("#// ------------------------------------------ \\\\#");
            writer.newLine();
            writer.newLine();

            for (String perm : permissions)
            {
                writer.write(perm);
                writer.newLine();
            }

            writer.close();
        }
        catch (Exception e)
        {

        }
    }
}
