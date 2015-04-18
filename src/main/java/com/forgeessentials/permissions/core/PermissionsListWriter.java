package com.forgeessentials.permissions.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.RootZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.permissions.Zone.PermissionList;
import com.forgeessentials.util.FunctionHelper;

public final class PermissionsListWriter {

    private static final String NEW_LINE = System.getProperty("line.separator");

    public static void write(RootZone rootZone, File output)
    {
        PermissionList defaultPerms = rootZone.getGroupPermissions(Zone.GROUP_DEFAULT);
        PermissionList opPerms = rootZone.getGroupPermissions(Zone.GROUP_OPERATORS);
        
        TreeMap<String, String> permissions = new TreeMap<>(opPerms);
        permissions.putAll(defaultPerms);

        int permCount = 0;
        int permNameLength = 0;
        for (String perm : permissions.keySet())
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
    
                for (Entry<String, String> permission : permissions.entrySet())
                {
                    String perm = permission.getKey();
                    if (perm.endsWith(FEPermissions.DESCRIPTION_PROPERTY))
                    {
                        perm = perm.substring(0, perm.length() - FEPermissions.DESCRIPTION_PROPERTY.length());
                        String value = permissions.get(perm);
                        if (value == null)
                        {
                            StringBuffer sb = new StringBuffer();
                            sb.append(perm);
                            for (int i = perm.length(); i <= permNameLength; i++)
                                sb.append(' ');
                            sb.append("# ");
                            sb.append(permission.getValue());
                            sb.append(NEW_LINE);
                            writer.write(sb.toString());
                        }
                    }
                    else
                    {
                        String description = permissions.get(perm + FEPermissions.DESCRIPTION_PROPERTY);
                        String value = permission.getValue();
                        String opValue = opPerms.get(perm);
                        StringBuffer sb = new StringBuffer();
                        sb.append(perm);
                        for (int i = perm.length(); i <= permNameLength; i++)
                            sb.append(' ');
                        sb.append("# ");
                        if (opValue != null)
                            sb.append("(OP only: " + opValue + ") ");
                        else
                            sb.append("(default: " + value + ") ");
                        if (description != null)
                            sb.append(description);
                        sb.append(NEW_LINE);
                        writer.write(sb.toString());
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
