package com.forgeessentials.api.permissions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.forgeessentials.util.ServerUtil;

/**
 * Used to sort groups by priority and ancestors
 */
public class GroupEntry implements Comparable<GroupEntry>
{

    private final String group;

    private final int priority;

    private final int originalPriority;

    public GroupEntry(String group, int priority, int originalPriority)
    {
        this.group = group;
        this.priority = priority;
        this.originalPriority = originalPriority;
    }

    public GroupEntry(String group, int priority)
    {
        this(group, priority, Integer.MAX_VALUE);
    }

    public GroupEntry(ServerZone zone, String group)
    {
        this(group, ServerUtil.parseIntDefault(zone.getGroupPermission(group, FEPermissions.GROUP_PRIORITY), FEPermissions.GROUP_PRIORITY_DEFAULT));
    }

    public GroupEntry(ServerZone zone, String group, int priority)
    {
        this(group, priority, ServerUtil.parseIntDefault(zone.getGroupPermission(group, FEPermissions.GROUP_PRIORITY), FEPermissions.GROUP_PRIORITY_DEFAULT));
    }

    public String getGroup()
    {
        return group;
    }

    public int getPriority()
    {
        return priority;
    }

    @Override
    public String toString()
    {
        return group;
    }

    @Override
    public int compareTo(GroupEntry o)
    {
        int c = -Integer.compare(priority, o.priority);
        if (c != 0)
            return c;
        c = -Integer.compare(originalPriority, o.originalPriority);
        if (c != 0)
            return c;
        return group.compareTo(o.group);
    }

    public static List<String> toList(Collection<GroupEntry> entries)
    {
        final List<String> result = new ArrayList<>(entries.size());
        for (GroupEntry entry : entries)
            result.add(entry.group);
        return result;
    }

}