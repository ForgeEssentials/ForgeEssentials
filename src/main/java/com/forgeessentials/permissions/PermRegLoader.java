package com.forgeessentials.permissions;

import com.forgeessentials.api.permissions.IPermRegHelper;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.util.OutputHandler;
import com.google.common.collect.HashMultimap;

import java.util.HashSet;
import java.util.TreeSet;

public class PermRegLoader implements IPermRegHelper {
    protected HashSet<String> mods;
    protected TreeSet<String> perms;
    protected HashMultimap<RegGroup, PermissionChecker> registerredPerms;

    protected PermRegLoader()
    {
        OutputHandler.felog.fine("PermRegLoader listening.");
        perms = new TreeSet<String>();
        registerredPerms = HashMultimap.create();

    }

        @Override
        public void registerPermissionLevel(String permission, RegGroup group)
        {
            registerPermissionLevel(permission, group, false);
        }

        @Override
        public void registerPermission(String permission)
        {
            perms.add(permission.toLowerCase());
        }

        @Override
        public void registerPermissionLevel(String permission, RegGroup group, boolean alone)
        {
            Permission deny;
            Permission allow;

            deny = new Permission(permission.toLowerCase(), false);
            allow = new Permission(permission.toLowerCase(), true);


            if (!deny.isAll)
            {
                perms.add(permission.toLowerCase());
            }

            if (group == null)
            {
                registerredPerms.put(RegGroup.ZONE, deny);
            }
            else if (group == RegGroup.ZONE)
            {
                registerredPerms.put(RegGroup.ZONE, allow);
            }
            else
            {
                registerredPerms.put(group, allow);

                if (alone)
                {
                    return;
                }

                for (RegGroup g : getHigherGroups(group))
                {
                    registerredPerms.put(g, allow);
                }

                for (RegGroup g : getLowerGroups(group))
                {
                    registerredPerms.put(g, deny);
                }
            }
        }

        private RegGroup[] getHigherGroups(RegGroup g)
        {
            switch (g)
            {
            case GUESTS:
                return new RegGroup[]
                        { RegGroup.MEMBERS, RegGroup.ZONE_ADMINS, RegGroup.OWNERS };
            case MEMBERS:
                return new RegGroup[]
                        { RegGroup.ZONE_ADMINS, RegGroup.OWNERS };
            case ZONE_ADMINS:
                return new RegGroup[]
                        { RegGroup.OWNERS };
            default:
                return new RegGroup[] { };
            }
        }

        private RegGroup[] getLowerGroups(RegGroup g)
        {
            switch (g)
            {
            case MEMBERS:
                return new RegGroup[]
                        { RegGroup.GUESTS };
            case ZONE_ADMINS:
                return new RegGroup[]
                        { RegGroup.MEMBERS, RegGroup.GUESTS };
            case OWNERS:
                return new RegGroup[]
                        { RegGroup.MEMBERS, RegGroup.GUESTS, RegGroup.ZONE_ADMINS };
            default:
                return new RegGroup[] { };
            }
        }

        @Override
        public void registerPermissionProp(String permission, String globalDefault)
        {
            PermissionProp prop = new PermissionProp(permission.toLowerCase(), globalDefault);
            registerredPerms.put(RegGroup.ZONE, prop);
        }

        @Override
        public void registerPermissionProp(String permission, int globalDefault)
        {
            PermissionProp prop = new PermissionProp(permission.toLowerCase(), "" + globalDefault);
            registerredPerms.put(RegGroup.ZONE, prop);
        }

        @Override
        public void registerPermissionProp(String permission, float globalDefault)
        {
            PermissionProp prop = new PermissionProp(permission.toLowerCase(), "" + globalDefault);
            registerredPerms.put(RegGroup.ZONE, prop);
        }

        @Override
        public void registerGroupPermissionprop(String permission, String value, RegGroup group)
        {
            PermissionProp prop = new PermissionProp(permission.toLowerCase(), "" + value);
            registerredPerms.put(group, prop);
        }

        @Override
        public void registerGroupPermissionprop(String permission, int value, RegGroup group)
        {
            PermissionProp prop = new PermissionProp(permission.toLowerCase(), "" + value);
            registerredPerms.put(group, prop);
        }

        @Override
        public void registerGroupPermissionprop(String permission, float value, RegGroup group)
        {
            PermissionProp prop = new PermissionProp(permission.toLowerCase(), "" + value);
            registerredPerms.put(group, prop);
        }

    public HashMultimap getRegisteredPerms()
    {
        OutputHandler.felog.info("Registered " + perms.size() + " permission nodes");
        PermissionsList list = new PermissionsList();
        if (list.shouldMake())
        {
            list.output(perms);
        }

        return registerredPerms;
    }

}
