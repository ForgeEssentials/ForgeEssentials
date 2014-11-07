package com.forgeessentials.permissions.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.TreeSet;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.Zone.PermissionList;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.FunctionHelper;

public class PermissionsListWriter {

    private static final String OUTPUT_FILE = "PermissionsList.txt";

    private File output;
    
    private static final String NEW_LINE = System.getProperty("line.separator");

    public PermissionsListWriter()
    {
        output = new File(ForgeEssentials.getFEDirectory(), OUTPUT_FILE);
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
            {
                permCount++;
                permNameLength = Math.max(permNameLength, perm.length());
            }
        permNameLength += 2;

        try
        {
            output.createNewFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(output)))
            {
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
                        String parentPerm = perm.substring(0, perm.length() - FEPermissions.DESCRIPTION_PROPERTY.length());
                        if (!permissions.containsKey(parentPerm)) {
                            sb.append(NEW_LINE);
                            sb.append(parentPerm);
                            lastPermLength = parentPerm.length();
                        }
                        for (; lastPermLength <= permNameLength; lastPermLength++)
                            sb.append(' ');
                        sb.append("# ");
                        sb.append(value);
                        writer.write(sb.toString());
                    }
                    else if (perm.endsWith(IPermissionsHelper.ALL_PERMS))
                    {
                        String parentPerm = perm.substring(0, perm.length() - IPermissionsHelper.PERMISSION_ASTERIX.length() - 1);
                        if (!permissions.containsKey(parentPerm)) {
                            writer.newLine();
                            writer.write(parentPerm);
                            lastPermLength = parentPerm.length();
                        }
                    }
                    else
                    {
                        writer.newLine();
                        writer.write(perm);
                        lastPermLength = perm.length();
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
