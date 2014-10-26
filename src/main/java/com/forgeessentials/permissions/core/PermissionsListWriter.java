package com.forgeessentials.permissions.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
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
        int permNameLength = 0;
        for (String perm : sortedPerms)
            if (!perm.endsWith(FEPermissions.DESCRIPTION_PROPERTY))
                permCount++;
            else
                permNameLength = Math.max(permNameLength, perm.length());
        permNameLength += 6;

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

            int lastPermLength = 0;
            for (String perm : sortedPerms)
            {
                String value = permissions.get(perm);
                if (value == null)
                    value = "";
                if (perm.endsWith(FEPermissions.DESCRIPTION_PROPERTY))
                {
                    StringBuffer sb = new StringBuffer();
                    if (permissions.containsKey(perm.substring(0, perm.length() - FEPermissions.DESCRIPTION_PROPERTY.length()))) {
                        for (; lastPermLength <= permNameLength; lastPermLength++)
                            sb.append(' ');
                    }
                    sb.append("# ");
                    sb.append(value);
                    writer.write(sb.toString());
                }
                else
                {
                    writer.newLine();
                    writer.write(perm);
                    lastPermLength = perm.length();
                }
            }
            writer.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
