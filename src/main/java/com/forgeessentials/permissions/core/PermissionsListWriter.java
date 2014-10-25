package com.forgeessentials.permissions.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.TreeSet;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.Zone.PermissionList;
import com.forgeessentials.permissions.ModulePermissions;
import com.forgeessentials.util.FunctionHelper;

public class PermissionsListWriter {

    private static final String OUTPUT_FILE = "PermissionsList.txt";

    private File output;

    public PermissionsListWriter()
    {
        output = new File(ModulePermissions.moduleFolder, OUTPUT_FILE);
        if (output.exists())
        {
            output.delete();
        }
    }

    public void write(PermissionList permissions)
    {
        TreeSet<String> sortedPerms = new TreeSet<String>(permissions.keySet());

        int permCount = 0;
        for (String perm : sortedPerms)
            if (!perm.endsWith(FEPermissions.DESCRIPTION_PROPERTY))
                permCount++;

        try
        {
            output.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(output));
            writer.write("#// ------------ PERMISSIONS LIST ------------ \\\\#");
            writer.newLine();
            writer.write("#// --------------- " + FunctionHelper.getCurrentDateString() + " --------------- \\\\#");
            writer.newLine();
            writer.write("#// ------------ Total amount: " + permCount + " ------------ \\\\#");
            writer.newLine();
            writer.write("#// ------------------------------------------ \\\\#");
            writer.newLine();
            writer.newLine();

            for (String perm : sortedPerms)
            {
                String value = permissions.get(perm);
                if (value == null)
                    value = "";
                if (perm.endsWith(FEPermissions.DESCRIPTION_PROPERTY))
                {
                    writer.write("# " + value);
                    writer.newLine();
                }
//                if (value.equals(IPermissionsHelper.PERMISSION_TRUE))
//                    writer.write("+");
//                else if (value.equals(IPermissionsHelper.PERMISSION_FALSE))
//                    writer.write("-");
                writer.write(perm);
                writer.newLine();
            }
            writer.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
