package com.forgeessentials.permissions;

import com.forgeessentials.api.permissions.IPermRegisterEvent;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.moduleLauncher.CallableMap.FECallable;
import com.forgeessentials.util.OutputHandler;
import com.google.common.collect.HashMultimap;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class PermRegLoader {

    public static PermRegLoader INSTANCE;
    protected HashSet<String> mods;
    protected TreeSet<String> perms;
    protected HashMultimap<RegGroup, PermissionChecker> registerredPerms;
    private Set<FECallable> data;

    public PermRegLoader(Set<FECallable> calls)
    {
        mods = new HashSet<String>();
        data = calls;
        INSTANCE = this;
    }

    protected void loadAllPerms()
    {
        PermissionRegistrationEvent event = new PermissionRegistrationEvent();

        String modid;
        for (FECallable call : data)
        {
            modid = call.getIdent();
            mods.add(modid);

            try
            {
                call.call(event);
            }
            catch (Exception e)
            {
                OutputHandler.felog.severe("Error trying to load permissions from \"" + modid + "\"!");
                e.printStackTrace();
            }
        }

        perms = event.registerred;
        registerredPerms = event.perms;
        OutputHandler.felog.info("Registered " + event.registerred.size() + " permissions nodes");

        PermissionsList list = new PermissionsList();
        if (list.shouldMake())
        {
            list.output(event.registerred);
        }
    }

    protected void clearMethods()
    {
        data = null;
    }

    public TreeSet<String> getPerms()
    {
        return perms;
    }

    private class PermissionRegistrationEvent implements IPermRegisterEvent {
        private HashMultimap<RegGroup, PermissionChecker> perms;
        private TreeSet<String> registerred;

        private PermissionRegistrationEvent()
        {
            perms = HashMultimap.create();
            registerred = new TreeSet<String>();
        }

        @Override
        public void registerPermissionLevel(String permission, RegGroup group)
        {
            registerPermissionLevel(permission, group, false);
        }

        @Override
        public void registerPermission(String permission)
        {
            registerred.add(permission.toLowerCase());
        }

        @Override
        public void registerPermissionLevel(String permission, RegGroup group, boolean alone)
        {
            Permission deny = new Permission(permission.toLowerCase(), false);
            Permission allow = new Permission(permission.toLowerCase(), true);

            if (!deny.isAll)
            {
                registerred.add(permission.toLowerCase());
            }

            if (group == null)
            {
                perms.put(RegGroup.ZONE, deny);
            }
            else if (group == RegGroup.ZONE)
            {
                perms.put(RegGroup.ZONE, allow);
            }
            else
            {
                perms.put(group, allow);

                if (alone)
                {
                    return;
                }

                for (RegGroup g : getHigherGroups(group))
                {
                    perms.put(g, allow);
                }

                for (RegGroup g : getLowerGroups(group))
                {
                    perms.put(g, deny);
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
            perms.put(RegGroup.ZONE, prop);
        }

        @Override
        public void registerPermissionProp(String permission, int globalDefault)
        {
            PermissionProp prop = new PermissionProp(permission.toLowerCase(), "" + globalDefault);
            perms.put(RegGroup.ZONE, prop);
        }

        @Override
        public void registerPermissionProp(String permission, float globalDefault)
        {
            PermissionProp prop = new PermissionProp(permission.toLowerCase(), "" + globalDefault);
            perms.put(RegGroup.ZONE, prop);
        }

        @Override
        public void registerGroupPermissionprop(String permission, String value, RegGroup group)
        {
            PermissionProp prop = new PermissionProp(permission.toLowerCase(), "" + value);
            perms.put(group, prop);
        }

        @Override
        public void registerGroupPermissionprop(String permission, int value, RegGroup group)
        {
            PermissionProp prop = new PermissionProp(permission.toLowerCase(), "" + value);
            perms.put(group, prop);
        }

        @Override
        public void registerGroupPermissionprop(String permission, float value, RegGroup group)
        {
            PermissionProp prop = new PermissionProp(permission.toLowerCase(), "" + value);
            perms.put(group, prop);
        }
    }
}
